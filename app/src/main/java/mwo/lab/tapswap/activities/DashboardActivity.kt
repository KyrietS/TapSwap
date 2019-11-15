package mwo.lab.tapswap.activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import mwo.lab.tapswap.R


class DashboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
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
    @Suppress("UNUSED_PARAMETER")
    fun myAccountOnClick(v: View) {
        val intent = Intent(this, MyAccountActivity::class.java)
        startActivity(intent)
    }
    @Suppress("UNUSED_PARAMETER")
    fun addItemOnClick(v: View) {
        val intent = Intent(this, AddItemActivity::class.java)
        startActivity(intent)
    }
    @Suppress("UNUSED_PARAMETER")
    fun mySwapsOnClick(v: View) {
        val intent = Intent(this, MySwapsActivity::class.java)
        startActivity(intent)
    }
    @Suppress("UNUSED_PARAMETER")
    fun whateverOnClick(v: View) {
        val intent = Intent(this, ItemPreviewActivity::class.java)
        startActivity(intent)
    }

}