package com.example.digitpredictor

import android.content.res.AssetManager
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
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
import org.jetbrains.kotlinx.multik.api.io.read
import org.jetbrains.kotlinx.multik.api.io.readNPY
import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.ndarray.data.D1
import org.jetbrains.kotlinx.multik.ndarray.data.D2
import org.jetbrains.kotlinx.multik.ndarray.data.D2Array
import org.jetbrains.kotlinx.multik.ndarray.data.Dimension
import org.jetbrains.kotlinx.multik.ndarray.data.NDArray
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
            val file = File("file://android_asset/nn_weights_0_1.npy")

            println(file.exists())
            //val weights_0_1:NDArray<Float, D2> = mk.read("nn_weights_0_1.npy")

            //println(weights_0_1.dim)
        }

        btnClear.setOnClickListener {
            canvasView.clearCanvas()
        }
    }


}
