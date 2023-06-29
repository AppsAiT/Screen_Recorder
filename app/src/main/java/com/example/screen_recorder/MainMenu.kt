package com.example.screen_recorder

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.media.projection.MediaProjectionManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.example.screen_recorder.databinding.ActivityMainMenuBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import jp.wasabeef.blurry.Blurry


class MainMenu : AppCompatActivity() {
    private lateinit var mediaProjectionManager: MediaProjectionManager
    private lateinit var binding: ActivityMainMenuBinding
    private val REQUEST_MEDIA_PROJECTION = 1
    var isRecording = false
    private lateinit var popupWindow: PopupWindow

    private lateinit var blurLayout: ViewGroup
    private lateinit var blurImageView: ImageView

    private val overlayPermissionRequestCode = 1001
    private lateinit var sharedPreferences: SharedPreferences // Declare the SharedPreferences variable


    private var isFabOpen = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        blurLayout = findViewById(R.id.blurLayout)
        blurImageView = findViewById(R.id.blurImageView)

        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val isPermissionGranted = sharedPreferences.getBoolean("permissionGranted", false)
        val isAskLaterClicked = sharedPreferences.getBoolean("askLaterClicked", false)
        val isPermissionDenied = sharedPreferences.getBoolean("permissionDenied", false)

        if (!isPermissionGranted && !isAskLaterClicked && !isPermissionDenied) {
            // The user has not made any choice yet, show the pop-up window
            showPopUpWindow()
        } else if (isPermissionGranted) {
            // The user has granted permission, start the service
            startFloatingControlService()
        } else if (isAskLaterClicked) {
            // The user has chosen to be asked later, show the pop-up window
            showPopUpWindow()
        }

//        // Inflate the pop-up layout
//        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
//        val popupView = inflater.inflate(R.layout.floating_notification_prompt, null)
//
//        // Create the PopupWindow
//        popupWindow = PopupWindow(popupView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
//
//        // Set the dismiss listener to handle actions when the pop-up is dismissed
//        popupWindow.setOnDismissListener {
//            // Handle actions when the pop-up is dismissed (e.g., perform certain actions or show another pop-up)
//            blurLayout.visibility = ViewGroup.GONE // Hide the blur effect when the popup is dismissed
//        }
//
//        // Enable outside touch handling to dismiss the PopupWindow
//        popupWindow.isOutsideTouchable = true
//
//        // Find and set click listeners for the buttons in the pop-up
//        val allow_floting_btn = popupView.findViewById<TextView>(R.id.allow_floating_btn)
//        val ask_me_later_btn = popupView.findViewById<TextView>(R.id.aske_me_later_btn)
//        val dnt_allow_floatinf_btn = popupView.findViewById<CheckBox>(R.id.dont_ask_again_btn)
//
//        val argbColor11 = Color.parseColor("#EC77AB")
//        val argbColor12 = Color.parseColor("#7873F5")
//        val shader_color = LinearGradient(
//            ask_me_later_btn.textSize, 0f, ask_me_later_btn.textSize, ask_me_later_btn.textSize,
//            argbColor12, argbColor11,
//            Shader.TileMode.CLAMP
//        )
//        ask_me_later_btn.paint.shader = shader_color
//      //  textView.paint.shader = shader
//
//        allow_floting_btn.setOnClickListener {
//            // Handle click event for Button 1
//            Toast.makeText(this,"Permission Granted",Toast.LENGTH_LONG).show()
//            popupWindow.dismiss()
//            // Show the blur effect
//            blurLayout.visibility = ViewGroup.GONE // Hide the blur effect when the popup is dismissed
//            if(checkHasDrawOverlayPermissions()) {
//                startService(Intent(this, FloatingControlService::class.java))
//            }else{
//                navigateDrawPermissionSetting()
//            }
//
//
//        }
//
//        ask_me_later_btn.setOnClickListener {
//            // Handle click event for Button 2
//            Toast.makeText(this,"Not this Time",Toast.LENGTH_LONG).show()
//            popupWindow.dismiss()
//            // Show the blur effect
//            blurLayout.visibility = ViewGroup.GONE // Hide the blur effect when the popup is dismissed
//        }
//
//        dnt_allow_floatinf_btn.setOnClickListener {
//            // Handle click event for Button 3
//            Toast.makeText(this,"Permission denied",Toast.LENGTH_LONG).show()
//            popupWindow.dismiss()
//            // Show the blur effect
//            blurLayout.visibility = ViewGroup.GONE // Hide the blur effect when the popup is dismissed
//        }
//
//        // Delay the showing of the pop-up window until the activity has finished initializing
//        binding.root.post {
//            // Show the pop-up window
//            popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0)
//            blurBackground()
//        }




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

    // Function to show the pop-up window
    private fun showPopUpWindow() {
        // Rest of the code for showing the pop-up window goes here
        // ...
        // Inflate the pop-up layout
        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView = inflater.inflate(R.layout.floating_notification_prompt, null)

        // Create the PopupWindow
        popupWindow = PopupWindow(popupView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)

        // Set the dismiss listener to handle actions when the pop-up is dismissed
        popupWindow.setOnDismissListener {
            // Handle actions when the pop-up is dismissed (e.g., perform certain actions or show another pop-up)
            blurLayout.visibility = ViewGroup.GONE // Hide the blur effect when the popup is dismissed
        }

        // Enable outside touch handling to dismiss the PopupWindow
        popupWindow.isOutsideTouchable = true

        // Find and set click listeners for the buttons in the pop-up
        val allow_floting_btn = popupView.findViewById<TextView>(R.id.allow_floating_btn)
        val ask_me_later_btn = popupView.findViewById<TextView>(R.id.aske_me_later_btn)
        val dnt_allow_floatinf_btn = popupView.findViewById<CheckBox>(R.id.dont_ask_again_btn)

        val argbColor11 = Color.parseColor("#EC77AB")
        val argbColor12 = Color.parseColor("#7873F5")
        val shader_color = LinearGradient(
            ask_me_later_btn.textSize, 0f, ask_me_later_btn.textSize, ask_me_later_btn.textSize,
            argbColor12, argbColor11,
            Shader.TileMode.CLAMP
        )
        ask_me_later_btn.paint.shader = shader_color
        //  textView.paint.shader = shader

        allow_floting_btn.setOnClickListener {
            // Handle click event for Button 1
            Toast.makeText(this,"Permission Granted",Toast.LENGTH_LONG).show()
            popupWindow.dismiss()
            // Show the blur effect
            blurLayout.visibility = ViewGroup.GONE // Hide the blur effect when the popup is dismissed
            sharedPreferences.edit {
                putBoolean("permissionGranted", true) // Save the permission granted status
                putBoolean("askLaterClicked", false) // Reset the "ask later" status
                putBoolean("permissionDenied", false) // Reset the permission denied status
            }
            startFloatingControlService()


        }

        ask_me_later_btn.setOnClickListener {
            // Handle click event for Button 2
            Toast.makeText(this,"Not this Time",Toast.LENGTH_LONG).show()
            popupWindow.dismiss()
            // Show the blur effect
            blurLayout.visibility = ViewGroup.GONE // Hide the blur effect when the popup is dismissed
            sharedPreferences.edit {
                putBoolean("permissionGranted", false) // Reset the permission granted status
                putBoolean("askLaterClicked", true) // Save the "ask later" status
                putBoolean("permissionDenied", false) // Reset the permission denied status
            }
        }

        dnt_allow_floatinf_btn.setOnClickListener {
            // Handle click event for Button 3
            Toast.makeText(this,"Permission denied",Toast.LENGTH_LONG).show()
            popupWindow.dismiss()
            // Show the blur effect
            blurLayout.visibility = ViewGroup.GONE // Hide the blur effect when the popup is dismissed
            sharedPreferences.edit {
                putBoolean("permissionGranted", false) // Reset the permission granted status
                putBoolean("askLaterClicked", false) // Reset the "ask later" status
                putBoolean("permissionDenied", true) // Save the permission denied status
            }
        }

        // Delay the showing of the pop-up window until the activity has finished initializing
        binding.root.post {
            // Show the pop-up window
            popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0)
            blurBackground()
        }
    }

    // Function to start the floating control service
    private fun startFloatingControlService() {
        // Rest of the code for starting the service goes here
        // ...
        if(checkHasDrawOverlayPermissions()) {
            startService(Intent(this, FloatingControlService::class.java))
        }else{
            navigateDrawPermissionSetting()
        }
    }

    private fun navigateDrawPermissionSetting() {
        val intent1 = Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package:$packageName"))
        startActivityForResult(intent1, REQUEST_CODE_DRAW_PREMISSION)
    }

    private fun checkAndStartService() {
        if(checkHasDrawOverlayPermissions()) {
            startService(Intent(this, FloatingControlService::class.java))
        }
    }

    private fun checkHasDrawOverlayPermissions(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Settings.canDrawOverlays(this)
        }else{
            true
        }
    }



    //Functions to implement Blur when popup screen is shown
    private fun blurBackground() {
        // Capture the current layout as a bitmap
        val rootView = window.decorView.findViewById<ViewGroup>(android.R.id.content)
        val bitmap = getBitmapFromView(rootView)

        // Apply the blur effect to the bitmap
        Blurry.with(this)
            .radius(10)
            .sampling(8)
            .from(bitmap)
            .into(blurImageView)

        // Show the blur effect layout
        blurLayout.visibility = ViewGroup.VISIBLE
    }

    private fun getBitmapFromView(view: View): Bitmap {
        view.isDrawingCacheEnabled = true
        val bitmap = Bitmap.createBitmap(view.drawingCache)
        view.isDrawingCacheEnabled = false
        return bitmap
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
        else if (requestCode == overlayPermissionRequestCode) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Settings.canDrawOverlays(this)) {
                // Permission granted, start the floating view service
                startFloatingViewService()
            } else {
                // Permission denied, show a message or take appropriate action
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }

        if(requestCode == REQUEST_CODE_DRAW_PREMISSION){
            checkAndStartService()
        }

    }

    private fun startFloatingViewService() {
       // startService(Intent(this, FloatingViewService::class.java))
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

    companion object{
        const val  ACTION_STOP_FOREGROUND = "${BuildConfig.APPLICATION_ID}.stopfloating.service"
        const val REQUEST_CODE_DRAW_PREMISSION = 2

    }
}