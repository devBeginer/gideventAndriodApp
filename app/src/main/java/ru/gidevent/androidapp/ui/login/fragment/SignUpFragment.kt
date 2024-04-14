package ru.gidevent.androidapp.ui.login.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import dagger.hilt.android.AndroidEntryPoint
import ru.gidevent.andriodapp.R
import ru.gidevent.andriodapp.databinding.FragmentSignUpBinding
import ru.gidevent.androidapp.ui.SharedViewModel
import ru.gidevent.androidapp.ui.login.viewModel.SignUpViewModel
import ru.gidevent.androidapp.ui.state.UIState
import ru.gidevent.androidapp.utils.showSnack

@AndroidEntryPoint
class SignUpFragment : Fragment() {

    private val viewModel: SignUpViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by viewModels({requireActivity()})

    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!

    private var registerMode: SignInScreenMode = SignInScreenMode.TOURIST

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tgSignUpRoles.check(R.id.button1)
        binding.tgSignUpRoles.addOnButtonCheckedListener { group, checkedId, isChecked ->
            if (isChecked) {
                when (checkedId) {
                    R.id.button1 -> {
                        registerMode = SignInScreenMode.TOURIST
                        binding.tilSignUpAbout.visibility = View.GONE
                    }

                    R.id.button2 -> {
                        registerMode = SignInScreenMode.SELLER
                        binding.tilSignUpAbout.visibility = View.VISIBLE
                    }
                }
            }
        }
        binding.btnSignUpLogin.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment, SignInFragment()).commit()
        }

        binding.btnSignUpRegister.setOnClickListener {
            if (binding.etSignUpName.text.toString().isNotEmpty()
                && binding.etSignUpLastName.text.toString().isNotEmpty()
                && binding.etSignUpLogin.text.toString().isNotEmpty()
                && binding.etSignUpPassword.text.toString().isNotEmpty()
                && binding.etSignUpPasswordRepeat.text.toString() == binding.etSignUpPassword.text.toString()
                && (registerMode == SignInScreenMode.TOURIST || binding.etSignUpAbout.text.toString().isNotEmpty())
            ) {
                viewModel.register(
                    binding.etSignUpLogin.text.toString(),
                    binding.etSignUpPassword.text.toString(),
                    binding.etSignUpName.text.toString(),
                    binding.etSignUpLastName.text.toString(),
                    binding.etSignUpAbout.text.toString(),
                    registerMode
                )
            } else {
                showSnack(requireView(), "Заполните все поля и попробуйте снова", 4)
            }

        }

        binding.ivSignUpBack.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment, SignInFragment()).commit()
        }

        viewModel.registerState.observe(viewLifecycleOwner, Observer {
            when (it) {
                is UIState.Success<*> -> {
                    requireActivity().supportFragmentManager.beginTransaction()
                        .replace(R.id.nav_host_fragment, SignInFragment()).addToBackStack(null)
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

    }
}