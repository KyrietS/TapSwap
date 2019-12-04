package mwo.lab.tapswap.activities

import android.app.AlertDialog
import android.graphics.Point
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.squareup.picasso.Picasso
import mwo.lab.tapswap.R
import mwo.lab.tapswap.api.APIService
import mwo.lab.tapswap.api.models.RequestResult
import mwo.lab.tapswap.views.LoadingView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SingleSwapActivity : AppCompatActivity() {

    private var swapId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_single_swap)

        swapId = intent.getIntExtra("matchId", -1)

        fillLayoutWithData()

        findViewById<Button>(R.id.finishButton).setOnClickListener {
            AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Wymiana przebiegła pomyślnie")
                .setMessage("Czy na pewno chcesz potwierdzić, że wymiana z użytkownikiem została zakończona pomyślnie?")
                .setPositiveButton("Tak") { _, _ ->
                    finishSwap()
                }
                .setNegativeButton("Nie", null)
                .show()
        }
        findViewById<Button>(R.id.reportButton).setOnClickListener {
            AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Zgłaszanie wymiany do administracji")
                .setMessage("Czy na pewno chcesz zgłosić tę wymianę z powodu nadużycia?")
                .setPositiveButton("Tak") { _, _ ->
                    reportSwap()
                }
                .setNegativeButton("Nie", null)
                .show()
        }
    }

    private fun fillLayoutWithData() {
        val myItemName = intent.getStringExtra("myItemName") ?: return close()
        val otherItemName = intent.getStringExtra("otherItemName") ?: return close()

        val toName = intent.getStringExtra("toName") ?: return close()
        val toEmail = intent.getStringExtra("toEmail") ?: return close()
        val toPhone = intent.getStringExtra("toPhone") ?: return close()

        val fromName = intent.getStringExtra("toName") ?: return close()
        val fromEmail = intent.getStringExtra("toEmail") ?: return close()
        val fromPhone = intent.getStringExtra("toPhone") ?: return close()

        findViewById<TextView>(R.id.myItemName).text = myItemName
        findViewById<TextView>(R.id.otherItemName).text = otherItemName

        findViewById<TextView>(R.id.toName).text = toName
        findViewById<TextView>(R.id.toEmail).text = toEmail
        findViewById<TextView>(R.id.toPhone).text = toPhone

        findViewById<TextView>(R.id.fromName).text = fromName
        findViewById<TextView>(R.id.fromEmail).text = fromEmail
        findViewById<TextView>(R.id.fromPhone).text = fromPhone

        loadImages()
    }

    private fun loadImages() {
        val myItemPhoto = intent.getStringExtra("myItemPhoto") ?: return close()
        val otherItemPhoto = intent.getStringExtra("otherItemPhoto") ?: return close()

        val myItemPhotoView = findViewById<ImageView>(R.id.myItemPhoto)
        val otherItemPhotoView = findViewById<ImageView>(R.id.otherItemPhoto)

        // Get screen width
        val display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)

        Picasso.get()
            .load(myItemPhoto)
            .resize(size.x/2, size.x/2)
            .centerCrop()
            .into(myItemPhotoView, object : com.squareup.picasso.Callback {
                override fun onSuccess() {

                }
                override fun onError(e: Exception?) {
                    Toast.makeText(this@SingleSwapActivity, "Connection error", Toast.LENGTH_SHORT).show()
                }
            })
        Picasso.get()
            .load(otherItemPhoto)
            .resize(size.x/2, size.x/2)
            .centerCrop()
            .into(otherItemPhotoView, object : com.squareup.picasso.Callback {
                override fun onSuccess() {

                }
                override fun onError(e: Exception?) {
                    Toast.makeText(this@SingleSwapActivity, "Connection error", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun finishSwap() {
        val api = APIService.create()
        val call = api.confirmMatch(swapId)
        val loading = findViewById<LoadingView>(R.id.loading)!!
        loading.begin()
        call.enqueue(object : Callback<RequestResult> {
            override fun onResponse(call: Call<RequestResult>, response: Response<RequestResult>) {
                loading.finish()
                Toast.makeText(this@SingleSwapActivity, "Wymiana została zakończona", Toast.LENGTH_SHORT).show()
                finish()
            }
            override fun onFailure(call: Call<RequestResult>, t: Throwable) {
                loading.finish()
                Toast.makeText(this@SingleSwapActivity, "Connection error", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun reportSwap() {
        Toast.makeText(this, "Wymiana została zgłoszona", Toast.LENGTH_SHORT).show()
    }

    private fun close() {
        finish()
    }

}
