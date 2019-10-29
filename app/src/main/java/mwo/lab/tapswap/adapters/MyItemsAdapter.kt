package mwo.lab.tapswap.adapters

import android.app.Activity
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
import mwo.lab.tapswap.activities.AddItemActivity
import mwo.lab.tapswap.models.Item


class MyItemsAdapter(
    private val context: Activity,
    private val items: ArrayList<Item>
) : RecyclerView.Adapter<MyItemsAdapter.ItemHolder>()  {

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
        viewHolder.itemTitle.text = item.title
        viewHolder.itemDescription.text = item.description

        viewHolder.itemPopupMenu.setOnClickListener {
            showPopupMenu(viewHolder.itemPopupMenu, i)
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
                R.id.edit -> {
                    Toast.makeText(context, "TODO: Fill fields with data", Toast.LENGTH_SHORT).show()
                    val intent = Intent(context, AddItemActivity::class.java)
                    context.startActivity(intent)
                }
                R.id.remove -> {
                    Toast.makeText(context, "TODO: Remove item confirmation", Toast.LENGTH_SHORT).show()
                }
            }
            true
        }
        popup.show()
    }

}