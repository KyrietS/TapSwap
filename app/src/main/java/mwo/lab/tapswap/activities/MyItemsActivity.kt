package mwo.lab.tapswap.activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.Button
import com.google.gson.Gson
import mwo.lab.tapswap.R
import mwo.lab.tapswap.adapters.MyItemsAdapter
import mwo.lab.tapswap.models.Item
import java.util.*

class MyItemsActivity : AppCompatActivity() {

    private val items = ArrayList<Item>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_items)

        val recyclerView = findViewById<RecyclerView>(R.id.items_recycler)


        // for optimisation of recyclerView
        recyclerView.setHasFixedSize(true)

        // LayoutManager
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Animator
        recyclerView.itemAnimator = DefaultItemAnimator()

        // Read mockup items from raw JSON file using GSON
        val json: String = resources.openRawResource(R.raw.items).readBytes().toString(Charsets.UTF_8)
        val gson = Gson()
        items.addAll(gson.fromJson(json, Array<Item>::class.java))

        val myAdapter = MyItemsAdapter(this)
        recyclerView.adapter = myAdapter

        val addButton = findViewById<Button>(R.id.addButton)
        addButton.setOnClickListener {
            val intent = Intent(this, AddItemActivity::class.java)
            startActivity(intent)
        }
    }
}
