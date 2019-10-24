package mwo.lab.tapswap

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
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_discover.*
import kotlin.math.abs

class DiscoverActivity : AppCompatActivity() {

    // Views
    private lateinit var imageSwitcher: ImageSwitcher
    private lateinit var overscrollLeft: View
    private lateinit var overscrollRight: View

    private lateinit var gestureDetector: GestureDetector
    private var currentPosition = 0

    // Animations
    private var slideInLeft: Animation? = null
    private var slideOutRight: Animation? = null
    private var slideInRight: Animation? = null
    private var slideOutLeft: Animation? = null
    private var overscrollLeftFadeOut: Animation? = null
    private var overscrollRightFadeOut: Animation? = null

    // Mock items
    private lateinit var items: Array<Item>

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_discover)

        // Read mockup items from raw JSON file using GSON
        val json: String = resources.openRawResource(R.raw.items).readBytes().toString(Charsets.UTF_8)
        val gson = Gson()
        items = gson.fromJson(json, Array<Item>::class.java)

        // Views
        imageSwitcher = findViewById(R.id.image)
        overscrollLeft = findViewById(R.id.overscroll_left)
        overscrollRight = findViewById(R.id.overscroll_right)

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

        // Default picture
        moveNextOrPrevious(0)
    }

    private fun moveNextOrPrevious(delta: Int) {
        val nextImagePos = currentPosition + delta

        // overscroll (no more photos) effect on the side on the screen
        if (nextImagePos < 0) {
            overscrollLeft.visibility = View.VISIBLE
            overscrollLeft.startAnimation(overscrollLeftFadeOut)
            return
        }
        if (nextImagePos >= items.size) {
            overscrollRight.visibility = View.VISIBLE
            overscrollRight.startAnimation(overscrollRightFadeOut)
            return
        }

        // Swipe animations
        imageSwitcher.inAnimation = if (delta > 0) slideInRight else slideInLeft
        imageSwitcher.outAnimation = if (delta > 0) slideOutLeft else slideOutRight

        // Displaying new image with it's data
        currentPosition = nextImagePos
        val res = resources.getIdentifier(items[currentPosition].drawable, "drawable", packageName)
        imageSwitcher.setImageResource(res)
        description.text = items[currentPosition].description
        item_title.text = items[currentPosition].title
    }

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
