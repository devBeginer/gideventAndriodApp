package ru.gidevent.androidapp.ui.VKID

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import com.google.gson.Gson
import retrofit2.converter.gson.GsonConverterFactory
import ru.gidevent.androidapp.ui.MainActivity

class AuthActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        /*if (intent != null) {
            val callbackURI = Uri.parse(intent.toString())
            handleCallback(callbackURI)
        }*/

        val data = intent?.data?.getQueryParameter("payload")
        val payload = Gson().fromJson(data, Payload::class.java)

        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    /*private fun handleCallback(uri: Uri) {
        val code = uri.getQueryParameter("code")
        if (code?.isEmpty() == false) {

        }
    }*/


}