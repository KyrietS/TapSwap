package mwo.lab.tapswap.activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.app.Activity
import android.util.Log
import android.view.View
import android.widget.*
import mwo.lab.tapswap.R
import mwo.lab.tapswap.api.APIService
import mwo.lab.tapswap.api.models.RequestResult
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignUpActivity : AppCompatActivity() {

    private lateinit var nameText: EditText
    private lateinit var emailText: EditText
    private lateinit var mobileText: EditText
    private lateinit var passwordText: EditText
    private lateinit var reEnterPasswordText: EditText
    private lateinit var signUpButton: Button
    private lateinit var loginLink: TextView

    private lateinit var progressBar: ProgressBar
    private lateinit var scrollView: ScrollView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        emailText = findViewById(R.id.input_email)
        passwordText = findViewById(R.id.input_password)
        nameText = findViewById(R.id.input_name)
        mobileText = findViewById(R.id.input_mobile)
        reEnterPasswordText = findViewById(R.id.input_reEnterPassword)
        signUpButton = findViewById(R.id.btn_signup)
        loginLink = findViewById(R.id.link_login)

        progressBar = findViewById(R.id.progress_bar)
        scrollView = findViewById(R.id.scroll_view)

        signUpButton.setOnClickListener { signUp() }

        loginLink.setOnClickListener {
            // Finish the registration screen and return to the Login activity
            val intent = Intent(applicationContext, LoginActivity::class.java)
            startActivity(intent)
            finish()
            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out)
        }
    }

    private fun signUp() {
        if (!validate()) {
            onSignUpFailed()
            return
        }

        // Scroll to the bottom to show the user progress bar
        scrollView.scrollTo(0, scrollView.bottom)

        val name = nameText.text.toString()
        val email = emailText.text.toString()
        val mobile = mobileText.text.toString()
        val password = passwordText.text.toString()

        enableUI(false)
        progressBar.visibility = View.VISIBLE

        sendSignUpRequest(name, email, mobile, password)
    }

    private fun sendSignUpRequest(
        name: String,
        email: String,
        mobile: String,
        password: String
    ) {
        val api = APIService.create()
        val call = api.addUser(name, email, mobile, password)
        call.enqueue(object : Callback<RequestResult> {
            override fun onResponse(call: Call<RequestResult>, response: Response<RequestResult>) {
                val success = response.body()?.isSuccess
                val errors = response.body()?.errors
                if(response.isSuccessful && success == true) {
                    onSignUpSuccess()
                } else if (errors != null){
                    Log.d("sign", "errors")
                    Log.d("sign", response.raw().toString())
                    errors.forEach {
                        when(it.code) {
                            "ER_DUP_ENTRY" -> emailText.error = "This email is already used"
                        }
                    }
                    onSignUpFailed()
                } else {
                    Log.d("sign", "unknown error")
                    Log.d("sign", response.raw().toString())
                    onSignUpFailed()
                }
            }
            override fun onFailure(call: Call<RequestResult>, t: Throwable) {
                enableUI(true)
                progressBar.visibility = View.GONE
                Toast.makeText(this@SignUpActivity, "Connection error", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun onSignUpSuccess() {
        Log.d("sign", "Success")
        setResult(Activity.RESULT_OK, null)
        finish()
    }

    private fun onSignUpFailed() {
        Toast.makeText(baseContext, "Sign up failed", Toast.LENGTH_SHORT).show()
        enableUI(true)
        progressBar.visibility = View.GONE
    }

    private fun validate(): Boolean {
        var valid = true

        val name = nameText.text.toString()
        val email = emailText.text.toString()
        val mobile = mobileText.text.toString()
        val password = passwordText.text.toString()
        val reEnterPassword = reEnterPasswordText.text.toString()

        if (name.isEmpty() || name.length < 3) {
            nameText.error = "at least 3 characters"
            valid = false
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailText.error = "enter a valid email address"
            valid = false
        }

        if (mobile.isEmpty() || mobile.length < 9 || mobile.contains("[^0-9 +-]".toRegex())) {
            mobileText.error = "Enter Valid Mobile Number"
            valid = false
        }

        if (password.isEmpty() || password.length < 4 || password.length > 10) {
            passwordText.error = "between 4 and 10 alphanumeric characters"
            valid = false
        }

        if (reEnterPassword.isEmpty() || reEnterPassword.length < 4 || reEnterPassword.length > 10 || reEnterPassword != password) {
            reEnterPasswordText.error = "Password Do not match"
            valid = false
        }

        return valid
    }

    private fun enableUI(state: Boolean) {
        emailText.isEnabled = state
        passwordText.isEnabled = state
        nameText.isEnabled = state
        mobileText.isEnabled = state
        reEnterPasswordText.isEnabled = state
        signUpButton.isEnabled = state
        loginLink.isEnabled = state
    }
}