package mwo.lab.tapswap.adapters

import android.app.Activity
import android.content.Intent
import android.graphics.Point
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.squareup.picasso.Picasso
import mwo.lab.tapswap.R
import mwo.lab.tapswap.activities.ConfirmSwapActivity
import mwo.lab.tapswap.activities.SingleSwapActivity
import mwo.lab.tapswap.api.APIService
import mwo.lab.tapswap.api.models.Matches
import mwo.lab.tapswap.views.LoadingView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MySwapsAdapter(
    private val context: Activity
) : RecyclerView.Adapter<MySwapsAdapter.SwapHolder>() {

    var swaps = listOf<Matches.MatchesData.Match>()

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        fetchData()
    }

    fun fetchData() {
        // Sending request for all my swaps
        val api = APIService.create()
        val call = api.getAllMatches()

        // Show loading circle
        val loading = context.findViewById<LoadingView>(R.id.loading)!!
        loading.begin()
        call.enqueue(object : Callback<Matches> {
            override fun onResponse(call: Call<Matches>, response: Response<Matches>) {
                if (response.isSuccessful) {
                    swaps = response.body()?.data?.matches ?: listOf()
                    notifyDataSetChanged()
                    loading.finish()
                }
            }

            override fun onFailure(call: Call<Matches>, t: Throwable) {
                loading.finish()
                Toast.makeText(context, "Connection error", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Implementation ViewHolder pattern
    // every object of this class has it's own reference to the layout element
    // so we call findViewById() only once for each element in the list.
    inner class SwapHolder(swap: View) : RecyclerView.ViewHolder(swap) {
        var myItemName: TextView = swap.findViewById(R.id.my_item_name)
        var otherItemName: TextView = swap.findViewById(R.id.other_item_name)
        var myItemPhoto: ImageView = swap.findViewById(R.id.my_item_photo)
        var otherItemPhoto: ImageView = swap.findViewById(R.id.other_item_photo)
        var status: TextView = swap.findViewById(R.id.status)
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
        viewHolder.myItemName.text = swap.myItem.name
        viewHolder.otherItemName.text = swap.exchangeItem.name
        when(swap.status) {
            "PENDING" -> {
                viewHolder.status.text = "Oczekująca"
                viewHolder.status.setTextColor(ContextCompat.getColor(context, R.color.pendingMatch))
            }
            "ACCEPTED" -> {
                viewHolder.status.text = "Zaakceptowana"
                viewHolder.status.setTextColor(ContextCompat.getColor(context, R.color.waitForOthersMatch))
            }
            "ACCEPTED_BY_ALL" -> {
                viewHolder.status.text = "Rozpoczęta"
                viewHolder.status.setTextColor(ContextCompat.getColor(context, R.color.startedMatch))
            }
            else -> {
                viewHolder.status.text = "Nieznany status"
                viewHolder.status.setTextColor(ContextCompat.getColor(context, R.color.fontLight))
            }
        }

        loadImages(viewHolder, i)

        viewHolder.itemView.setOnClickListener {
            showDetails(swap)
        }
        viewHolder.swapPopupMenu.setOnClickListener {
            showPopupMenu(viewHolder.swapPopupMenu, swap)
        }
    }

    private fun loadImages(viewHolder: SwapHolder, i: Int) {
        val swap = swaps[i]

        val myPhotoURL = swap.myItem.photoUrl
        val otherPhotoURL = swap.exchangeItem.photoUrl

        // Get screen width
        val display = context.windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)

        Picasso.get()
            .load(myPhotoURL)
            .resize(size.x/2, size.x/2)
            .centerCrop()
            .into(viewHolder.myItemPhoto, object : com.squareup.picasso.Callback {
                override fun onSuccess() {

                }
                override fun onError(e: Exception?) {
                    Toast.makeText(context, "Connection error", Toast.LENGTH_SHORT).show()
                }
            })
        Picasso.get()
            .load(otherPhotoURL)
            .resize(size.x/2, size.x/2)
            .centerCrop()
            .into(viewHolder.otherItemPhoto, object : com.squareup.picasso.Callback {
                override fun onSuccess() {

                }
                override fun onError(e: Exception?) {
                    Toast.makeText(context, "Connection error", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun showDetails(swap: Matches.MatchesData.Match) {
        val intent: Intent = when(swap.status) {
            "PENDING" -> {
                Intent(context, ConfirmSwapActivity::class.java)
            }
            "ACCEPTED" -> {
                Intent(context, ConfirmSwapActivity::class.java)
            }
            "ACCEPTED_BY_ALL" -> {
                Intent(context, SingleSwapActivity::class.java)
            }
            else -> {
                return
            }
        }
        // Nazwa przedmiotu
        intent.putExtra("myItemName", swap.myItem.name)
        intent.putExtra("otherItemName", swap.exchangeItem.name)
        // Opis
        intent.putExtra("myItemDesc", swap.myItem.description)
        intent.putExtra("otherItemDesc", swap.exchangeItem.description)
        // Zdjęcie
        intent.putExtra("myItemPhoto", swap.myItem.photoUrl)
        intent.putExtra("otherItemPhoto", swap.exchangeItem.photoUrl)
        // Kategoria cenowa
        intent.putExtra("myItemPrice", swap.myItem.priceCategory)
        intent.putExtra("otherItemPrice", swap.exchangeItem.priceCategory)
        // Dane osoby
        intent.putExtra("name", swap.toWho.name)
        intent.putExtra("email", swap.toWho.email)
        intent.putExtra("phone", swap.toWho.phone)

        context.startActivity(intent)
    }

    private fun showPopupMenu(view: View, swap: Matches.MatchesData.Match) {
        // inflate menu
        val popup = PopupMenu(view.context, view)
        val inflater = popup.menuInflater
        inflater.inflate(R.menu.my_swap_popup_menu, popup.menu)

        // actions for menu buttons
        popup.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.details -> {
                    showDetails(swap)
                }
            }
            true
        }
        popup.show()
    }
}

