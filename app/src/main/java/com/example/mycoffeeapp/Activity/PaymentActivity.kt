package com.example.mycoffeeapp.Activity

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.mycoffeeapp.R
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import com.journeyapps.barcodescanner.BarcodeEncoder

class PaymentActivity : AppCompatActivity() {
    private lateinit var tvTotalAmount: TextView
    private lateinit var ivQRCode: ImageView
    private lateinit var tvCountdown: TextView
    private lateinit var btnConfirmPayment: Button
    private var countDownTimer: CountDownTimer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        // Initialize views
        tvTotalAmount = findViewById(R.id.tvTotalAmount)
        ivQRCode = findViewById(R.id.ivQRCode)
        tvCountdown = findViewById(R.id.tvCountdown)
        btnConfirmPayment = findViewById(R.id.btnConfirmPayment)

        // Get total amount from intent
        val totalAmount = intent.getDoubleExtra("total_amount", 0.0)
        tvTotalAmount.text = "Tổng tiền: ${totalAmount}đ"

        // Generate QR code
        generateQRCode(totalAmount.toString())

        // Start countdown timer
        startCountdown()

        // Set click listener for confirm payment button
        btnConfirmPayment.setOnClickListener {
            val intent = Intent(this, ActivityChecking::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun generateQRCode(content: String) {
        try {
            val multiFormatWriter = MultiFormatWriter()
            val bitMatrix: BitMatrix = multiFormatWriter.encode(
                content,
                BarcodeFormat.QR_CODE,
                800,
                800
            )
            val barcodeEncoder = BarcodeEncoder()
            val bitmap: Bitmap = barcodeEncoder.createBitmap(bitMatrix)
            ivQRCode.setImageBitmap(bitmap)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun startCountdown() {
        countDownTimer = object : CountDownTimer(15 * 60 * 1000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val minutes = millisUntilFinished / 1000 / 60
                val seconds = millisUntilFinished / 1000 % 60
                tvCountdown.text = String.format("Thời gian còn lại: %02d:%02d", minutes, seconds)
            }

            override fun onFinish() {
                tvCountdown.text = "Hết thời gian thanh toán"
                btnConfirmPayment.isEnabled = false
            }
        }.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer?.cancel()
    }
} 