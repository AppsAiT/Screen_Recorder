package com.example.screen_recorder

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import com.example.screen_recorder.databinding.ActivitySplashBinding


class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding
    private var CurrentProgress = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Handler().postDelayed({
            animateLogo()
        }, 800)//when the user opens the app, after how much time logo should move upwards

    }

    private fun animateLogo() {
        ObjectAnimator.ofFloat(binding.mainIcon, "translationY", 0f, -120f).apply {
            duration = 2000 //time for which image is moving upwards
            interpolator = AccelerateDecelerateInterpolator()
            start()

            Handler().postDelayed(
                {
                    binding.loadingText.visibility = View.VISIBLE
                    binding.progressBar.visibility = View.VISIBLE
                    loadingProgress()
                }, 2000 //time after which loading screen bar appears
            )
        }
    }

    private fun loadingProgress() {
        val totalProgress = 100
        val updateInterval = 10
        val animationDuration = 3000

        ObjectAnimator.ofInt(binding.progressBar, "progress", 0, totalProgress).apply {
            duration = animationDuration.toLong()
            start()

            Handler().postDelayed({
                startActivity(Intent(this@SplashActivity, MainMenu::class.java))
                finish()
            }, 3400)// time after which next activity should open

        }
    }
}