package ru.gidevent.androidapp.ui.profileScreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import ru.gidevent.andriodapp.databinding.FragmentEditProfileBinding
import ru.gidevent.androidapp.ui.SharedViewModel


@AndroidEntryPoint
class EditProfileFragment: Fragment() {
    private val sharedViewModel: SharedViewModel by viewModels({requireActivity()})

    //private val viewModel: ProfileViewModel by viewModels()

    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!
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

        binding.toolbarEditProfile.setNavigationOnClickListener() {
            parentFragmentManager.popBackStack()
        }
    }
}