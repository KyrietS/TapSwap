package mwo.lab.tapswap.api

import android.content.Context
import android.util.Log
import mwo.lab.tapswap.activities.TapSwap

object Token {
    val userToken: String
            get() {
                val context = TapSwap.appContext
                val prefs = context?.getSharedPreferences("auth", Context.MODE_PRIVATE)
                val token = prefs?.getString("userToken", "") ?: ""
                Log.d("omg", "Odczytałem token: $token")
                return prefs?.getString("userToken", "") ?: ""
            }
}

// "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOjIsIm5hbWUiOiJDemFyZWsiLCJlbWFpbCI6ImFsZUBlbWFpbC5jb20iLCJwaG9uZSI6IjEyMzEyMzEyMyIsImlhdCI6MTU3Mjk1Nzk5N30.xQ501gnv-Yk68U0Yq05qdpl-Ordb2yfAHu5sRQ_MyTs"