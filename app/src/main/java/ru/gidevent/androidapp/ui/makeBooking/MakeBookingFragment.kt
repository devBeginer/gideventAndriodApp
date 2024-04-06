package ru.gidevent.androidapp.ui.makeBooking

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.chip.ChipGroup.OnCheckedStateChangeListener
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import dagger.hilt.android.AndroidEntryPoint
import ru.gidevent.andriodapp.R
import ru.gidevent.andriodapp.databinding.FragmentMakeBookingBinding
import ru.gidevent.androidapp.data.model.advertisement.dto.EventTime
import ru.gidevent.androidapp.data.model.booking.BookingParams
import ru.gidevent.androidapp.data.model.booking.BookingParamsResponse
import ru.gidevent.androidapp.data.model.booking.BookingRequest
import ru.gidevent.androidapp.data.model.booking.GroupRequest
import ru.gidevent.androidapp.ui.SharedViewModel
import ru.gidevent.androidapp.ui.mainScreen.fragment.MainScreenContainerFragment
import ru.gidevent.androidapp.ui.state.UIStateMakeBooking
import ru.gidevent.androidapp.utils.Utils
import ru.gidevent.androidapp.utils.Utils.toString
import ru.gidevent.androidapp.utils.showSnack
import java.util.Calendar
import java.util.Locale


@AndroidEntryPoint
class MakeBookingFragment  : Fragment() {
    private val viewModel: MakeBookingViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by viewModels({requireActivity()})
    private var _binding: FragmentMakeBookingBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: BookingPriceRecyclerViewAdapter
    private var id: Long? = null
    private val time: HashMap<Int, EventTime> = hashMapOf()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMakeBookingBinding.inflate(inflater, container, false)
        val view = binding.root

        id = arguments?.getLong("ID")
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initObservers()
    }

    private fun initObservers() {
        viewModel.bookingVariant.observe(viewLifecycleOwner, Observer { it ->
            when(it){
                is UIStateMakeBooking.SuccessBook<*> -> {
                    requireActivity().supportFragmentManager.beginTransaction()
                        .replace(R.id.nav_host_fragment, MainScreenContainerFragment()).addToBackStack(null).commit()
                }
                is UIStateMakeBooking.UpdateUI<*> -> {
                    val bookingParams = it.data as BookingParams
                    time.clear()
                    binding.chipGroupMakeBookingTime.removeAllViews()
                    bookingParams.eventTimeList.forEach{
                        val chip = Chip(requireContext())
                        chip.isCheckable = true
                        chip.id = View.generateViewId()
                        chip.text = it.time.time.toString("HH:mm")
                        time[chip.id] = it
                        binding.chipGroupMakeBookingTime.addView(chip)
                    }

                    adapter.setItemList(bookingParams.price)
                    sharedViewModel.showProgressIndicator(false)
                }
                is UIStateMakeBooking.Error -> {
                    sharedViewModel.showProgressIndicator(false)
                    showSnack(requireView(), it.message, 5)
                }
                is UIStateMakeBooking.ConnectionError -> {
                    sharedViewModel.showProgressIndicator(false)
                    showSnack(requireView(), "Отсутствует интернет подключение", 3)
                }
                is UIStateMakeBooking.Idle -> {

                }

                is UIStateMakeBooking.Loading -> {
                    sharedViewModel.showProgressIndicator(true)
                }
                else -> {}
            }

        })

        viewModel.total.observe(viewLifecycleOwner, Observer {
            binding.tvMakeBookingTotalCost.text = "${it.first} за ${it.second} участников"
            binding.tvMakeBookingTotalCost.visibility = View.VISIBLE
        })
    }

    private fun initView() {
        id?.let { viewModel.initView(it) }
        adapter = BookingPriceRecyclerViewAdapter(listOf(),
            { count, price, id ->
                return@BookingPriceRecyclerViewAdapter if (time[binding.chipGroupMakeBookingTime.checkedChipId]!=null) {
                    var newCount = count - 1
                    newCount = if (newCount >= 0) {
                        viewModel.totalCost -= price
                        viewModel.totalCount--
                        viewModel.priceCount[id] = newCount
                        newCount
                    }else{
                        count
                    }
                    viewModel.postTotal()
                    newCount
                } else {
                    showSnack(
                        requireView(),
                        "Не выбрано время, выберете время и повторите снова",
                        5
                    )
                    0
                }
            },
            { count, price, id ->
                val tmp = time[binding.chipGroupMakeBookingTime.checkedChipId]
                return@BookingPriceRecyclerViewAdapter if (tmp!=null) {
                    val max = time[binding.chipGroupMakeBookingTime.checkedChipId]?.let { time ->
                        viewModel.maxCount[time.timeId]
                    } ?: 0
                    var newCount = if (viewModel.totalCount < max) {
                        viewModel.priceCount[id] = count + 1
                        viewModel.totalCount++
                        viewModel.totalCost += price
                        count + 1
                    } else {
                        count
                    }
                    viewModel.postTotal()
                    newCount
                } else {
                    showSnack(
                        requireView(),
                        "Не выбрано время, выберете время и повторите снова",
                        5
                    )
                    0
                }
            })
        binding.rvMakeBookingPrice.adapter = adapter
        //binding.tvMakeBookingDate.text = "${viewModel.date.time.toString("dd.MM.yyyy")}, ${Utils.dayOfWeekToString(viewModel.date)}"
        binding.tvMakeBookingDate.text = "${viewModel.date.time.toString("dd.MM.yyyy, EE")}"
        binding.tvMakeBookingDateSelect.setOnClickListener {
            showDatePicker()
        }
        binding.btnMakeBooking.setOnClickListener {
            val groups = adapter.dataSet.filter { it.count>0 }.map {
                GroupRequest(0, it.customerCategoryId, it.count, 0)
            }
            if(id!=null && time[binding.chipGroupMakeBookingTime.checkedChipId]!=null){
                id?.let {advertId->
                    time[binding.chipGroupMakeBookingTime.checkedChipId]?.let { time->
                        viewModel.postBooking(
                            BookingRequest(0, time.timeId, advertId, viewModel.date.timeInMillis, viewModel.totalCost, false, groups))
                    }
                }
            }
        }

        binding.chipGroupMakeBookingTime.setOnCheckedStateChangeListener { chipGroup, ints ->
            viewModel.resetPriceList()
            adapter.resetItemList()
        }
    }

    private fun showDatePicker() {
        val today = MaterialDatePicker.todayInUtcMilliseconds()


        // Build constraints.
        val constraintsBuilder =
            CalendarConstraints.Builder()
                .setStart(today)
        val datePickerBuilder =
            MaterialDatePicker.Builder.datePicker()
                .setTitleText("Выберите дату")
                .setCalendarConstraints(constraintsBuilder.build())
        datePickerBuilder.setSelection(viewModel.date.timeInMillis)

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
            id?.let { viewModel.initView(it) }
            //binding.tvMakeBookingDate.text = "${viewModel.date.time.toString("dd.MM.yyyy")}, ${Utils.dayOfWeekToString(viewModel.date)}"
            binding.tvMakeBookingDate.text = "${viewModel.date.time.toString("dd.MM.yyyy, EE")}"
        }
    }

    companion object {
        fun newInstance(id: Long): MakeBookingFragment {
            val fragment = MakeBookingFragment()
            val args = Bundle()
            args.putLong("ID", id)
            fragment.setArguments(args)
            return fragment
        }
    }
}