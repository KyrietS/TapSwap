package mwo.lab.tapswap

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.widget.Button
import com.google.gson.Gson
import mwo.lab.tapswap.api.Endpoints
import mwo.lab.tapswap.api.APIService
import mwo.lab.tapswap.api.model.UserItems
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class MyItemsActivity : AppCompatActivity() {

    private val items = ArrayList<Item>()

    private lateinit var api: Endpoints

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_items)

        // TODO chyba nie wysyła tokenu
        api = APIService.create()

        val call = api.getUserItems()
        call.enqueue( object : Callback<UserItems> {
            override fun onResponse(call: Call<UserItems>, response: Response<UserItems>) {

                Log.d("omg", call.request().headers().toString())
                val body = response.body()!!
                Log.d("omg", "RESPONSE")
                Log.d("omg", body.toString())
            }

            override fun onFailure(call: Call<UserItems>, t: Throwable) {
                Log.d("omg", "FAILURE")
            }
        })

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

        val addButton = findViewById<Button>(R.id.addButton)
        addButton.setOnClickListener {
            val intent = Intent(this, AddItemActivity::class.java)
            startActivity(intent)
        }
    }
}
