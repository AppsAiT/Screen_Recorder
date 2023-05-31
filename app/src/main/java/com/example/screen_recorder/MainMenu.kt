package com.example.screen_recorder

import android.R
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.example.screen_recorder.databinding.ActivityMainMenuBinding


class MainMenu : AppCompatActivity() {
    private lateinit var binding: ActivityMainMenuBinding
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
        val customBackground = ResourcesCompat.getDrawable(resources,com.example.screen_recorder.R.drawable.custom_nav,null)
        binding.bottomAppBar.background=customBackground
//
//        val shapeAppearanceModel =
//            ShapeAppearanceModel().toBuilder().setTopRightCorner(CornerFamily.ROUNDED, 110f)
//                .setTopLeftCorner(CornerFamily.ROUNDED, 110f)
//                .setBottomLeftCorner(CornerFamily.ROUNDED, 130f)
//                .setBottomRightCorner(CornerFamily.ROUNDED, 130f)
//                .build()
//        ViewCompat.setBackground(binding.bottomAppBar, MaterialShapeDrawable(shapeAppearanceModel))
    }
}