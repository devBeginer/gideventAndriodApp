package ru.gidevent.androidapp.ui.mainScreen.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import dagger.hilt.android.AndroidEntryPoint
import ru.gidevent.andriodapp.R
import ru.gidevent.andriodapp.databinding.FragmentProfileBinding
import ru.gidevent.androidapp.data.model.auth.UserRoles
import ru.gidevent.androidapp.ui.SharedViewModel
import ru.gidevent.androidapp.ui.admin.fragment.AdminCategoryFragment
import ru.gidevent.androidapp.ui.admin.fragment.AdminCustomerFragment
import ru.gidevent.androidapp.ui.admin.fragment.AdminTransportFragment
import ru.gidevent.androidapp.ui.admin.fragment.ModerateAdvertsFragment
import ru.gidevent.androidapp.ui.advertisement.AdvertisementFragment
import ru.gidevent.androidapp.ui.edit.fragment.EditContainerFragment
import ru.gidevent.androidapp.ui.login.fragment.SignInFragment
import ru.gidevent.androidapp.ui.mainScreen.viewModel.ProfileViewModel
import ru.gidevent.androidapp.ui.profileScreen.EditProfileFragment
import ru.gidevent.androidapp.ui.seller_management.fragment.MyAdvertsFragment
import ru.gidevent.androidapp.ui.seller_management.fragment.MyBookingsFragment
import ru.gidevent.androidapp.utils.Utils

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
        sharedViewModel.showProgressIndicator(true)


        viewModel.initView()
        viewModel.data.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                binding.tvProfileName.text = "${it.firstName} ${it.lastName}"
                Glide.with(requireContext())
                    .load("${Utils.IMAGE_URL}${it.photo}")
                    .placeholder(R.drawable.viewpager_item2)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .centerCrop()
                    .into(binding.ivProfileCircularAvatar)

                binding.tvProfileActiveBookingsCount.text = it.bookingsCount.toString()
                binding.tvProfileTodayBookingsCount.text = it.todayBookingsCount.toString()

                if(it.roles.contains(UserRoles.SELLER)){
                    binding.cvProfileCreateSellerAdvert.visibility = View.VISIBLE
                    binding.cvProfileSellerMyAdvert.visibility = View.VISIBLE
                    binding.cvProfileSellerMyBookings.visibility = View.VISIBLE

                    binding.cvProfileSellerAdverts.visibility = View.VISIBLE
                    binding.cvProfileSellerTodayBookings.visibility = View.VISIBLE
                    binding.tvProfileSellerAdvertsCount.text = it.advertsCount.toString()
                    binding.tvProfileSellerTodayBookingsCount.text = it.ordersCount.toString()
                }else{
                    binding.cvProfileCreateSellerAdvert.visibility = View.GONE
                    binding.cvProfileSellerMyAdvert.visibility = View.GONE
                    binding.cvProfileSellerMyBookings.visibility = View.GONE

                    binding.tvProfileSellerAdvertsCount.visibility = View.GONE
                    binding.tvProfileSellerTodayBookingsCount.visibility = View.GONE
                }
                sharedViewModel.showProgressIndicator(false)
            }
        })

        binding.btnRepeat.setOnClickListener {
            viewModel.initView()
        }

        binding.cvProfileCreateSellerAdvert.setOnClickListener {
            requireActivity().supportFragmentManager
                .beginTransaction()
                .replace(R.id.nav_host_fragment, EditContainerFragment()).addToBackStack(null)
                .commit()
        }

        binding.cvProfileSellerMyAdvert.setOnClickListener {
            requireActivity().supportFragmentManager
                .beginTransaction()
                .replace(R.id.nav_host_fragment, MyAdvertsFragment()).addToBackStack(null)
                .commit()
        }

        binding.cvProfileSellerMyBookings.setOnClickListener {
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

        binding.cvProfileEdit.setOnClickListener {
            parentFragmentManager.beginTransaction().replace(R.id.main_nav_host_fragment, EditProfileFragment()).addToBackStack(null).commit()
        }

        binding.cvProfileAdminCategory.visibility = View.VISIBLE
        binding.cvProfileAdminModerateAdvert.visibility = View.VISIBLE
        binding.cvProfileAdminCustomer.visibility = View.VISIBLE
        binding.cvProfileAdminTransport.visibility = View.VISIBLE

        binding.cvProfileAdminCategory.setOnClickListener {
            requireActivity().supportFragmentManager
                .beginTransaction()
                .replace(R.id.nav_host_fragment, AdminCategoryFragment()).addToBackStack(null)
                .commit()
        }

        binding.cvProfileAdminModerateAdvert.setOnClickListener {
            requireActivity().supportFragmentManager
                .beginTransaction()
                .replace(R.id.nav_host_fragment, ModerateAdvertsFragment()).addToBackStack(null)
                .commit()
        }

        binding.cvProfileAdminCustomer.setOnClickListener {
            requireActivity().supportFragmentManager
                .beginTransaction()
                .replace(R.id.nav_host_fragment, AdminCustomerFragment()).addToBackStack(null)
                .commit()
        }

        binding.cvProfileAdminTransport.setOnClickListener {
            requireActivity().supportFragmentManager
                .beginTransaction()
                .replace(R.id.nav_host_fragment, AdminTransportFragment()).addToBackStack(null)
                .commit()
        }
    }
}