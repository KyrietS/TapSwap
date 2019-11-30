package mwo.lab.tapswap.activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.Button
import mwo.lab.tapswap.R
import mwo.lab.tapswap.adapters.MySwapsAdapter

class MySwapsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_swaps)

        val recyclerView = findViewById<RecyclerView>(R.id.swaps_recycler)

        // for optimisation of recyclerView
        recyclerView.setHasFixedSize(true)

        // LayoutManager
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Animator
        recyclerView.itemAnimator = DefaultItemAnimator()

        val myAdapter = MySwapsAdapter(this)
        recyclerView.adapter = myAdapter


    }
}