package ru.gidevent.androidapp.ui.advertisement

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import ru.gidevent.andriodapp.databinding.BottomsheetDialogAdvertScheduleBinding
import ru.gidevent.androidapp.data.model.advertisement.AdvertisementCardInfo
import ru.gidevent.androidapp.ui.SharedViewModel
import ru.gidevent.androidapp.ui.advertisement.adapter.AdvertScheduleRecyclerViewAdapter
import ru.gidevent.androidapp.ui.state.UIState
import ru.gidevent.androidapp.utils.showSnack

class AdvertScheduleBottomSheet(val viewModel: AdvertisementViewModel): BottomSheetDialogFragment() {
    //private val viewModel: AdvertisementViewModel by viewModels({requireParentFragment()})
    private val sharedViewModel: SharedViewModel by viewModels({requireActivity()})
    private var _binding: BottomsheetDialogAdvertScheduleBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: AdvertScheduleRecyclerViewAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomsheetDialogAdvertScheduleBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        initData()

    }

    private fun initView() {
        adapter = AdvertScheduleRecyclerViewAdapter(listOf())
        binding.rvAdvertSchedule.adapter = adapter
    }

    override fun onDismiss(dialog: DialogInterface) {
        viewModel.initView()
        super.onDismiss(dialog)
    }


    private fun initData() {
        viewModel.data.observe(viewLifecycleOwner, Observer {
            when (it) {
                is UIState.Success<*> -> {
                    sharedViewModel.showProgressIndicator(false)
                    val dataSet = it.data as AdvertisementCardInfo?
                    if (dataSet != null) {
                        dataSet.eventTimeList?.let { timeList -> adapter.setItemsList(timeList) }
                    }
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

                is UIState.Unauthorised -> {
                    sharedViewModel.showProgressIndicator(false)
                }
                is UIState.Loading ->{
                    sharedViewModel.showProgressIndicator(true)
                }

                else -> {}
            }
        })
    }
}