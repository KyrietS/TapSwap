package mwo.lab.tapswap.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.*
import mwo.lab.tapswap.R

class LoginActivity : AppCompatActivity() {

    lateinit var emailText: EditText
    lateinit var passwordText: EditText
    lateinit var loginButton: Button
    lateinit var progressBar: ProgressBar
    lateinit var signUpLink: TextView

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

        // TODO: Implement your own authentication logic here.

        android.os.Handler().postDelayed(
            {
                // On complete call either onLoginSuccess or onLoginFailed
                onLoginSuccess()
                // onLoginFailed();
//                progressDialog.dismiss()
            }, 3000
        )
    }

    private fun sendLoginRequest(email: String, password: String): String? {

        return ""
    }

    private fun saveToken(token: String) {

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_SIGN_UP) {
            if (resultCode == Activity.RESULT_OK) {
                Snackbar.make(scrollView, "Pomyślnie założono konto. Możesz się zalogować.", Snackbar.LENGTH_LONG).show()
            }
        }
    }

    override fun onBackPressed() {
        // Disable going back to the MainActivity
        moveTaskToBack(true)
    }

    private fun onLoginSuccess() {
        finish()
    }

    private fun onLoginFailed() {
        Toast.makeText(baseContext, "Login failed", Toast.LENGTH_LONG).show()

        enableUI(true)
    }

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

        if (password.isEmpty() || password.length < 4 || password.length > 10) {
            passwordText.error = "between 4 and 10 alphanumeric characters"
            valid = false
        } else {
            passwordText.error = null
        }

        return valid
    }

    private fun enableUI(state: Boolean) {
        loginButton.isEnabled = state
        emailText.isEnabled = state
        passwordText.isEnabled = state
    }

    companion object {
        private const val REQUEST_SIGN_UP = 0
    }
}