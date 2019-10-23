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
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.math.abs

class MainActivity : AppCompatActivity() {

    private val currentPosition = "current_position"

    private var mImageSwitcher: ImageSwitcher? = null
    private var mOverscrollLeft: View? = null
    private var mOverscrollRight: View? = null

    private var mGestureDetector: GestureDetector? = null

    private val items = arrayOf(
        Item(R.drawable.book, "Wprowadzenie do algorytmów, Cormen", "Ciekawa fabuła, połączenie komedii i dramatu. Szczerze polecam :)"),
        Item(R.drawable.knife, "Nusz xd", "Opis noża"),
        Item(R.drawable.pistol, "pistolet", "piestoleteł"),
        Item(R.drawable.watch, "zegarek", "zegrkeł")
    )

    private var mCurrentPosition = 0

    private var mSlideInLeft: Animation? = null
    private var mSlideOutRight: Animation? = null
    private var mSlideInRight: Animation? = null
    private var mSlideOutLeft: Animation? = null
    private var mOverscrollLeftFadeOut: Animation? = null
    private var mOverscrollRightFadeOut: Animation? = null

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState != null) {
            mCurrentPosition = savedInstanceState.getInt(currentPosition, 0)
        }

        // Views
        mImageSwitcher = findViewById(R.id.image)
        mOverscrollLeft = findViewById(R.id.overscroll_left)
        mOverscrollRight = findViewById(R.id.overscroll_right)

        // Animations
        mSlideInLeft = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left)
        mSlideOutRight = AnimationUtils.loadAnimation(this, android.R.anim.slide_out_right)
        mSlideInRight = AnimationUtils.loadAnimation(this, R.anim.slide_in_right)
        mSlideOutLeft = AnimationUtils.loadAnimation(this, R.anim.slide_out_left)

        mOverscrollLeftFadeOut = AnimationUtils
            .loadAnimation(this, R.anim.fade_out)
        mOverscrollRightFadeOut = AnimationUtils.loadAnimation(
            this,
            R.anim.fade_out
        )

        // ImageSwitcher
        mImageSwitcher?.setFactory {
            val view = ImageView(this@MainActivity)
            view.scaleType = ImageView.ScaleType.FIT_XY
            view
        }

        // Default picture
        mImageSwitcher?.setImageResource(items[mCurrentPosition].drawable)
        descritpion.text = items[mCurrentPosition].description
        item_title.text = items[mCurrentPosition].title
        mGestureDetector = GestureDetector(this, SwipeListener())
        mImageSwitcher?.setOnTouchListener { _, event ->
            mGestureDetector?.onTouchEvent(event)
            true
        }
    }

    /* Restore image position when back from other app */
    public override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        if (outState == null) {
            return
        }
        outState.putInt(currentPosition, mCurrentPosition)
    }

    private fun moveNextOrPrevious(delta: Int) {
        val nextImagePos = mCurrentPosition + delta
        if (nextImagePos < 0) {
            mOverscrollLeft?.visibility = View.VISIBLE
            mOverscrollLeft?.startAnimation(mOverscrollLeftFadeOut)
            return
        }
        if (nextImagePos >= items.size) {
            mOverscrollRight?.visibility = View.VISIBLE
            mOverscrollRight?.startAnimation(mOverscrollRightFadeOut)
            return
        }

        mImageSwitcher?.inAnimation = if (delta > 0) mSlideInRight else mSlideInLeft
        mImageSwitcher?.outAnimation = if (delta > 0) mSlideOutLeft else mSlideOutRight

        mCurrentPosition = nextImagePos
        mImageSwitcher?.setImageResource(items[mCurrentPosition].drawable)
        descritpion.text = items[mCurrentPosition].description
        item_title.text = items[mCurrentPosition].title
    }

    private inner class SwipeListener : GestureDetector.SimpleOnGestureListener() {

        override fun onFling( e1: MotionEvent, e2: MotionEvent, velocityX: Float,
                              velocityY: Float ): Boolean {

            /* Swipe parameters */
            val swipeMinDistance = 75
            val swipeMaxOffPath = 250
            val swipeThresholdVelocity = 200

            try {
                if (abs(e1.y - e2.y) > swipeMaxOffPath)
                    return false
                // right to left swipe
                if (e1.x - e2.x > swipeMinDistance && abs(velocityX) > swipeThresholdVelocity) {
                    moveNextOrPrevious(1)
                } else if (e2.x - e1.x > swipeMinDistance && abs(velocityX) > swipeThresholdVelocity) {
                    moveNextOrPrevious(-1)
                }
            } catch (e: Exception) {
                // nothing
            }

            return false
        }

    }
}
