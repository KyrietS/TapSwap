package mwo.lab.tapswap.activities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import mwo.lab.tapswap.R
import mwo.lab.tapswap.api.APIService
import mwo.lab.tapswap.api.models.RequestResult
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LaunchScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launch_screen)

        startDashboard()
    }

    private fun startDashboard() {
        // Sending request for testing my token
        val api = APIService.create()
        val call = api.isTokenValid()

        call.enqueue( object : Callback<RequestResult> {
            override fun onResponse(call: Call<RequestResult>, response: Response<RequestResult>) {
                // Token zapisany w urządzeniu jest poprawny
                if(response.isSuccessful && response.body()?.success == true) {
                    val intent = Intent(this@LaunchScreenActivity, DashboardActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    val intent = Intent(this@LaunchScreenActivity, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
            override fun onFailure(call: Call<RequestResult>, t: Throwable) {
                Toast.makeText(this@LaunchScreenActivity, "Connection error", Toast.LENGTH_SHORT).show()
                Handler().postDelayed({
                    startDashboard()
                }, 2000)
            }
        })
    }

}
