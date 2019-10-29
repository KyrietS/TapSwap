package mwo.lab.tapswap.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import mwo.lab.tapswap.R

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