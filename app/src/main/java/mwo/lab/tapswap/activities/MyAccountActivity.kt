package mwo.lab.tapswap.activities

import android.app.Activity
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.*
import mwo.lab.tapswap.R
import mwo.lab.tapswap.api.APIService
import mwo.lab.tapswap.api.models.Myself
import mwo.lab.tapswap.api.models.RequestResult
import mwo.lab.tapswap.views.LoadingView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyAccountActivity : AppCompatActivity() {

    private lateinit var nameText: EditText
    private lateinit var emailText: EditText
    private lateinit var mobileText: EditText
    private lateinit var oldPasswordText: EditText
    private lateinit var newPasswordText: EditText
    private lateinit var reEnterPasswordText: EditText
    private lateinit var saveChangesButton: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var scrollView: ScrollView
    private lateinit var loading: LoadingView

    var password: String = ""
    var name: String? = null
    var phone: String? = null
    var newPassword: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_account)

        emailText = findViewById(R.id.input_email)
        oldPasswordText = findViewById(R.id.input_password)
        newPasswordText = findViewById(R.id.input_new_password)
        nameText = findViewById(R.id.input_name)
        mobileText = findViewById(R.id.input_mobile)
        reEnterPasswordText = findViewById(R.id.input_reEnterPassword)
        saveChangesButton = findViewById(R.id.btn_signup)
        progressBar = findViewById(R.id.progress_bar)
        scrollView = findViewById(R.id.scroll_view)
        loading = findViewById(R.id.loading)
        saveChangesButton.setOnClickListener { saveChanges() }

        fillFormWithData()
    }

    private fun fillFormWithData() {
        enableUI(false)
        val api = APIService.create()
        val call = api.getMyself()
        loading.begin()
        call.enqueue(object : Callback<Myself> {
            override fun onResponse(call: Call<Myself>, response: Response<Myself>) {
                loading.finish()
                if(response.isSuccessful && response.body()?.success == true) {
                    val myselfData = response.body()!!.data
                    nameText.setText(myselfData.name)
                    emailText.setText(myselfData.email)
                    mobileText.setText(myselfData.phone)
                    enableUI(true)
                } else {
                    onFailure(call, null)
                }

            }
            override fun onFailure(call: Call<Myself>, t: Throwable?) {
                enableUI(true)
                Toast.makeText(this@MyAccountActivity, "Connection error", Toast.LENGTH_SHORT).show()
                finish()
            }
        })
    }

    private fun saveChanges() {
        if (!validate()) {
            onSaveFailed()
            return
        }

        // Scroll to the bottom to show the user progress bar
        scrollView.scrollTo(0, scrollView.bottom)

        name = nameText.text.toString()
        phone = mobileText.text.toString()
        password = oldPasswordText.text.toString()


        enableUI(false)
        progressBar.visibility = View.VISIBLE

        sendUpdateAccountRequest(name, phone, password, newPassword)
    }

    private fun sendUpdateAccountRequest(
        name: String?,
        mobile: String?,
        old_password: String,
        new_password: String?
    ) {
        val api = APIService.create()
        val call = api.updateUser(name, mobile, old_password, new_password)
        call.enqueue(object : Callback<RequestResult> {
            override fun onResponse(call: Call<RequestResult>, response: Response<RequestResult>) {
                if(response.isSuccessful && response.body()?.success == true) {
                    onSaveSuccess()
                } else {
                    Log.d("sign", "unknown error")
                    Log.d("sign", response.raw().toString())
                    onSaveFailed()
                }
            }
            override fun onFailure(call: Call<RequestResult>, t: Throwable) {
                enableUI(true)
                progressBar.visibility = View.GONE
                Toast.makeText(this@MyAccountActivity, "Connection error", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun onSaveSuccess() {
        Log.d("sign", "Success")
        Toast.makeText(this, "Dane zostały zmienione", Toast.LENGTH_SHORT).show()
        setResult(Activity.RESULT_OK, null)
        finish()
    }

    private fun onSaveFailed() {
        Toast.makeText(baseContext, "Wystąpił błąd", Toast.LENGTH_SHORT).show()
        enableUI(true)
        progressBar.visibility = View.GONE
    }

    private fun validate(): Boolean { //TODO: validate data changes
        var valid = true

        val name = nameText.text.toString()
        val email = emailText.text.toString()
        val mobile = mobileText.text.toString()
        val old_password = oldPasswordText.text.toString()
        val new_password = newPasswordText.text.toString()
        val reEnterPassword = reEnterPasswordText.text.toString()

        if (name.isEmpty() || name.length < 3) {
            nameText.error = "at least 3 characters"
            valid = false
        }

        if (mobile.isEmpty() || mobile.length < 9 || mobile.contains("[^0-9 +-]".toRegex())) {
            mobileText.error = "Enter Valid Mobile Number"
            valid = false
        }

        newPassword = if(new_password.isEmpty()) {
            null
        } else {
            new_password
        }

        if (new_password.isNotEmpty() && reEnterPassword != new_password) {
            reEnterPasswordText.error = "Password Do not match"
            valid = false
        }

        return valid
    }

    private fun enableUI(state: Boolean) {
        emailText.isEnabled = false
        oldPasswordText.isEnabled = state
        newPasswordText.isEnabled = state
        nameText.isEnabled = state
        mobileText.isEnabled = state
        reEnterPasswordText.isEnabled = state
        saveChangesButton.isEnabled = state
    }
}