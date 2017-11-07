package de.ys_solutions.magic_thegathering

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
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

  private var cardAdapter: CardAdapter = CardAdapter()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    requestWindowFeature(Window.FEATURE_NO_TITLE)
    window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        WindowManager.LayoutParams.FLAG_FULLSCREEN)

    (application as MagicApp).netComponent.inject(this)

    MagicActivityUI(cardAdapter).setContentView(this)

    val queryParams: Map<String, String> = hashMapOf("page" to "0",
        "pageSize" to "100",
        "set" to "AER")

    cardsRepository.loadAllCards(queryParams,
        callback = object : CardsDataSource.LoadAllCardsCallback {
          override fun onCardsLoaded(cards: List<Card>) {
            if (cards.isEmpty()) return

            cardAdapter.cardList = cards
            cardAdapter.notifyDataSetChanged()
          }

          override fun onDataNotAvailable() {
            Toast.makeText(applicationContext, "Error loading cards", Toast.LENGTH_SHORT).show()
          }

        })
  }

  private fun hideSystemUI() {
    // Set the IMMERSIVE flag.
    // Set the content to appear under the system bars so that the content
    // doesn't resize when the system bars hide and show.
    window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
        View.SYSTEM_UI_FLAG_FULLSCREEN or
        View.SYSTEM_UI_FLAG_IMMERSIVE
  }

  // This snippet shows the system bars. It does this by removing all the flags
  // except for the ones that make the content appear under the system bars.
  private fun showSystemUI() {
    window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
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

  inner class CardAdapter(
      var cardList: List<Card> = ArrayList<Card>()) : RecyclerView.Adapter<CardAdapter.CardHolder>() {

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

}