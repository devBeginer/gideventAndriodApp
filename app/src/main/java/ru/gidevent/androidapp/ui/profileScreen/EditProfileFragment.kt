package ru.gidevent.androidapp.ui.profileScreen

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.gidevent.andriodapp.R
import ru.gidevent.andriodapp.databinding.FragmentEditProfileBinding
import ru.gidevent.androidapp.data.model.advertisement.response.ResponsePoster
import ru.gidevent.androidapp.data.model.auth.UserRoles
import ru.gidevent.androidapp.data.model.auth.response.EditProfile
import ru.gidevent.androidapp.ui.SharedViewModel
import ru.gidevent.androidapp.ui.state.UIState
import ru.gidevent.androidapp.ui.state.UIStateEditProfile
import ru.gidevent.androidapp.utils.Utils
import ru.gidevent.androidapp.utils.showSnack
import java.util.UUID


@AndroidEntryPoint
class EditProfileFragment: Fragment() {
    private val sharedViewModel: SharedViewModel by viewModels({requireActivity()})

    private val viewModel: EditViewModel by viewModels()

    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!
    private var image = ""
    private var mode = UserRoles.USER
    private var roles = setOf(UserRoles.USER)
    private var vkUser = false
    private val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if(uri!=null){
            CoroutineScope(Dispatchers.IO).launch {
                val result = viewModel.postPhoto(uri)
                withContext(Dispatchers.Main){
                    when (result) {
                        is UIState.Success<*> -> {
                            val poster = result.data as ResponsePoster?
                            if (poster != null) {
                                image = poster.fileUUID
                                Glide.with(requireContext())
                                    .load(/*${Utils.IMAGE_URL}*/"${image}")
                                    .placeholder(R.drawable.avatar_placeholder)
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .centerCrop()
                                    .into(binding.ivEditProfileCircularAvatar)
                            }
                        }

                        is UIState.Error -> {
                            showSnack(requireView(), result.message, 5)
                        }

                        is UIState.ConnectionError -> {
                            showSnack(requireView(), "Отсутствует интернет подключение", 3)
                        }

                        is UIState.Idle -> {

                        }

                        else -> {}
                    }
                }
            }
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.initView()

        binding.toolbarEditProfile.setNavigationOnClickListener() {
            parentFragmentManager.popBackStack()
        }
        binding.ivEditProfileEditPhoto.setOnClickListener {
            getContent.launch("image/*")
        }

        binding.btnEditProfileRegister.setOnClickListener {
            if (binding.etEditProfileName.text.toString().isNotEmpty()
                && binding.etEditProfileLogin.text.toString().isNotEmpty()
                && binding.etEditProfileLastName.text.toString().isNotEmpty()
                && (vkUser || binding.etEditProfilePassword.text.toString().isNotEmpty())
                && binding.etEditProfilePasswordRepeat.text.toString() == binding.etEditProfilePassword.text.toString()
                && (mode == UserRoles.USER || binding.etEditProfileAbout.text.toString().isNotEmpty())
            ) {
                if(vkUser && binding.etEditProfilePassword.text.toString().isEmpty() || binding.etEditProfilePassword.text.toString().matches(Regex(Utils.PASSWORD_PATTERN))){
                    viewModel.update(
                        binding.etEditProfileLogin.text.toString(),
                        if(vkUser && binding.etEditProfilePassword.text.toString().isEmpty()) UUID.randomUUID().toString() else binding.etEditProfilePassword.text.toString(),
                        binding.etEditProfileName.text.toString(),
                        binding.etEditProfileLastName.text.toString(),
                        binding.etEditProfileAbout.text.toString(),
                        roles,
                        image,
                        vkUser
                    )
                }else {
                    showSnack(requireView(), "Пароль должен содержать не меньше 8 символов, хотя бы 1 цифру и не иметь пробелов", 4)
                }
            } else {
                showSnack(requireView(), "Заполните все поля и попробуйте снова", 4)
            }

        }

        binding.btnEditProfileBecomeSeller.setOnClickListener {
            viewModel.becomeSeller()
        }

        viewModel.data.observe(viewLifecycleOwner, Observer {
            when (it) {
                is UIStateEditProfile.Success<*> -> {
                    val editProfile = it.data as EditProfile?
                    if(editProfile!=null){
                        binding.etEditProfileName.setText(editProfile.firstName)
                        binding.etEditProfileLastName.setText(editProfile.lastName)
                        binding.etEditProfileLogin.setText(editProfile.login)
                        if(editProfile.roles.contains(UserRoles.SELLER)){
                            binding.tilEditProfileAbout.visibility = View.VISIBLE
                            binding.btnEditProfileBecomeSeller.visibility = View.GONE
                            binding.etEditProfileAbout.setText(editProfile.about)
                            mode = UserRoles.SELLER
                            roles = editProfile.roles
                        }else{
                            binding.tilEditProfileAbout.visibility = View.GONE
                            binding.btnEditProfileBecomeSeller.visibility = View.VISIBLE
                            mode = UserRoles.USER
                            roles = editProfile.roles
                        }
                        vkUser = editProfile.vkUser
                        image = editProfile.photo
                        Glide.with(requireContext())
                            .load(/*${Utils.IMAGE_URL}*/"${editProfile.photo}")
                            .placeholder(R.drawable.avatar_placeholder)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .centerCrop()
                            .into(binding.ivEditProfileCircularAvatar)
                    }

                    sharedViewModel.showProgressIndicator(false)
                }
                is UIStateEditProfile.SuccessUpdate -> {
                    showSnack(requireView(), "Данные успешно обновлены", 5)

                    sharedViewModel.showProgressIndicator(false)
                }

                is UIStateEditProfile.Error -> {
                    sharedViewModel.showProgressIndicator(false)
                    showSnack(requireView(), it.message, 5)
                }

                is UIStateEditProfile.ConnectionError -> {
                    sharedViewModel.showProgressIndicator(false)
                    showSnack(requireView(), "Отсутствует интернет подключение", 3)
                }

                is UIStateEditProfile.Idle -> {}

                is UIStateEditProfile.Loading -> {
                    sharedViewModel.showProgressIndicator(true)
                }
                else -> {}
            }

        })


    }
}