package edu.cit.medil.libtek

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import edu.cit.medil.libtek.util.TokenManager

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnBack = findViewById<Button>(R.id.btnBack)

        btnBack.setOnClickListener {
            // 1. Log the user out using the correct method name from your TokenManager
            val tokenManager = TokenManager(this)
            tokenManager.clearAuthData()

            // 2. Go back to the login screen
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}