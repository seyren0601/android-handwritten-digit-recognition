package com.example.digitpredictor

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.ComponentActivity
import org.json.JSONArray
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnGuess = findViewById<Button>(R.id.btnGuess)
        val btnClear = findViewById<Button>(R.id.btnClear)
        val canvasView = findViewById<DigitCanvas>(R.id.canvas_digit)

        btnGuess.setOnClickListener {
            var bitMap = canvasView.defaultBitmap
            bitMap = Bitmap.createScaledBitmap(bitMap, 28, 28, false)
            val array = IntArray(28 * 28)

            bitMap.getPixels(array, 0, bitMap.width, 0, 0, bitMap.width, bitMap.height)

            val jsonArray = JSONArray(array)

            val retrofit = Retrofit.Builder()
                // pc
                .baseUrl("http://10.0.2.2:8000")
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            val apiService = retrofit.create(ApiService::class.java)

            apiService.postRequest(array).enqueue(object : Callback<Int> {
                override fun onResponse(call: Call<Int>, response: Response<Int>) {
                    var prediction = response.body().toString()

                    println(response)

                    var txtResult = findViewById<TextView>(R.id.txtGuess)
                    txtResult.text = prediction
                }

                override fun onFailure(call: Call<Int>, t: Throwable) {
                    println(t.message)
                }
            })
        }

        btnClear.setOnClickListener {
            canvasView.clearCanvas()
        }

        val btnToChatBot = findViewById<Button>(R.id.btn_toChatbot)
        btnToChatBot.setOnClickListener {
            val intent = Intent(this, ChatbotActivity::class.java)
            startActivity(intent)
        }
    }
}

interface ApiService{
    @POST("/guess")
    fun postRequest(@Body body:IntArray): Call<Int>
}
