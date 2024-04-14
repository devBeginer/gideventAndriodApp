package ru.gidevent.androidapp.ui.seller_management.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.google.android.material.chip.Chip
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import dagger.hilt.android.AndroidEntryPoint
import ru.gidevent.andriodapp.R
import ru.gidevent.andriodapp.databinding.FragmentMyBookingsBinding
import ru.gidevent.androidapp.data.model.myAdverts.MyBooking
import ru.gidevent.androidapp.ui.SharedViewModel
import ru.gidevent.androidapp.ui.seller_management.adapter.MyBookingsRecyclerViewAdapter
import ru.gidevent.androidapp.ui.seller_management.viewModel.MyBookingsViewModel
import ru.gidevent.androidapp.ui.state.UIStateAdvertList
import ru.gidevent.androidapp.utils.showSnack
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class MyBookingsFragment: Fragment() {
    private val viewModel: MyBookingsViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by viewModels({requireActivity()})

    private var _binding: FragmentMyBookingsBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: MyBookingsRecyclerViewAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMyBookingsBinding.inflate(inflater, container, false)
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
        viewModel.initChips()
    }

    private fun initView(){
        adapter = MyBookingsRecyclerViewAdapter(listOf(), {
            viewModel.initBottomSheet(it)
            BookingInfoBottomSheet().show(childFragmentManager, "bookingInfoBottomDialog")
        },{
            viewModel.confirmFromFragment(it)
        },{
            viewModel.confirmFromFragment(it)
        })
        binding.rvMyBookings.adapter = adapter

        binding.chipGroupMyBookingAdverts.setOnCheckedStateChangeListener { chipGroup, ints ->
            viewModel.advert = viewModel.adverts[chipGroup.checkedChipId] ?: -1L

            viewModel.initView()
        }

        binding.toolbarMyBooking.setOnMenuItemClickListener { menuItem->
            when(menuItem.itemId){
                R.id.my_bookings_calendar->{
                    showDatePicker()
                }
            }
            true
        }


        binding.toolbarMyBooking.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
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
                    val confirmed = it.data as Pair<Long, Boolean>


                    adapter.updateItem(confirmed.second, confirmed.first)

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
                    binding.rvMyBookings.visibility = View.GONE
                    sharedViewModel.showProgressIndicator(false)
                }
                is UIStateAdvertList.Loading -> {
                    sharedViewModel.showProgressIndicator(true)
                }
                else -> {}
            }
        } )

        viewModel.advertsData.observe(viewLifecycleOwner, Observer { advertChipList ->
            binding.chipGroupMyBookingAdverts.removeAllViews()
            viewModel.adverts.clear()
            advertChipList.forEach {
                val chip = Chip(requireContext())
                chip.isCheckable = true
                chip.id = View.generateViewId()
                chip.text = it.name
                viewModel.adverts[chip.id] = it.id
                binding.chipGroupMyBookingAdverts.addView(chip)
            }
            if(viewModel.advert==-1L){
                binding.chipGroupMyBookingAdverts.clearCheck()
            }else{
                viewModel.adverts.forEach{
                    if(it.value==viewModel.advert){
                        binding.chipGroupMyBookingAdverts.check(it.key)
                    }
                }
            }
        })
    }

    private fun showDatePicker() {
        val today = MaterialDatePicker.todayInUtcMilliseconds()
        val constraintsBuilder =
            CalendarConstraints.Builder()
                .setStart(today)
        val datePickerBuilder =
            MaterialDatePicker.Builder.datePicker()
                .setTitleText("Выберите дату")
                .setCalendarConstraints(constraintsBuilder.build())
        viewModel.date?.let {
            datePickerBuilder.setSelection(it.timeInMillis)
        }


        val datePicker = datePickerBuilder.build()

        datePicker.show(childFragmentManager, "dateBottomDialog")
        datePicker.addOnPositiveButtonClickListener { dates ->

            var newDate = Calendar.getInstance(Locale.getDefault())
            newDate.timeInMillis = dates
            newDate.set(Calendar.HOUR_OF_DAY, 0)
            newDate.set(Calendar.SECOND, 0)
            newDate.set(Calendar.MINUTE, 0)
            newDate.set(Calendar.MILLISECOND, 0)
            viewModel.date = newDate
            viewModel.initView()
        }
    }
}
