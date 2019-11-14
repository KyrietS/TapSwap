package mwo.lab.tapswap.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageSwitcher
import android.widget.ImageView
import android.widget.Toast
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_discover.*
import mwo.lab.tapswap.R
import mwo.lab.tapswap.adapters.ImageSwitcherPicasso
import mwo.lab.tapswap.api.APIService
import mwo.lab.tapswap.api.models.DiscoverItems
import mwo.lab.tapswap.api.models.Item
import mwo.lab.tapswap.views.LoadingView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.math.abs

class DiscoverActivity : AppCompatActivity() {

    // Views
    private lateinit var imageSwitcher: ImageSwitcher
    private lateinit var overscrollLeft: View
    private lateinit var overscrollRight: View

    private lateinit var imageSwitcherPicasso: ImageSwitcherPicasso
    private lateinit var gestureDetector: GestureDetector

    // Animations
    private var slideInLeft: Animation? = null

    private var slideOutRight: Animation? = null
    private var slideInRight: Animation? = null
    private var slideOutLeft: Animation? = null
    private var overscrollLeftFadeOut: Animation? = null
    private var overscrollRightFadeOut: Animation? = null

    // Mock items
    private val items = mutableListOf<Item>()

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_discover)

        // Fetch items as soon as possible
        fetchItems()

        // Views
        imageSwitcher = findViewById(R.id.image)
        overscrollLeft = findViewById(R.id.overscroll_left)
        overscrollRight = findViewById(R.id.overscroll_right)

        imageSwitcherPicasso = ImageSwitcherPicasso(this, imageSwitcher)

        // Animations
        slideInLeft = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left)
        slideOutRight = AnimationUtils.loadAnimation(this, android.R.anim.slide_out_right)
        slideInRight = AnimationUtils.loadAnimation(this, R.anim.slide_in_right)
        slideOutLeft = AnimationUtils.loadAnimation(this, R.anim.slide_out_left)
        overscrollLeftFadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out)
        overscrollRightFadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out)

        // ImageSwitcher
        imageSwitcher.setFactory {
            val view = ImageView(this@DiscoverActivity)
            view.scaleType = ImageView.ScaleType.FIT_XY
            view
        }

        // Setting up gesture detector for swipes left & right
        gestureDetector = GestureDetector(this, SwipeListener())
        imageSwitcher.setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)
            true
        }
    }

    /**
     * Fetches list of items and puts them into the list
     * (this list is used like a queue tbh)
     */
    private fun fetchItems() {
        // Sending request for all my items
        val api = APIService.create()
        val call = api.getRandItem()
        val numOfItems = items.size

        // Show loading circle
        val loading = findViewById<LoadingView>(R.id.loading)!!
        // For first fetching show loading circle
        if(numOfItems == 0)
            loading.begin()
        call.enqueue( object : Callback<DiscoverItems> {
            override fun onResponse(call: Call<DiscoverItems>, response: Response<DiscoverItems>) {
                if(response.isSuccessful && response.body() != null) {
                    // Add new items to the end of the list
                    val data = response.body()?.data ?: listOf()
                    items.addAll(data)

                    // Load new picture if there was no picture
                    if(numOfItems == 0)
                        moveNextOrPrevious(0)
                }
                loading.finish()
            }
            override fun onFailure(call: Call<DiscoverItems>, t: Throwable) {
                loading.finish()
                Toast.makeText(this@DiscoverActivity, "Connection error", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun moveNextOrPrevious(delta: Int) {

        if(delta>0) setupAnimations(Direction.RIGHT)
        if(delta<0) setupAnimations(Direction.LEFT)

        // fetch new images to cache when it's close to the end
        if(items.size < 3){
            fetchItems()
        }

        // TODO: akceptacja / odrzucanie przedmiotu

        // Displaying new image with it's data
        if (items.size > 0) {
            val item = items[0]
            // Add next images to cache
            cacheItems(1..3)
            displayItem(item)
            // Remove item from list after displaying it
            items.removeAt(0)
        } // else wait for fetching new ones
    }

    private fun cacheItems(range: IntRange) {
        for(i in range) {
            if(i < items.size) {
                Picasso.get()
                    .load(items[i].itemPhoto)
                    .resize(imageSwitcher.width, imageSwitcher.height)
                    .centerCrop()
                    .fetch()
            }
        }
    }

    /**
     * Shows item in the GUI
     */
    private fun displayItem(item: Item) {
        // Fill data for new item
        Picasso.get()
            .load(item.itemPhoto!!)
            .resize(imageSwitcher.width, imageSwitcher.height)
            .centerCrop()
            .into(imageSwitcherPicasso)
        item_title.text = item.itemName
        description.text = item.itemDescription
    }

    private fun setupAnimations(direction: Direction) {
        // Swipe animations
        imageSwitcher.inAnimation = when(direction) {
            Direction.RIGHT -> slideInRight
            Direction.LEFT -> slideInLeft
        }
        imageSwitcher.outAnimation = when(direction) {
            Direction.RIGHT -> slideOutLeft
            Direction.LEFT -> slideOutRight
        }
        // overscroll (no more photos) effect on the side on the screen
        if(items.size <= 1) {
            when(direction) {
                Direction.RIGHT -> {
                    overscrollRight.visibility = View.VISIBLE
                    overscrollRight.startAnimation(overscrollRightFadeOut)
                    return
                }
                Direction.LEFT -> {
                    overscrollLeft.visibility = View.VISIBLE
                    overscrollLeft.startAnimation(overscrollLeftFadeOut)
                    return
                }
            }
        }
    }

    private enum class Direction{ LEFT, RIGHT }

    private inner class SwipeListener : GestureDetector.SimpleOnGestureListener() {

        override fun onFling( e1: MotionEvent, e2: MotionEvent,
                              velocityX: Float, velocityY: Float ): Boolean {
            /* Swipe parameters */
            val swipeMinDistance = 75
            val swipeMaxOffPath = 250
            val swipeThresholdVelocity = 200

            if (abs(e1.y - e2.y) > swipeMaxOffPath)
                return false
            // right to left swipe
            if (e1.x - e2.x > swipeMinDistance && abs(velocityX) > swipeThresholdVelocity) {
                moveNextOrPrevious(1)
            } else if (e2.x - e1.x > swipeMinDistance && abs(velocityX) > swipeThresholdVelocity) {
                moveNextOrPrevious(-1)
            }

            return false
        }

    }
}
