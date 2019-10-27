package mwo.lab.tapswap;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class LaunchScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch_screen)

        val intent = Intent(this,
                DashboardActivity::class.java)
        startActivity(intent)
        finish()
    }
}