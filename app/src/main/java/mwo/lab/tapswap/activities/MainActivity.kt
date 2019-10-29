package mwo.lab.tapswap.activities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import mwo.lab.tapswap.R

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    @Suppress("UNUSED_PARAMETER")
    fun discoverOnClick(v: View) {
        val intent = Intent(this, DiscoverActivity::class.java)
        startActivity(intent)
    }

    @Suppress("UNUSED_PARAMETER")
    fun myItemsOnClick(v: View) {
        val intent = Intent(this, MyItemsActivity::class.java)
        startActivity(intent)
    }
}
