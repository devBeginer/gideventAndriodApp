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
import ru.gidevent.andriodapp.databinding.FragmentFavouritesBinding
import ru.gidevent.androidapp.data.model.mainRecyclerviewModels.AdvertPreviewCard
import ru.gidevent.androidapp.ui.SharedViewModel
import ru.gidevent.androidapp.ui.advertisement.AdvertisementFragment
import ru.gidevent.androidapp.ui.login.fragment.SignInFragment
import ru.gidevent.androidapp.ui.mainScreen.adapter.FavouriteRecyclerViewAdapter
import ru.gidevent.androidapp.ui.mainScreen.viewModel.FavouritesViewModel
import ru.gidevent.androidapp.ui.state.UIStateAdvertList
import ru.gidevent.androidapp.utils.showSnack

@AndroidEntryPoint
class FavouritesFragment : Fragment() {

    private val viewModel: FavouritesViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by viewModels({requireActivity()})

    private var _binding: FragmentFavouritesBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: FavouriteRecyclerViewAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFavouritesBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        initView()
        initObservers()


    }

    override fun onResume() {
        super.onResume()
        viewModel.initView()
    }

    private fun initView(){
        //viewModel.initView()
        adapter = FavouriteRecyclerViewAdapter(listOf(),{
            requireActivity().supportFragmentManager.beginTransaction().replace(R.id.nav_host_fragment, AdvertisementFragment.newInstance(it)).addToBackStack(null).commit()
        },{
            viewModel.postFavourite(it)
        })
        binding.rvFavouriteCards.adapter = adapter

        binding.btnFavouriteSignIn.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment, SignInFragment()).addToBackStack(null).commit()
        }
    }

    private fun initObservers(){

        viewModel.data.observe(viewLifecycleOwner, Observer {
            when(it){
                is UIStateAdvertList.Success<*> -> {
                    sharedViewModel.showProgressIndicator(false)
                    val dataSet = it.data as List<AdvertPreviewCard>?
                    if(dataSet!=null){
                        adapter.setItemsList(dataSet)
                    }
                }
                is UIStateAdvertList.Update<*> -> {
                    sharedViewModel.showProgressIndicator(false)
                    val advert = it.data as AdvertPreviewCard?
                    if(advert!=null){
                        adapter.updateItem(advert)
                    }
                }
                is UIStateAdvertList.Error -> {
                    sharedViewModel.showProgressIndicator(false)
                    showSnack(requireView(), it.message, 5)
                }
                is UIStateAdvertList.ConnectionError -> {
                    sharedViewModel.showProgressIndicator(false)
                    showSnack(requireView(), "Отсутствует интернет подключение", 3)
                }
                is UIStateAdvertList.Idle -> {

                }
                is UIStateAdvertList.Loading -> {
                    sharedViewModel.showProgressIndicator(true)
                }
                is UIStateAdvertList.Unauthorised -> {
                    sharedViewModel.showProgressIndicator(false)
                    binding.rvFavouriteCards.visibility = View.GONE
                    binding.tvFavouriteNotAuth.visibility = View.VISIBLE
                    binding.btnFavouriteSignIn.visibility = View.VISIBLE
                }
                else -> {}
            }
        })
    }
}