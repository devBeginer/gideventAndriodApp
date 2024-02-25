package ru.gidevent.androidapp.ui.mainScreen.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import dagger.hilt.android.AndroidEntryPoint
import ru.gidevent.andriodapp.R
import ru.gidevent.andriodapp.databinding.FragmentMainScreenContainerBinding
import ru.gidevent.androidapp.ui.mainScreen.viewModel.MainViewModel

@AndroidEntryPoint
class MainScreenContainerFragment: Fragment() {

    private val viewModel: MainViewModel by viewModels()

    private var _binding: FragmentMainScreenContainerBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMainScreenContainerBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.btmNavViewMainContainer.setOnItemSelectedListener { item: MenuItem ->
            var selectedFragment = when (item.itemId) {
                R.id.mainFragment -> MainFragment()
                R.id.favouriteFragment -> FavouritesFragment()
                R.id.purchasesFragment -> PurchasesFragment()
                R.id.searchFragment -> SearchFragment()
                R.id.profileFragment -> ProfileFragment()
                else -> null
            }
            if (selectedFragment != null) {
                activity?.supportFragmentManager?.beginTransaction()
                    ?.replace(R.id.main_nav_host_fragment, selectedFragment)?.commit()
            }

            true
        }

        if (savedInstanceState == null) {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.main_nav_host_fragment, MainFragment()).commit()
        }
    }
}