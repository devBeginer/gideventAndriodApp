package ru.gidevent.androidapp.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import dagger.hilt.android.AndroidEntryPoint
import ru.gidevent.andriodapp.R
import ru.gidevent.andriodapp.databinding.ActivityMainBinding
import ru.gidevent.andriodapp.databinding.FragmentFavouritesBinding
import ru.gidevent.androidapp.ui.login.fragment.SignInFragment
import ru.gidevent.androidapp.ui.mainScreen.adapter.FavouriteRecyclerViewAdapter
import ru.gidevent.androidapp.ui.mainScreen.fragment.MainScreenContainerFragment
import ru.gidevent.androidapp.ui.mainScreen.viewModel.FavouritesViewModel

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val viewModel: SharedViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        supportFragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment, MainScreenContainerFragment()).addToBackStack(null)
            .commit()

        viewModel.progressBar.observe(this, Observer {
            if(it){
                binding.mainProgressIndicator.visibility = View.VISIBLE
            }else{
                binding.mainProgressIndicator.visibility = View.GONE
            }
        })

    }
}