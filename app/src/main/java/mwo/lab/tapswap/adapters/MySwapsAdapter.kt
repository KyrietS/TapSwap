package mwo.lab.tapswap.adapters

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import mwo.lab.tapswap.R
import mwo.lab.tapswap.activities.ItemPreviewActivity
import mwo.lab.tapswap.api.APIService
import mwo.lab.tapswap.api.models.Item
import mwo.lab.tapswap.api.models.RequestResult
import mwo.lab.tapswap.api.models.Swap
import mwo.lab.tapswap.api.models.UserSwaps
import mwo.lab.tapswap.views.LoadingView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MySwapsAdapter(
    private val context: Activity
) : RecyclerView.Adapter<MySwapsAdapter.SwapHolder>() {

    var swaps = listOf<Swap>()

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        fetchData()
    }

    fun fetchData() {
        // Sending request for all my swaps
        val api = APIService.create()
        val call = api.getUserSwaps()

        // Show loading circle
        val loading = context.findViewById<LoadingView>(R.id.loading)!!
        loading.begin()
        call.enqueue(object : Callback<UserSwaps> {
            override fun onResponse(call: Call<UserSwaps>, response: Response<UserSwaps>) {
                if (response.isSuccessful) {
                    swaps = response.body()?.data ?: listOf()
                    notifyDataSetChanged()
                    loading.finish()
                }
            }

            override fun onFailure(call: Call<UserSwaps>, t: Throwable) {
                loading.finish()
                Toast.makeText(context, "Connection error", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Implementation ViewHolder pattern
    // every object of this class has it's own reference to the layout element
    // so we call findViewById() only once for each element in the list.
    inner class SwapHolder(swap: View) : RecyclerView.ViewHolder(swap) {
        var myItemTitle: TextView = swap.findViewById(R.id.my_swap_item_name)
        var theirItemTitle: TextView = swap.findViewById(R.id.their_swap_item_name)
        var myItemPhoto: TextView = swap.findViewById(R.id.my_swap_item)
        var theirItemPhoto: TextView = swap.findViewById(R.id.their_swap_item)
        var swapPopupMenu: ImageButton = swap.findViewById(R.id.popup_menu)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): SwapHolder {
        // inflate layout for single swap
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.my_swap, viewGroup, false)

        // create and return my swap holder
        return SwapHolder(view)
    }

    override fun getItemCount(): Int {
        return swaps.size
    }

    override fun onBindViewHolder(viewHolder: SwapHolder, i: Int) {
        // fill item layout
        val swap = swaps[i]
        //viewHolder.itemTitle.text = item.itemName
        //viewHolder.itemDescription.text = item.itemDescription

        viewHolder.swapPopupMenu.setOnClickListener {
            showPopupMenu(viewHolder.swapPopupMenu, i)
        }

    }

    // TODO: position parameter is necessary to implement delete action
    private fun showPopupMenu(view: View, position: Int) {
        // inflate menu
        val popup = PopupMenu(view.context, view)
        val inflater = popup.menuInflater
        inflater.inflate(R.menu.my_swap_popup_menu, popup.menu)

        // actions for menu buttons
        popup.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.details -> {
                    viewDetails(swaps[position])
                }

            }
            true
        }
        popup.show()
    }

    private fun viewDetails(swap: Swap){

    }


}

