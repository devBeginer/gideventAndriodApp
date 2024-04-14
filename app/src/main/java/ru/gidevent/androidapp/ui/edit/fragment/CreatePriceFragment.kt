package ru.gidevent.androidapp.ui.edit.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import dagger.hilt.android.AndroidEntryPoint
import ru.gidevent.andriodapp.R
import ru.gidevent.andriodapp.databinding.FragmentCreatePriceBinding
import ru.gidevent.androidapp.data.model.advertisement.PriceRVItem
import ru.gidevent.androidapp.ui.SharedViewModel
import ru.gidevent.androidapp.ui.edit.CreateAdvertViewModel
import ru.gidevent.androidapp.ui.edit.adapter.PriceEditRecyclerViewAdapter
import ru.gidevent.androidapp.ui.mainScreen.fragment.MainScreenContainerFragment
import ru.gidevent.androidapp.ui.state.UIState
import ru.gidevent.androidapp.utils.showSnack

@AndroidEntryPoint
class CreatePriceFragment(/*private val viewModel: CreateAdvertViewModel*/): Fragment() {
    private val viewModel: CreateAdvertViewModel by viewModels({requireParentFragment()})
    private val sharedViewModel: SharedViewModel by viewModels({requireActivity()})

    private var _binding: FragmentCreatePriceBinding? = null
    private val binding get() = _binding!!
    private var currentMode: Int = CREATE_MODE
    private lateinit var priceRVAdapter: PriceEditRecyclerViewAdapter
    companion object {
        const val EDIT_MODE = 0
        const val CREATE_MODE = 1

    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCreatePriceBinding.inflate(inflater, container, false)
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

    private fun initObservers() {
        viewModel.priceList.observe(viewLifecycleOwner, Observer { it ->
            when (it) {
                is UIState.Success<*> -> {
                    val eventTimeList = it.data as List<PriceRVItem>
                    priceRVAdapter.setItemList(eventTimeList)

                    sharedViewModel.showProgressIndicator(false)
                }

                is UIState.Error -> {
                    sharedViewModel.showProgressIndicator(false)
                    showSnack(requireView(), it.message, 5)
                }

                is UIState.ConnectionError -> {
                    sharedViewModel.showProgressIndicator(false)
                    showSnack(requireView(), "Отсутствует интернет подключение", 3)
                }

                is UIState.Idle -> {

                }

                is UIState.Loading -> {

                    sharedViewModel.showProgressIndicator(true)
                }

                else -> {}
            }
        })
    }

    private fun initView() {

        viewModel.initPriceList()
        /*when(currentMode){
            CreateAdvertisementFragment.EDIT_MODE ->{
                binding.rvCreatePricelist.visibility = View.VISIBLE
                binding.tvCreatePricelistTitle.visibility = View.VISIBLE
                binding.rvCreateSchedule.visibility = View.VISIBLE
                binding.tvCreateScheduleTitle.visibility = View.VISIBLE
                viewModel.initFields()
            }
            CreateAdvertisementFragment.CREATE_MODE ->{
                binding.rvCreatePricelist.visibility = View.GONE
                binding.tvCreatePricelistTitle.visibility = View.GONE
                binding.rvCreateSchedule.visibility = View.GONE
                binding.tvCreateScheduleTitle.visibility = View.GONE
            }
        }*/


        priceRVAdapter = PriceEditRecyclerViewAdapter(listOf(), {
            viewModel.delPrice(it)
        }, {
            editPrice(it)
        }, {
            createPrice()
        })

        binding.rvCreatePricelist.adapter = priceRVAdapter



        binding.btnCreatePost.setOnClickListener {

                requireActivity().supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.nav_host_fragment, MainScreenContainerFragment()).addToBackStack(null)
                    .commit()

        }

        binding.toolbarCreatePrice.setNavigationOnClickListener() {
            parentFragmentManager.popBackStack()
            //onBackPressed() // возврат на предыдущий activity
        }

    }

    private fun createPrice() {
        PriceBottomSheetDialog()
            .show(parentFragmentManager/*childFragmentManager*/, "priceBottomSheetDialog")
    }

    private fun editPrice(id: Long) {
        val priceBottomSheetDialog = PriceBottomSheetDialog()
        val bundle = Bundle()
        bundle.putLong("ID", id)
        priceBottomSheetDialog.arguments = bundle
        priceBottomSheetDialog
        priceBottomSheetDialog
            .show(parentFragmentManager/*childFragmentManager*/, "priceBottomSheetDialog")
    }
}