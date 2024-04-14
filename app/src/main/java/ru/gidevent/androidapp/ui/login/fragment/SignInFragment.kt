package ru.gidevent.androidapp.ui.login.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.browser.customtabs.CustomTabsIntent
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import dagger.hilt.android.AndroidEntryPoint
import ru.gidevent.andriodapp.R
import ru.gidevent.andriodapp.databinding.FragmentSignInBinding
import ru.gidevent.androidapp.ui.SharedViewModel
import ru.gidevent.androidapp.ui.VKID.PKCEUtil
import ru.gidevent.androidapp.ui.mainScreen.fragment.MainScreenContainerFragment
import ru.gidevent.androidapp.ui.login.viewModel.SignInViewModel
import ru.gidevent.androidapp.ui.state.UIState
import ru.gidevent.androidapp.utils.showSnack
import java.util.UUID

@AndroidEntryPoint
class SignInFragment : Fragment() {

    private val viewModel: SignInViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by viewModels({requireActivity()})

    private var _binding: FragmentSignInBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSignInBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedViewModel.showProgressIndicator(false)

        binding.btnSignInRegister.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction().addToBackStack(null)
                .replace(R.id.nav_host_fragment, SignUpFragment()).commit()
        }

        binding.ivSignInBack.setOnClickListener {
            /*requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment, MainScreenContainerFragment()).commit()*/
            requireActivity().supportFragmentManager.popBackStack()
        }

        binding.btnSignInLogin.setOnClickListener {
            /*requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment, MainScreenContainer()).addToBackStack(null).commit()*/
            if (binding.etSignInLogin.text.toString().isNotEmpty()
                && binding.etSignInPassword.text.toString().isNotEmpty()
            ) {
                sharedViewModel.showProgressIndicator(true)
                viewModel.login(
                    binding.etSignInLogin.text.toString(),
                    binding.etSignInPassword.text.toString()
                )
            }
        }

        binding.etSignInPassword.setOnEditorActionListener { textView, actionId, keyEvent ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (binding.etSignInLogin.text.toString().isNotEmpty()
                    && binding.etSignInPassword.text.toString().isNotEmpty()
                ) {
                    viewModel.login(
                        binding.etSignInLogin.text.toString(),
                        binding.etSignInPassword.text.toString()
                    )
                }
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }

        viewModel.loginState.observe(viewLifecycleOwner, Observer {
            when(it){
                is UIState.Success<*> -> {
                    requireActivity().supportFragmentManager.beginTransaction()
                        .replace(R.id.nav_host_fragment, MainScreenContainerFragment())/*.addToBackStack(null)*/
                        .commit()
                }
                is UIState.Error -> {
                    sharedViewModel.showProgressIndicator(false)
                    showSnack(requireView(), it.message, 5)
                }
                is UIState.ConnectionError -> {
                    sharedViewModel.showProgressIndicator(false)
                    showSnack(requireView(), "Отсутствует интернет подключение", 3)
                }
                is UIState.Idle -> {}

                is UIState.Loading -> {
                    sharedViewModel.showProgressIndicator(true)
                }
                else -> {}
            }
        })

        binding.btnSignInOthers.setOnClickListener {
            openWebPage()
        }


        /*viewModel.loginResult.observe(viewLifecycleOwner, Observer {
            if (it) {
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.nav_host_fragment, MainScreenContainerFragment()).addToBackStack(null)
                    .commit()
            } else {
                showLongToast(requireContext(), "Проверьте логин или пароль")
            }
        })*/
    }

    private fun openWebPage() {
        val url = "https://id.vk.com/auth?" +
                "state=${PKCEUtil.generateState()}" +
                "&response_type=silent_token" +
                "&code_challenge=${PKCEUtil.getCodeChallenge()}" +
                "&code_challenge_method=sha256" +
                "&app_id=51808635" +
                "&v=0.0.2" +
                //"&redirect_uri=ru.gidevent.androidapp:/vk51808635/vk.com" +
                //"&redirect_uri=ru.gidevent.androidapp://vk51808635/vk.com" +
                //"&redirect_uri=vk51808635://vk.com" +
                "&redirect_uri=content://ru.gidevent.androidapp:200/vk51808635/vk.com" +
                //"&redirect_uri=vk51808635:/vk.com" +
                "&uuid=${UUID.randomUUID()}"

        val builder = CustomTabsIntent.Builder( )
        builder.setShowTitle(true)
        builder.setInstantAppsEnabled(true)
        val customBuilder = builder.build()
        customBuilder.launchUrl(requireContext(), Uri.parse(url))

        //val webpage: Uri = Uri.parse(url)
        //val intent = Intent(Intent.ACTION_VIEW, webpage)
        //if (intent.resolveActivity(packageManager) != null) {
            //startActivity(intent)
        //}
    }
}