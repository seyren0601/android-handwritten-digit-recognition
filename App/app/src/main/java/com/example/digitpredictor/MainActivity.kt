package com.example.digitpredictor

import android.content.res.AssetManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.digitpredictor.ui.theme.DigitPredictorTheme
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONArray
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import java.io.ByteArrayOutputStream
import java.io.File
import java.net.URI
import kotlin.io.path.Path

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
    }
}

interface ApiService{
    @POST("/guess")
    fun postRequest(@Body body:IntArray): Call<Int>
}
