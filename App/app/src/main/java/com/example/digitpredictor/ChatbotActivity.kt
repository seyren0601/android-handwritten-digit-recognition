package com.example.digitpredictor

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.Html
import android.util.Log
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.ComponentActivity
import kotlinx.coroutines.channels.*
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.Buffer
import okio.ByteString
import okio.Utf8
import java.io.IOException
import java.nio.charset.Charset

class ChatbotActivity:ComponentActivity() {
    private lateinit var client: OkHttpClient
    private lateinit var webSocket: WebSocket
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_chatbot)

        val chatbox = findViewById<LinearLayout>(R.id.chatbox)
        val lastTextView = null
        val btnSubmit = findViewById<Button>(R.id.btn_submit)

        val client = OkHttpClient()

        val request = Request.Builder().url("http://10.0.2.2:8000/chatbot").build()




        btnSubmit.setOnClickListener {
            val viewTextInput = findViewById<EditText>(R.id.txt_input)
            val message = viewTextInput.text.toString()
            if(!message.equals("")){
                var textView = TextView(this).apply{
                    layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT).apply {
                        bottomMargin = 20 // marginBottom
                    }
                    textSize=25F
                    text = Html.fromHtml("<font color=#00FF00>USER</font><br>" + message)
                    textAlignment = TextView.TEXT_ALIGNMENT_VIEW_END
                }

                chatbox.addView(textView)

                var chatbotView = TextView(this).apply{
                    layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT).apply {
                        bottomMargin = 20 // marginBottom
                    }
                    textSize = 25F
                    text = Html.fromHtml("<font color=#0000FF>CHATBOT</font><br>")
                }
                chatbox.addView(chatbotView)

//                val listener = com.example.digitpredictor.WebSocketListener(this, chatbox, chatbotView)
//                webSocket = client.newWebSocket(request, listener)
//
//                webSocket.send(message)
                val client = OkHttpClient()
                val request = Request.Builder()
                    .method("GET", null)
                    .url("http://10.0.2.2:8000/chatbot/stream/?request=${message}")
                    .build()

                client.newCall(request).enqueue(object: Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        println("Failed to receive response: ${e.message}")
                    }

                    override fun onResponse(call:Call, response:Response){
                        if (!response.isSuccessful) {
                            println("Unexpected code: ${response.code}")
                            return
                        }

                        response.body?.let{body->
                            val source = body.source()
                            val buffer = Buffer()
                            while(!source.exhausted()){
                                val readBytes = source.read(buffer, Long.MAX_VALUE)
                                val line = buffer.readString(Charset.forName("UTF-8"))
                                //val line = source.readByteString(4)
                                Log.d("debug",line.toString())
                                if(line != null)
                                    runOnUiThread(Runnable {
                                        chatbotView?.text = Html.fromHtml((chatbotView?.text.toString() + line).replace("CHATBOT", "<font color=#0000FF>CHATBOT</font><br>"))
                                    })
                            }
                            body.close()
                        }
                    }
                })
            }
            viewTextInput.setText("")
//            webSocket.close(1000, "done")
        }
    }
}

class WebSocketListener(val context:Activity, val chatbox:LinearLayout, val chatbotView:TextView):WebSocketListener(){
    private var count = 0
    override fun onOpen(webSocket: WebSocket, response: Response) {
        Log.d("WebSocket", "Connected to server")
        // You can send messages after connection is established
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        Log.d("WebSocket", "Message received: $text")
        // Handle the message received from server
        context.runOnUiThread(Runnable {
            chatbotView?.text = chatbotView?.text.toString() + text
        })

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