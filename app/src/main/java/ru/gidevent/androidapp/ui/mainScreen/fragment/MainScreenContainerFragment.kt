package ru.gidevent.androidapp.ui.mainScreen.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
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

    var fragment = "MainFragment"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(
            true // default to enabled
        ) {
            override fun handleOnBackPressed() {

            }
        })
    }

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
                R.id.mainFragment -> {
                    fragment = "MainFragment"
                    MainFragment()
                }
                R.id.favouriteFragment -> {
                    fragment = "FavouritesFragment"
                    FavouritesFragment()
                }
                R.id.purchasesFragment -> {
                    fragment = "PurchasesFragment"
                    PurchasesFragment()
                }
                R.id.searchFragment -> {
                    fragment = "SearchFragment"
                    SearchFragment()
                }
                R.id.profileFragment -> {
                    fragment = "ProfileFragment"
                    ProfileFragment()
                }
                else -> {
                    fragment = "MainFragment"
                    MainFragment()
                }
            }

            /*requireActivity().supportFragmentManager*/childFragmentManager.beginTransaction()
                    .replace(R.id.main_nav_host_fragment, selectedFragment).commit()


            true
        }
        childFragmentManager.beginTransaction()
            .replace(R.id.main_nav_host_fragment, MainFragment()).commit()
        Log.d("SELECTED_ITEM", binding.btmNavViewMainContainer.selectedItemId.toString() )
        /*var selectedFragment = when(binding.btmNavViewMainContainer.selectedItemId){
            R.id.mainFragment -> {
                fragment = "MainFragment"
                MainFragment()
            }
            R.id.favouriteFragment -> {
                fragment = "FavouritesFragment"
                FavouritesFragment()
            }
            R.id.purchasesFragment -> {
                fragment = "PurchasesFragment"
                PurchasesFragment()
            }
            R.id.searchFragment -> {
                fragment = "SearchFragment"
                SearchFragment()
            }
            R.id.profileFragment -> {
                fragment = "ProfileFragment"
                ProfileFragment()
            }
            else -> {
                fragment = "MainFragment"
                MainFragment()
            }
        }
        childFragmentManager.beginTransaction().replace(R.id.main_nav_host_fragment, selectedFragment).commit()*/

        /*if (savedInstanceState == null) {
            *//*requireActivity().supportFragmentManager*//*childFragmentManager.beginTransaction()
                .replace(R.id.main_nav_host_fragment, MainFragment()).commit()
        }else{
            var selectedFragment = when (fragment) {
                "MainFragment" -> MainFragment()
                "FavouritesFragment" -> FavouritesFragment()
                "PurchasesFragment" -> PurchasesFragment()
                "SearchFragment" -> SearchFragment()
                "ProfileFragment" -> ProfileFragment()
                else -> null
            }
            if (selectedFragment != null) {
                *//*requireActivity().supportFragmentManager*//*childFragmentManager.beginTransaction()
                    .replace(R.id.main_nav_host_fragment, selectedFragment).commit()
            }
        }*/
    }

    /*override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("selectedFragment", fragment)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        fragment = savedInstanceState?.getString("selectedFragment") ?: "MainFragment"
    }*/
}