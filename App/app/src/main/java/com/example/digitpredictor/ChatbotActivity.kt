package com.example.digitpredictor

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ScrollView
import android.widget.TextView
import androidx.activity.ComponentActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttp
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okhttp3.internal.http.HttpMethod
import okio.ByteString
import java.net.URI

class ChatbotActivity:ComponentActivity() {
    private lateinit var client: OkHttpClient
    private lateinit var webSocket: WebSocket
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_chatbot)

        val scrollView = findViewById<ScrollView>(R.id.chatbox)
        val lastTextView = null
        val btnSubmit = findViewById<Button>(R.id.btn_submit)

        val client = OkHttpClient()

        val request = Request.Builder().url("http://10.0.2.2:8000/chatbot").build()

        val listener = com.example.digitpredictor.WebSocketListener()
        webSocket = client.newWebSocket(request, listener)

        btnSubmit.setOnClickListener {
            val viewTextInput = findViewById<EditText>(R.id.txt_input)
            val message = viewTextInput.text.toString()
            webSocket.send(message)
            client.dispatcher.executorService.shutdown()
        }
    }
}

class WebSocketListener():WebSocketListener(){
    private var count = 0
    override fun onOpen(webSocket: WebSocket, response: Response) {
        Log.d("WebSocket", "Connected to server")
        // You can send messages after connection is established
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        Log.d("WebSocket", "Message received: $text")
        // Handle the message received from server
    }

    override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
        Log.d("WebSocket", "Binary message received: ${bytes.hex()}")
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        Log.d("WebSocket", "Closing connection: $code / $reason")
        webSocket.close(1000, null)
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        Log.e("WebSocket", "Error: ${t.message}")
        // Handle failure
    }
}