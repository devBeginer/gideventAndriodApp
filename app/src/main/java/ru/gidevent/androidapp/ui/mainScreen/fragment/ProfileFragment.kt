package ru.gidevent.androidapp.ui.mainScreen.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import dagger.hilt.android.AndroidEntryPoint
import ru.gidevent.andriodapp.R
import ru.gidevent.andriodapp.databinding.FragmentProfileBinding
import ru.gidevent.androidapp.ui.SharedViewModel
import ru.gidevent.androidapp.ui.edit.fragment.EditContainerFragment
import ru.gidevent.androidapp.ui.login.fragment.SignInFragment
import ru.gidevent.androidapp.ui.mainScreen.viewModel.ProfileViewModel
import ru.gidevent.androidapp.ui.seller_management.fragment.MyAdvertsFragment
import ru.gidevent.androidapp.ui.seller_management.fragment.MyBookingsFragment

@AndroidEntryPoint
class ProfileFragment: Fragment() {
    private val sharedViewModel: SharedViewModel by viewModels({requireActivity()})

    private val viewModel: ProfileViewModel by viewModels()

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
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
        viewModel.data.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                binding.tvMainScreenContainer.text = it.firstName
            }
        })

        binding.btnRepeat.setOnClickListener {
            viewModel.initView()
        }

        binding.btnProfileCreateAdvert.setOnClickListener {
            requireActivity().supportFragmentManager
                .beginTransaction()
                .replace(R.id.nav_host_fragment, EditContainerFragment()).addToBackStack(null)
                .commit()
        }

        binding.btnProfileMyAdvert.setOnClickListener {
            requireActivity().supportFragmentManager
                .beginTransaction()
                .replace(R.id.nav_host_fragment, MyAdvertsFragment()).addToBackStack(null)
                .commit()
        }

        binding.btnProfileMyBookings.setOnClickListener {
            requireActivity().supportFragmentManager
                .beginTransaction()
                .replace(R.id.nav_host_fragment, MyBookingsFragment()).addToBackStack(null)
                .commit()
        }

        binding.btnProfileExit.setOnClickListener {
            viewModel.logout()
            requireActivity().supportFragmentManager
                .beginTransaction()
                .replace(R.id.nav_host_fragment, SignInFragment())
                .commit()
        }
    }
}