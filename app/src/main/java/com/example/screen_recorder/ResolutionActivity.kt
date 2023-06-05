package com.example.screen_recorder

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import com.example.screen_recorder.databinding.ActivityResolutionBinding

class ResolutionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResolutionBinding
    private var selectedTextView: TextView? = null
    private var selectedImageButton: ImageButton? = null
    lateinit var selectedText:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityResolutionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backButton.setOnClickListener {
            onBackPressed()
        }
        selectedTextView=binding.text720
        selectedImageButton=binding.text720Button
        binding.auto.setOnClickListener {
            clickOrNot(binding.auto, binding.autoButton)
        }
        binding.text4K.setOnClickListener {
            clickOrNot(binding.text4K, binding.text4KButton)
        }
        binding.text2K.setOnClickListener {
            clickOrNot(binding.text2K, binding.text2KButton)
        }
        binding.text1080.setOnClickListener {
            clickOrNot(binding.text1080, binding.text1080Button)
        }
        binding.text720.setOnClickListener {
            clickOrNot(binding.text720, binding.text720Button)
        }
        binding.text480.setOnClickListener {
            clickOrNot(binding.text480, binding.text480Button)
        }
        binding.text360.setOnClickListener {
            clickOrNot(binding.text360, binding.text360Button)
        }



    }
    private fun clickOrNot(textView: TextView, imageButton: ImageButton) {
        selectedTextView?.setTextColor(Color.WHITE) // Reset the color of the previously selected TextView
        selectedImageButton?.visibility = View.GONE
        textView.setTextColor(Color.parseColor("#F24C9A")) // Set the selected color for the clicked TextView
        imageButton.visibility = View.VISIBLE
        selectedTextView = textView // Update the currently selected TextView
        selectedImageButton = imageButton
        selectedText = textView.text.toString()
    }
    override fun onBackPressed() {
        val resultIntent = Intent()
        resultIntent.putExtra("selectedText", selectedText)
        setResult(Activity.RESULT_OK, resultIntent)
        super.onBackPressed()
    }
}