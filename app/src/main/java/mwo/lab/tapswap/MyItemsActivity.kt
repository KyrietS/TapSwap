package mwo.lab.tapswap

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import com.google.gson.Gson
import java.util.*

class MyItemsActivity : AppCompatActivity() {

    private val items = ArrayList<Item>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_items)

        val recyclerView = findViewById<RecyclerView>( R.id.items_recycler )

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

        val myAdapter = MyItemsAdapter(this, items)
        recyclerView.adapter = myAdapter
    }
}
