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
import ru.gidevent.andriodapp.databinding.FragmentPurchasesBinding
import ru.gidevent.androidapp.data.model.myAdverts.MyBooking
import ru.gidevent.androidapp.ui.SharedViewModel
import ru.gidevent.androidapp.ui.advertisement.CustomerBookingInfoBottomSheet
import ru.gidevent.androidapp.ui.login.fragment.SignInFragment
import ru.gidevent.androidapp.ui.mainScreen.adapter.PurchasesRecyclerViewAdapter
import ru.gidevent.androidapp.ui.mainScreen.viewModel.PurchasesViewModel
import ru.gidevent.androidapp.ui.state.UIStateAdvertList
import ru.gidevent.androidapp.utils.showSnack

@AndroidEntryPoint
class PurchasesFragment : Fragment() {

    private val viewModel: PurchasesViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by viewModels({requireActivity()})

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
            viewModel.initBottomSheet(it)
            CustomerBookingInfoBottomSheet().show(childFragmentManager, "bookingInfoBottomDialog")
        },{
            //viewModel.postFavourite(it)
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
                    sharedViewModel.showProgressIndicator(false)
                    val dataSet = it.data as List<MyBooking>?
                    if(dataSet!=null){
                        adapter.setItemsList(dataSet)
                    }
                }
                is UIStateAdvertList.Update<*> -> {
                    val dataSet = it.data as List<MyBooking>?
                    if(dataSet!=null){
                        adapter.setItemsList(dataSet)
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
                    binding.rvPurchasesCards.visibility = View.GONE
                    binding.tvPurchasesNotAuth.visibility = View.VISIBLE
                    binding.btnPurchasesSignIn.visibility = View.VISIBLE
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