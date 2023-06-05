package com.example.screen_recorder

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import com.example.screen_recorder.databinding.ActivityFramesBinding

class FramesActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFramesBinding
    private var selectedTextView: TextView? = null
    private var selectedImageButton: ImageButton? = null
    lateinit var selectedText: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFramesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backButton.setOnClickListener {
            onBackPressed()
        }
        selectedTextView = binding.textView25
        selectedImageButton = binding.text25Button
        binding.auto.setOnClickListener {
            clickOrNot(binding.textViewAuto, binding.autoButton)
        }
        binding.text24.setOnClickListener {
            clickOrNot(binding.textView24, binding.text24Button)
        }
        binding.text25.setOnClickListener {
            clickOrNot(binding.textView25, binding.text25Button)
        }
        binding.text30.setOnClickListener {
            clickOrNot(binding.textView30, binding.text30Button)
        }
        binding.text35.setOnClickListener {
            clickOrNot(binding.textView35, binding.text35Button)
        }
        binding.text50.setOnClickListener {
            clickOrNot(binding.textView50, binding.text50Button)
        }
        binding.text60.setOnClickListener {
            clickOrNot(binding.textView60, binding.text60Button)
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