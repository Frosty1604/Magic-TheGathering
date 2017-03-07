package de.ys_solutions.magic_thegathering.util

/*
 * Copyright (c) 2014-present, Facebook, Inc.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
*/

import com.facebook.stetho.inspector.network.DefaultResponseHandler
import com.facebook.stetho.inspector.network.NetworkEventReporter
import com.facebook.stetho.inspector.network.NetworkEventReporterImpl
import com.facebook.stetho.inspector.network.RequestBodyHelper
import okhttp3.*
import okio.BufferedSource
import okio.Okio
import java.io.IOException
import java.io.InputStream
import java.util.concurrent.atomic.AtomicInteger

/**
 * Provides easy integration with [OkHttp](http://square.github.io/okhttp/) 3.x by way of
 * the new [Interceptor](https://github.com/square/okhttp/wiki/Interceptors) system. To
 * use:
 * <pre>
 * OkHttpClient client = new OkHttpClient.Builder()
 * .addNetworkInterceptor(new StethoInterceptor())
 * .build();
</pre> *
 */
class StethoInterceptor : Interceptor {
    private val mEventReporter = NetworkEventReporterImpl.get()

    private val mNextRequestId = AtomicInteger(0)

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val requestId = mNextRequestId.getAndIncrement().toString()

        val request = chain.request()

        var requestBodyHelper: RequestBodyHelper? = null
        if (mEventReporter.isEnabled) {
            requestBodyHelper = RequestBodyHelper(mEventReporter, requestId)
            val inspectorRequest = OkHttpInspectorRequest(requestId, request, requestBodyHelper)
            mEventReporter.requestWillBeSent(inspectorRequest)
        }

        var response: Response
        try {
            response = chain.proceed(request)
        } catch (e: IOException) {
            if (mEventReporter.isEnabled) {
                mEventReporter.httpExchangeFailed(requestId, e.toString())
            }
            throw e
        }

        if (mEventReporter.isEnabled) {
            if (requestBodyHelper != null && requestBodyHelper.hasBody()) {
                requestBodyHelper.reportDataSent()
            }

            val connection = chain.connection()
            mEventReporter.responseHeadersReceived(
                    OkHttpInspectorResponse(
                            requestId,
                            request,
                            response,
                            connection))

            val body = response.body()
            var contentType: MediaType? = null
            var responseStream: InputStream? = null
            if (body != null) {
                contentType = body.contentType()
                responseStream = body.byteStream()
            }

            responseStream = mEventReporter.interpretResponseStream(
                    requestId,
                    if (contentType != null) contentType.toString() else null,
                    response.header("Content-Encoding"),
                    responseStream,
                    DefaultResponseHandler(mEventReporter, requestId))
            if (responseStream != null) {
                response = response.newBuilder()
                        .body(ForwardingResponseBody(body, responseStream))
                        .build()
            }
        }

        return response
    }

    private class OkHttpInspectorRequest(
            private val mRequestId: String,
            private val mRequest: Request,
            private val mRequestBodyHelper: RequestBodyHelper) : NetworkEventReporter.InspectorRequest {

        override fun id(): String {
            return mRequestId
        }

        override fun friendlyName(): String? {
            // Hmm, can we do better?  tag() perhaps?
            return null
        }

        override fun friendlyNameExtra(): Int? {
            return null
        }

        override fun url(): String {
            return mRequest.url().toString()
        }

        override fun method(): String {
            return mRequest.method()
        }

        @Throws(IOException::class)
        override fun body(): ByteArray? {
            val body = mRequest.body() ?: return null
            val out = mRequestBodyHelper.createBodySink(firstHeaderValue("Content-Encoding"))
            val bufferedSink = Okio.buffer(Okio.sink(out))
            bufferedSink.use { bufferedSink ->
                body.writeTo(bufferedSink)
            }
            return mRequestBodyHelper.displayBody
        }

        override fun headerCount(): Int {
            return mRequest.headers().size()
        }

        override fun headerName(index: Int): String {
            return mRequest.headers().name(index)
        }

        override fun headerValue(index: Int): String {
            return mRequest.headers().value(index)
        }

        override fun firstHeaderValue(name: String): String? {
            return mRequest.header(name)
        }
    }

    private class OkHttpInspectorResponse(
            private val mRequestId: String,
            private val mRequest: Request,
            private val mResponse: Response,
            private val mConnection: Connection) : NetworkEventReporter.InspectorResponse {

        override fun requestId(): String {
            return mRequestId
        }

        override fun url(): String {
            return mRequest.url().toString()
        }

        override fun statusCode(): Int {
            return mResponse.code()
        }

        override fun reasonPhrase(): String {
            return mResponse.message()
        }

        override fun connectionReused(): Boolean {
            // Not sure...
            return false
        }

        override fun connectionId(): Int {
            return mConnection.hashCode()
        }

        override fun fromDiskCache(): Boolean {
            return mResponse.cacheResponse() != null
        }

        override fun headerCount(): Int {
            return mResponse.headers().size()
        }

        override fun headerName(index: Int): String {
            return mResponse.headers().name(index)
        }

        override fun headerValue(index: Int): String {
            return mResponse.headers().value(index)
        }

        override fun firstHeaderValue(name: String): String? {
            return mResponse.header(name)
        }
    }

    private class ForwardingResponseBody(private val mBody: ResponseBody, interceptedStream: InputStream) : ResponseBody() {
        private val mInterceptedSource: BufferedSource = Okio.buffer(Okio.source(interceptedStream))

        override fun contentType(): MediaType {
            return mBody.contentType()
        }

        override fun contentLength(): Long {
            return mBody.contentLength()
        }

        override fun source(): BufferedSource {
            // close on the delegating body will actually close this intercepted source, but it
            // was derived from mBody.byteStream() therefore the close will be forwarded all the
            // way to the original.
            return mInterceptedSource
        }
    }
}
