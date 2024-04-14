package ru.gidevent.androidapp.ui.seller_management.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import dagger.hilt.android.AndroidEntryPoint
import ru.gidevent.andriodapp.R
import ru.gidevent.andriodapp.databinding.FragmentMyAdvertsBinding
import ru.gidevent.androidapp.data.model.myAdverts.MyAdvert
import ru.gidevent.androidapp.ui.SharedViewModel
import ru.gidevent.androidapp.ui.advertisement.AdvertisementFragment
import ru.gidevent.androidapp.ui.seller_management.adapter.MyAdvertsRecyclerViewAdapter
import ru.gidevent.androidapp.ui.seller_management.viewModel.MyAdvertsViewModel
import ru.gidevent.androidapp.ui.state.UIStateAdvertList
import ru.gidevent.androidapp.utils.showSnack

@AndroidEntryPoint
class MyAdvertsFragment: Fragment() {
    private val viewModel: MyAdvertsViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by viewModels({requireActivity()})

    private var _binding: FragmentMyAdvertsBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: MyAdvertsRecyclerViewAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMyAdvertsBinding.inflate(inflater, container, false)
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
        adapter = MyAdvertsRecyclerViewAdapter(listOf()) {
            /*requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment, AdvertisementFragment.newInstance(it))
                .addToBackStack(null).commit()*/
            parentFragmentManager.beginTransaction().replace(R.id.main_nav_host_fragment, AdvertisementFragment.newInstance(it)).addToBackStack(null).commit()
        }
        binding.rvAdvertsCards.adapter = adapter


        binding.toolbarMyAdverts.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun initObservers(){

        viewModel.data.observe(viewLifecycleOwner, Observer {
            when(it){
                is UIStateAdvertList.Success<*> -> {
                    sharedViewModel.showProgressIndicator(false)
                    val dataSet = it.data as List<MyAdvert>?
                    if(dataSet!=null){
                        adapter.setItemsList(dataSet)
                    }
                }
                is UIStateAdvertList.Update<*> -> {
                    val myAdvert = it.data as MyAdvert?
                    if(myAdvert!=null){
                        adapter.updateItem(myAdvert)
                    }
                    sharedViewModel.showProgressIndicator(false)
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
                is UIStateAdvertList.Unauthorised -> {
                    binding.rvAdvertsCards.visibility = View.GONE
                    sharedViewModel.showProgressIndicator(false)
                }
                is UIStateAdvertList.Loading -> {
                    sharedViewModel.showProgressIndicator(true)
                }
                else -> {}
            }
        } )
    }
}
