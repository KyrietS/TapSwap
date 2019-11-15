package mwo.lab.tapswap.activities

import android.graphics.Point
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_item_preview.*
import mwo.lab.tapswap.R
import mwo.lab.tapswap.views.LoadingView


class ItemPreviewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_preview)

        fillView()
    }

    private fun fillView() {
        val photoURL = intent.getStringExtra("itemPhoto") ?: return close()
        val name = intent.getStringExtra("itemName") ?: return close()
        val description = intent.getStringExtra("itemDescription") ?: return close()

        val loading = findViewById<LoadingView>(R.id.loading)!!
        loading.begin()

        // Get screen width
        val display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)

        Picasso.get()
            .load(photoURL)
            .resize(size.x, size.x)
            .centerCrop()
            .into(item_image, object : Callback {
                override fun onSuccess() {
                    loading.finish()
                }
                override fun onError(e: Exception?) {
                    loading.finish()
                    Toast.makeText(this@ItemPreviewActivity, "Connection error", Toast.LENGTH_SHORT).show()
                    close()
                }
            })
        item_name.text = name
        item_description.text = description
    }

    @Suppress("UNUSED_PARAMETER")
    fun onClose(view: View) {
        close()
    }

    private fun close() {
        finish()
    }
}
