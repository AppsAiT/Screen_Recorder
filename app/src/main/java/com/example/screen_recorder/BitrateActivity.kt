package com.example.screen_recorder

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import com.example.screen_recorder.databinding.ActivityBitrateBinding

class BitrateActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBitrateBinding
    private var selectedTextView: TextView? = null
    private var selectedImageButton: ImageButton? = null
    lateinit var selectedText:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBitrateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backButton.setOnClickListener {
            onBackPressed()
        }
        selectedTextView=binding.Mbps3
        selectedImageButton=binding.Mbps3Button

        binding.auto.setOnClickListener {
            clickOrNot(binding.auto, binding.autoButton)
        }
        binding.Mbps1.setOnClickListener {
            clickOrNot(binding.Mbps1, binding.Mbps1Button)
        }
        binding.Mbps2.setOnClickListener {
            clickOrNot(binding.Mbps2, binding.Mbps2Button)
        }
        binding.Mbps3.setOnClickListener {
            clickOrNot(binding.Mbps3, binding.Mbps3Button)
        }
        binding.Mbps4.setOnClickListener {
            clickOrNot(binding.Mbps4, binding.Mbps4Button)
        }
        binding.Mbps5.setOnClickListener {
            clickOrNot(binding.Mbps5, binding.Mbps5Button)
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