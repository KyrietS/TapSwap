package mwo.lab.tapswap.views

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.ProgressBar
import android.widget.RelativeLayout

/**
 * A View for displaying loading circle.
 */
class LoadingView(_context: Context, _attrs: AttributeSet?) : RelativeLayout(_context, _attrs) {
    init {
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        gravity = Gravity.CENTER
        visibility = View.GONE

        val progressBar = ProgressBar(context)
        progressBar.layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        progressBar.isIndeterminate = true
        addView(progressBar)
    }

    /**
     * Show loading circle
     */
    fun begin() {
        visibility = View.VISIBLE
    }

    /**
     * Hide loading circle
     */
    fun finish() {
        visibility = View.GONE
    }

}