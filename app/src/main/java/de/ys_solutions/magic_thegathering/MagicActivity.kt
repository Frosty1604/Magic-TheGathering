package de.ys_solutions.magic_thegathering

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import com.squareup.picasso.Picasso
import de.ys_solutions.magic_thegathering.data.model.Card
import de.ys_solutions.magic_thegathering.data.source.CardsDataSource
import de.ys_solutions.magic_thegathering.data.source.CardsRepository
import org.jetbrains.anko.*
import org.jetbrains.anko.recyclerview.v7.recyclerView
import javax.inject.Inject

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class MagicActivity : AppCompatActivity() {
    @Inject
    lateinit var cardsRepository: CardsRepository

    @Inject
    lateinit var picasso: Picasso

    private val mHideHandler = Handler()
    private var mContentView: View? = null
    /*private val mHidePart2Runnable = Runnable {
        // at compile-time and do nothing on earlier devices.
        mContentView!!.systemUiVisibility = View.SYSTEM_UI_FLAG_LOW_PROFILE or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
    }*/
    private var mVisible: Boolean = false
    private val mHideRunnable = Runnable { hide() }

    private var cardAdapter: CardAdapter = CardAdapter()

    /**
     * Touch listener to use for in-
     * layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        (application as MagicApp).netComponent.inject(this)

        mVisible = true
        //mContentView = findViewById(R.id.fullscreen_content)

        MagicActivityUI(cardAdapter).setContentView(this)
        // Set up the user interaction to manually show or hide the system UI.
        //mContentView!!.setOnClickListener {
        //toggle();
        //}

        val queryParams: Map<String, String> = hashMapOf("page" to "0",
                "pageSize" to "100",
                "set" to "AER")

        cardsRepository.loadAllCards(queryParams, callback = object : CardsDataSource.LoadAllCardsCallback {
            override fun onCardsLoaded(cards: List<Card>) {
                if (cards.isEmpty()) return

                cardAdapter.cardList = cards
                cardAdapter.notifyDataSetChanged()
            }

            override fun onDataNotAvailable() {
                Toast.makeText(applicationContext, "Error loading cards", Toast.LENGTH_SHORT).show()
            }

        })


        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
    }

    class MagicActivityUI(val cardAdapter: CardAdapter) : AnkoComponent<MagicActivity> {
        override fun createView(ui: AnkoContext<MagicActivity>): View = with(ui) {
            return verticalLayout {
                recyclerView {
                    layoutManager = GridLayoutManager(context, 3)
                    adapter = cardAdapter
                    setHasFixedSize(true)

                }.lparams(width = matchParent, height = matchParent)
            }
        }

    }

    inner class CardAdapter(var cardList: List<Card> = ArrayList<Card>()) : RecyclerView.Adapter<CardAdapter.CardHolder>() {

        override fun onBindViewHolder(holder: CardHolder, position: Int) {
            val card = cardList[position]
            holder.bind(card)
        }

        override fun getItemCount(): Int {
            return cardList.size
        }

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): CardHolder {
            return CardHolder(CardViewItem().createView(AnkoContext.create(parent!!.context, parent)))
        }

        inner class CardViewItem : AnkoComponent<ViewGroup> {
            override fun createView(ui: AnkoContext<ViewGroup>): View {
                return with(ui) {
                    linearLayout {
                        lparams(width = matchParent, height = wrapContent) {
                            orientation = LinearLayout.VERTICAL
                            imageView {
                                id = R.id.image
                                scaleType = ImageView.ScaleType.FIT_CENTER
                                adjustViewBounds = true
                            }
                        }
                    }
                }
            }

        }

        inner class CardHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val image: ImageView = itemView.find(R.id.image)

            fun bind(card: Card) {
                image.loadImage(card.imageUrl)
            }
        }

    }

    fun ImageView.loadImage(imageUrl: String) {
        if (TextUtils.isEmpty(imageUrl)) {
            picasso.load(R.drawable.mtg_card_back).into(this)
        } else {
            picasso.load(imageUrl).into(this)
        }
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100)
    }

    private fun toggle() {
        if (mVisible) {
            hide()
        } else {
            show()
        }
    }

    private fun hide() {
        // Hide UI first
        supportActionBar?.hide()
        mVisible = false

        // Schedule a runnable to remove the status and navigation bar after a delay
        // mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY.toLong())
    }

    @SuppressLint("InlinedApi")
    private fun show() {
        // Show the system bar
        mContentView!!.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        mVisible = true

        // Schedule a runnable to display UI elements after a delay
        //mHideHandler.removeCallbacks(mHidePart2Runnable)
    }

    /**
     * Schedules a call to hide() in [delayMillis] milliseconds, canceling any
     * previously scheduled calls.
     */
    private fun delayedHide(delayMillis: Int) {
        mHideHandler.removeCallbacks(mHideRunnable)
        mHideHandler.postDelayed(mHideRunnable, delayMillis.toLong())
    }

    companion object {
        /**
         * Whether or not the system UI should be auto-hidden after
         * [.AUTO_HIDE_DELAY_MILLIS] milliseconds.
         */
        private val AUTO_HIDE = true

        /**
         * If [.AUTO_HIDE] is set, the number of milliseconds to wait after
         * user interaction before hiding the system UI.
         */
        private val AUTO_HIDE_DELAY_MILLIS = 1000

        /**
         * Some older devices needs a small delay between UI widget updates
         * and a change of the status and navigation bar.
         */
        private val UI_ANIMATION_DELAY = 300
    }
}
