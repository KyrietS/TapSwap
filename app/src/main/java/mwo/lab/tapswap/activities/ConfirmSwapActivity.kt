package mwo.lab.tapswap.activities

import android.graphics.Point
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.squareup.picasso.Picasso
import mwo.lab.tapswap.R

class ConfirmSwapActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirm_swap)

        fillLayoutWithData()
    }

    private fun fillLayoutWithData() {
        val myItemName = intent.getStringExtra("myItemName") ?: return close()
        val otherItemName = intent.getStringExtra("otherItemName") ?: return close()
        val myItemPrice = intent.getStringExtra("myItemPrice") ?: return close()
        val otherItemPrice = intent.getStringExtra("otherItemPrice") ?: return close()

        findViewById<TextView>(R.id.myItemName).text = myItemName
        findViewById<TextView>(R.id.otherItemName).text = otherItemName
        findViewById<TextView>(R.id.myPrice).text = myItemPrice
        findViewById<TextView>(R.id.otherPrice).text = otherItemPrice

        loadImages()
    }

    private fun loadImages() {
        val myItemPhoto = intent.getStringExtra("myItemPhoto") ?: return close()
        val otherItemPhoto = intent.getStringExtra("otherItemPhoto") ?: return close()

        val myItemPhotoView = findViewById<ImageView>(R.id.myItemPhoto)
        val otherItemPhotoView = findViewById<ImageView>(R.id.otherItemPhoto)

        // Get screen width
        val display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)

        Picasso.get()
            .load(myItemPhoto)
            .resize(size.x/2, size.x/2)
            .centerCrop()
            .into(myItemPhotoView, object : com.squareup.picasso.Callback {
                override fun onSuccess() {

                }
                override fun onError(e: Exception?) {
                    Toast.makeText(this@ConfirmSwapActivity, "Connection error", Toast.LENGTH_SHORT).show()
                }
            })
        Picasso.get()
            .load(otherItemPhoto)
            .resize(size.x/2, size.x/2)
            .centerCrop()
            .into(otherItemPhotoView, object : com.squareup.picasso.Callback {
                override fun onSuccess() {

                }
                override fun onError(e: Exception?) {
                    Toast.makeText(this@ConfirmSwapActivity, "Connection error", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun close() {
        finish()
    }
}
