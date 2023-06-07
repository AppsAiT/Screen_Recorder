package com.example.screen_recorder

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.media.projection.MediaProjectionManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.screen_recorder.databinding.ActivityMainMenuBinding


class MainMenu : AppCompatActivity() {
    private lateinit var mediaProjectionManager: MediaProjectionManager
    private lateinit var binding: ActivityMainMenuBinding
    private val REQUEST_MEDIA_PROJECTION = 1
    var isRecording = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val argbColor1 = Color.parseColor("#EC77AB")
        val argbColor2 = Color.parseColor("#7873F5")
        val shader = LinearGradient(
            binding.res.textSize,
            0f,
            binding.res.textSize,
            binding.res.textSize,
            argbColor2,
            argbColor1,
            Shader.TileMode.CLAMP
        )
        binding.res.paint.shader = shader
        val shader2 = LinearGradient(
            binding.bitrate.textSize,
            0f,
            binding.bitrate.textSize,
            binding.bitrate.textSize,
            argbColor2,
            argbColor1,
            Shader.TileMode.CLAMP
        )
        binding.bitrate.paint.shader = shader2
        val shader3 = LinearGradient(
            binding.framerate.textSize,
            0f,
            binding.framerate.textSize,
            binding.framerate.textSize,
            argbColor2,
            argbColor1,
            Shader.TileMode.CLAMP
        )
        binding.framerate.paint.shader = shader3
//        val customBackground = ResourcesCompat.getDrawable(resources,com.example.screen_recorder.R.drawable.custom_nav,null)
//        binding.bottomAppBar.background=customBackground
//
//        val shapeAppearanceModel =
//            ShapeAppearanceModel().toBuilder().setTopRightCorner(CornerFamily.ROUNDED, 110f)
//                .setTopLeftCorner(CornerFamily.ROUNDED, 110f)
//                .setBottomLeftCorner(CornerFamily.ROUNDED, 130f)
//                .setBottomRightCorner(CornerFamily.ROUNDED, 130f)
//                .build()
//        ViewCompat.setBackground(binding.bottomAppBar, MaterialShapeDrawable(shapeAppearanceModel))

        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                com.example.screen_recorder.R.id.video -> {
                    replaceFragment(SettingsFragment())
                    // Handle selection for menu item 1
                    true
                }

                com.example.screen_recorder.R.id.setting -> {
                    // Handle selection for menu item 2
                    replaceFragment(SettingsFragment())
                    true
                }

                else -> false
            }
        }

        //Ask for permission to start recording (making instance)
        mediaProjectionManager =
            getSystemService(MEDIA_PROJECTION_SERVICE) as MediaProjectionManager

        binding.recordButton.setOnClickListener {
            if (isRecording) {
                isRecording = false
                val stopIntent = Intent(this, ScreenRecordService::class.java)
                stopIntent.action = "com.example.screen_recorder.STOP_RECORDING"
                stopService(stopIntent)
                binding.recordButton.setImageResource(R.drawable.union)
            } else {
                // Start the permission request
                val permissionIntent = mediaProjectionManager.createScreenCaptureIntent()
                startActivityForResult(
                    permissionIntent,REQUEST_MEDIA_PROJECTION
                )
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==REQUEST_MEDIA_PROJECTION){
            if(resultCode== RESULT_OK){
                //Permission granted, start the recording service now
                val serviceIntent = Intent(this, ScreenRecordService::class.java)
                serviceIntent.putExtra(ScreenRecordService.EXTRA_RESULT_CODE, resultCode)
                serviceIntent.putExtra(ScreenRecordService.EXTRA_DATA, data)
                startService(serviceIntent)
                binding.recordButton.setImageResource(R.drawable.baseline_stop_24)
                isRecording = true
            } else {
                Toast.makeText(this,"Permission denied",Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val transaction: FragmentTransaction = fragmentManager.beginTransaction()
        transaction.replace(binding.frameContainer.id, fragment)
        transaction.commit()

    }



    override fun onPause() {
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
    }
}