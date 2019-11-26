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
import mwo.lab.tapswap.api.models.UserItems
import mwo.lab.tapswap.views.LoadingView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MySwapsAdapter(
    private val context: Activity
) : RecyclerView.Adapter<MySwapsAdapter.ItemHolder>() {

    var items = listOf<Item>()

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        fetchData()
    }

    fun fetchData() {
        // Sending request for all my items
        val api = APIService.create()
        val call = api.getUserItems()

        // Show loading circle
        val loading = context.findViewById<LoadingView>(R.id.loading)!!
        loading.begin()
        call.enqueue(object : Callback<UserItems> {
            override fun onResponse(call: Call<UserItems>, response: Response<UserItems>) {
                if (response.isSuccessful) {
                    items = response.body()?.data ?: listOf()
                    notifyDataSetChanged()
                    loading.finish()
                }
            }

            override fun onFailure(call: Call<UserItems>, t: Throwable) {
                loading.finish()
                Toast.makeText(context, "Connection error", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Implementation ViewHolder pattern
    // every object of this class has it's own reference to the layout element
    // so we call findViewById() only once for each element in the list.
    inner class ItemHolder(item: View) : RecyclerView.ViewHolder(item) {
        var itemTitle: TextView = item.findViewById(R.id.title)
        var itemDescription: TextView = item.findViewById(R.id.description)
        var itemPopupMenu: ImageButton = item.findViewById(R.id.popup_menu)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ItemHolder {
        // inflate layout for single item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.my_item, viewGroup, false)

        // create and return my item holder
        return ItemHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(viewHolder: ItemHolder, i: Int) {
        // fill item layout
        val item = items[i]
        viewHolder.itemTitle.text = item.itemName
        viewHolder.itemDescription.text = item.itemDescription

        viewHolder.itemPopupMenu.setOnClickListener {
            showPopupMenu(viewHolder.itemPopupMenu, i)
        }
        viewHolder.itemView.setOnClickListener {
            previewItem(item)
        }
    }

    // TODO: position parameter is necessary to implement delete action
    private fun showPopupMenu(view: View, position: Int) {
        // inflate menu
        val popup = PopupMenu(view.context, view)
        val inflater = popup.menuInflater
        inflater.inflate(R.menu.my_item_popup_menu, popup.menu)

        // actions for menu buttons
        popup.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.preview -> {
                    previewItem(items[position])
                }
                R.id.remove -> {
                    AlertDialog.Builder(context)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Usuwanie przedmiotu")
                        .setMessage("Czy na pewno chcesz usunąć ten przedmiot?")
                        .setPositiveButton("Tak") { _, _ ->
                            deleteItem(items[position])
                        }
                        .setNegativeButton("Nie", null)
                        .show()
                }
            }
            true
        }
        popup.show()
    }

    private fun previewItem(item: Item) {
        val intent = Intent(context, ItemPreviewActivity::class.java)
        intent.putExtra("itemPhoto", item.itemPhoto)
        intent.putExtra("itemName", item.itemName)
        intent.putExtra("itemDescription", item.itemDescription)
        context.startActivity(intent)
    }

    private fun deleteItem(item: Item) {
        val api = APIService.create()
        val call = api.deleteItem(item.id)
        val loading = context.findViewById<LoadingView>(R.id.loading)!!
        loading.begin()
        call.enqueue(object : Callback<RequestResult> {
            override fun onResponse(call: Call<RequestResult>, response: Response<RequestResult>) {
                loading.finish()
                Toast.makeText(context, "Usunięto przedmiot z konta", Toast.LENGTH_SHORT).show()
                fetchData()
            }

            override fun onFailure(call: Call<RequestResult>, t: Throwable) {
                loading.finish()
                Toast.makeText(context, "Connection error", Toast.LENGTH_SHORT).show()
            }
        })
    }
}

