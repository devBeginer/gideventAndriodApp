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
import ru.gidevent.andriodapp.databinding.FragmentCreateScheduleBinding
import ru.gidevent.androidapp.data.model.advertisement.dto.EventTime
import ru.gidevent.androidapp.ui.SharedViewModel
import ru.gidevent.androidapp.ui.edit.CreateAdvertViewModel
import ru.gidevent.androidapp.ui.edit.adapter.ScheduleEditRecyclerViewAdapter
import ru.gidevent.androidapp.ui.state.UIState
import ru.gidevent.androidapp.utils.showSnack

@AndroidEntryPoint
class CreateScheduleFragment(): Fragment() {
    private val viewModel: CreateAdvertViewModel by viewModels({requireParentFragment()})
    private val sharedViewModel: SharedViewModel by viewModels({requireActivity()})

    private var _binding: FragmentCreateScheduleBinding? = null
    private val binding get() = _binding!!
    private lateinit var scheduleRVAdapter: ScheduleEditRecyclerViewAdapter
    private var currentMode: Int = CREATE_MODE

    companion object {
        const val EDIT_MODE = 0
        const val CREATE_MODE = 1

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCreateScheduleBinding.inflate(inflater, container, false)
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
        viewModel.eventTimeList.observe(viewLifecycleOwner, Observer { it ->
            when (it) {
                is UIState.Success<*> -> {
                    val eventTimeList = it.data as List<EventTime>
                    scheduleRVAdapter.setItemList(eventTimeList)

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

        viewModel.initScheduleList()
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


        binding.toolbarCreateSchedule.setNavigationOnClickListener() {
            parentFragmentManager.popBackStack()
        }
        scheduleRVAdapter = ScheduleEditRecyclerViewAdapter(listOf(), {
            viewModel.delTime(it)
        }, {
            editTime(it)
        }, {
            createTime()
        })
        binding.rvCreateSchedule.adapter = scheduleRVAdapter



        binding.btnCreatePost.setOnClickListener {

            parentFragmentManager
            //requireActivity().supportFragmentManager
            /*requireParentFragment().childFragmentManager*/
                .beginTransaction()
                .replace(R.id.edit_nav_host_fragment, CreatePriceFragment()).addToBackStack(null)
                .commit()

        }


    }

    private fun createTime() {
        EventtimeBottomSheetDialog()
            .show(parentFragmentManager/*childFragmentManager*/, "eventTimeBottomDialog")
    }

    private fun editTime(id: Long) {
        val eventtimeBottomSheetDialog = EventtimeBottomSheetDialog()
        val bundle = Bundle()
        bundle.putLong("ID", id)
        eventtimeBottomSheetDialog.arguments = bundle
        eventtimeBottomSheetDialog
            .show(parentFragmentManager/*childFragmentManager*/, "eventTimeBottomDialog")
    }
}