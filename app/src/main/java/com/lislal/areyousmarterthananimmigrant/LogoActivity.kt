package com.lislal.areyousmarterthananimmigrant

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class LogoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_logo)

        val splashImage: ImageView = findViewById(R.id.splashImage)

        val fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        val fadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out)

        splashImage.startAnimation(fadeIn)

        Handler().postDelayed({
            splashImage.startAnimation(fadeOut)

            startActivity(Intent(this@LogoActivity, MainActivity::class.java))
            finish()
        }, 5000) // 5000 milliseconds = 5 seconds
    }
}