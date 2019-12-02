package mwo.lab.tapswap.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.*
import mwo.lab.tapswap.R
import mwo.lab.tapswap.api.APIService
import mwo.lab.tapswap.api.models.LoginResult
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var emailText: EditText
    private lateinit var passwordText: EditText
    private lateinit var loginButton: Button
    private lateinit var signUpLink: TextView
    lateinit var progressBar: ProgressBar

    lateinit var scrollView: ScrollView

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        emailText = findViewById(R.id.input_email)
        passwordText = findViewById(R.id.input_password)
        loginButton = findViewById(R.id.btn_login)
        progressBar = findViewById(R.id.progress_bar)
        signUpLink = findViewById(R.id.link_signup)

        scrollView = findViewById(R.id.scroll_view)

        loginButton.setOnClickListener { login() }

        signUpLink.setOnClickListener {
            // Start the SignUp activity
            val intent = Intent(applicationContext, SignUpActivity::class.java)
            startActivityForResult(intent, REQUEST_SIGN_UP)
            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out)
        }
    }

    /**
     * Wciśnięcie przycisku 'zaloguj'
     */
    private fun login() {
        if (!validate()) {
            onLoginFailed()
            return
        }

        enableUI(false)

        progressBar.visibility = View.VISIBLE

        val email = emailText.text.toString()
        val password = passwordText.text.toString()

        sendLoginRequest(email, password)
    }

    /**
     * Wysłanie do serwera zapytania o token podając email i hasło
     */
    private fun sendLoginRequest(email: String, password: String){
        val api = APIService.create()
        val call = api.login(email, password)
        call.enqueue(object : Callback<LoginResult> {
            override fun onResponse(call: Call<LoginResult>, response: Response<LoginResult>) {
                val success = response.body()?.success ?: false
                if(response.isSuccessful && success) {
                    val token = response.body()!!.data!!.userToken
                    saveToken(token)
                    onLoginSuccess()
                } else if (!success){
                    Snackbar.make(scrollView, "Niepoprawny email lub hasło", Snackbar.LENGTH_LONG).show()
                    onLoginFailed()
                } else {
                    Log.d("login", "unknown error")
                    Log.d("login", response.raw().toString())
                    Toast.makeText(this@LoginActivity, "Login failed", Toast.LENGTH_SHORT).show()
                    onLoginFailed()
                }
            }
            override fun onFailure(call: Call<LoginResult>, t: Throwable) {
                enableUI(true)
                progressBar.visibility = View.GONE
                Toast.makeText(this@LoginActivity, "Connection error", Toast.LENGTH_SHORT).show()
            }
        })
    }

    /**
     * Zapisywanie tokenu w pamięci telefonu
     */
    private fun saveToken(token: String) {
        Log.d("omg", "Zapisałem token: $token")
        val sharedPref = getSharedPreferences("auth", Context.MODE_PRIVATE) ?: return
        with(sharedPref.edit()) {
            putString("userToken", token)
            commit()
        }
    }

    /**
     * Powrót z okna rejestracji
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_SIGN_UP) {
            if (resultCode == Activity.RESULT_OK) {
                Snackbar.make(scrollView, "Pomyślnie założono konto. Możesz się zalogować.", Snackbar.LENGTH_LONG).show()
            }
        }
    }

    /**
     * Wyłączenie przycisku wstecz
     */
    override fun onBackPressed() {
        moveTaskToBack(true)
    }

    /**
     * Logowanie nastąpiło pomyślnie
     */
    private fun onLoginSuccess() {
        Toast.makeText(this, "Zalogowano", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, DashboardActivity::class.java)
        startActivity(intent)
        finish()
    }

    /**
     * Logowanie się nie powiodło
     */
    private fun onLoginFailed() {
        enableUI(true)
        progressBar.visibility = View.INVISIBLE
    }

    /**
     * Walidacja pól formularza
     */
    private fun validate(): Boolean {
        var valid = true

        val email = emailText.text.toString()
        val password = passwordText.text.toString()

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailText.error = "enter a valid email address"
            valid = false
        } else {
            emailText.error = null
        }


        passwordText.error = null

        return valid
    }

    /**
     * Blokowanie UI
     */
    private fun enableUI(state: Boolean) {
        loginButton.isEnabled = state
        emailText.isEnabled = state
        passwordText.isEnabled = state
    }

    companion object {
        private const val REQUEST_SIGN_UP = 0
    }
}