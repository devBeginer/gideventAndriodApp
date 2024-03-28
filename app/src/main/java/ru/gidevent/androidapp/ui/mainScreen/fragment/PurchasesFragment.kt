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
import ru.gidevent.andriodapp.databinding.FragmentMainBinding
import ru.gidevent.andriodapp.databinding.FragmentPurchasesBinding
import ru.gidevent.androidapp.data.model.mainRecyclerviewModels.AdvertPreviewCard
import ru.gidevent.androidapp.data.model.mainRecyclerviewModels.HeaderViewpagerItem
import ru.gidevent.androidapp.data.model.mainRecyclerviewModels.MainRecyclerViewData
import ru.gidevent.androidapp.ui.advertisement.AdvertisementFragment
import ru.gidevent.androidapp.ui.login.fragment.SignInFragment
import ru.gidevent.androidapp.ui.mainScreen.adapter.FavouriteRecyclerViewAdapter
import ru.gidevent.androidapp.ui.mainScreen.adapter.MainRecyclerViewAdapter
import ru.gidevent.androidapp.ui.mainScreen.adapter.PurchasesRecyclerViewAdapter
import ru.gidevent.androidapp.ui.mainScreen.viewModel.MainViewModel
import ru.gidevent.androidapp.ui.mainScreen.viewModel.PurchasesViewModel
import ru.gidevent.androidapp.ui.state.UIState
import ru.gidevent.androidapp.ui.state.UIStateAdvertList
import ru.gidevent.androidapp.utils.showSnack

@AndroidEntryPoint
class PurchasesFragment : Fragment() {

    private val viewModel: PurchasesViewModel by viewModels()

    private var _binding: FragmentPurchasesBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: PurchasesRecyclerViewAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPurchasesBinding.inflate(inflater, container, false)
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
        adapter = PurchasesRecyclerViewAdapter(listOf(), {
            requireActivity().supportFragmentManager.beginTransaction().replace(R.id.nav_host_fragment, AdvertisementFragment.newInstance(it)).addToBackStack(null).commit()
        },{
            viewModel.postFavourite(it)
        })
        binding.rvPurchasesCards.adapter = adapter

        binding.btnPurchasesSignIn.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment, SignInFragment()).addToBackStack(null).commit()
        }
    }

    private fun initObservers(){

        viewModel.data.observe(viewLifecycleOwner, Observer {
            when(it){
                is UIStateAdvertList.Success<*> -> {
                    val dataSet = it.data as List<AdvertPreviewCard>?
                    if(dataSet!=null){
                        adapter.setItemsList(dataSet)
                    }
                }
                is UIStateAdvertList.Update<*> -> {
                    val advert = it.data as AdvertPreviewCard?
                    if(advert!=null){
                        adapter.updateItem(advert)
                    }
                }
                is UIStateAdvertList.Error -> {
                    showSnack(requireView(), it.message, 5)
                }
                is UIStateAdvertList.ConnectionError -> {
                    showSnack(requireView(), "Отсутствует интернет подключение", 3)
                }
                is UIStateAdvertList.Idle -> {

                }
                is UIStateAdvertList.Unauthorised -> {
                    binding.rvPurchasesCards.visibility = View.GONE
                    binding.tvPurchasesNotAuth.visibility = View.VISIBLE
                    binding.btnPurchasesSignIn.visibility = View.VISIBLE
                }
                else -> {}
            }
        } )
    }
}