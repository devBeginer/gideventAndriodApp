package ru.gidevent.androidapp.ui.VKID

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import ru.gidevent.androidapp.ui.MainActivity
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import ru.gidevent.andriodapp.R
import ru.gidevent.androidapp.ui.mainScreen.fragment.MainScreenContainerFragment
import ru.gidevent.androidapp.ui.state.UIState
import ru.gidevent.androidapp.utils.showSnack

@AndroidEntryPoint
class AuthActivity: AppCompatActivity() {

    private val viewModel: VkAuthViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val data = intent?.data?.getQueryParameter("payload")
        val payload = Gson().fromJson(data, Payload::class.java)

        val verifier = PKCEUtil.getCodeVerifier()
        Toast.makeText(this, verifier, Toast.LENGTH_LONG).show()
        viewModel.login(payload.token, /*verifier*/payload.uuid)

        viewModel.loginState.observe(this, Observer {
            when(it){
                is UIState.Success<*> -> {
                    val newIntent = Intent(this, MainActivity::class.java)
                    startActivity(newIntent)
                }
                is UIState.Error -> {
                    Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                    val newIntent = Intent(this, MainActivity::class.java)
                    startActivity(newIntent)
                }
                is UIState.ConnectionError -> {
                    Toast.makeText(this, "Отсутствует интернет подключение", Toast.LENGTH_LONG).show()
                    val newIntent = Intent(this, MainActivity::class.java)
                    startActivity(newIntent)
                }
                is UIState.Idle -> {}

                is UIState.Loading -> {
                }
                else -> {}
            }
        })
    }
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        /*if (intent != null) {
            val callbackURI = Uri.parse(intent.toString())
            handleCallback(callbackURI)
        }*/

        /*val data = intent?.data?.getQueryParameter("payload")
        val payload = Gson().fromJson(data, Payload::class.java)

        Toast.makeText(this, payload.user.toString(), Toast.LENGTH_LONG).show()

        val newIntent = Intent(this, MainActivity::class.java)
        startActivity(newIntent)*/
    }

    /*private fun handleCallback(uri: Uri) {
        val code = uri.getQueryParameter("code")
        if (code?.isEmpty() == false) {

        }
    }*/


}