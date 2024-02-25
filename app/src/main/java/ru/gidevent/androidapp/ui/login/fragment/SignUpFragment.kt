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
import ru.gidevent.androidapp.ui.login.viewModel.SignUpViewModel
import ru.gidevent.androidapp.ui.state.UIState
import ru.gidevent.androidapp.utils.showSnack

@AndroidEntryPoint
class SignUpFragment : Fragment() {

    private val viewModel: SignUpViewModel by viewModels()

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
            if (binding.etSignUpName.toString().isNotEmpty() && binding.etSignUpLastName.toString()
                    .isNotEmpty() && binding.etSignUpLogin.toString()
                    .isNotEmpty() && binding.etSignUpPassword.toString()
                    .isNotEmpty() && binding.etSignUpPasswordRepeat.toString() == binding.etSignUpPassword.toString()
            ) {
                viewModel.register(
                    binding.etSignUpName.toString(),
                    binding.etSignUpLastName.toString(),
                    binding.etSignUpLogin.toString(),
                    binding.etSignUpPassword.toString(),
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
                    showSnack(requireView(), it.message, 5)
                }

                is UIState.ConnectionError -> {
                    showSnack(requireView(), "Отсутствует интернет подключение", 3)
                }

                is UIState.Idle -> {}
                else -> {}
            }
        })

    }
}