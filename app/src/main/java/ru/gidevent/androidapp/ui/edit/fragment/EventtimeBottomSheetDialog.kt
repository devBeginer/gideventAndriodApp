package ru.gidevent.androidapp.ui.edit.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.gidevent.andriodapp.databinding.BottomsheetDialogEventtimeBinding
import ru.gidevent.androidapp.data.model.advertisement.dto.EventTime
import ru.gidevent.androidapp.ui.edit.CreateAdvertViewModel
import ru.gidevent.androidapp.ui.state.UIState
import ru.gidevent.androidapp.utils.Utils.toString
import ru.gidevent.androidapp.utils.showSnack
import java.util.Calendar
import java.util.Locale

@AndroidEntryPoint
class EventtimeBottomSheetDialog(): BottomSheetDialogFragment() {
    private val viewModel: CreateAdvertViewModel by viewModels({requireParentFragment()})
    private var _binding: BottomsheetDialogEventtimeBinding? = null
    private val binding get() = _binding!!

    private var currentMode = CREATE_MODE

    companion object{
        const val EDIT_MODE = 0
        const val CREATE_MODE = 1
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomsheetDialogEventtimeBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initData()

    }

    private fun initData() {
        binding.tpScheduleTime.setIs24HourView(true)
        
        binding.switchScheduleRepeatable.setOnCheckedChangeListener { compoundButton, isChecked ->
            if(isChecked){
                binding.tvScheduleDatePeriod.text = "Дата проведения"
                binding.hsvScheduleWeekdays.visibility = View.VISIBLE
            }else{
                binding.tvScheduleDatePeriod.text = "Даты проведения"
                binding.hsvScheduleWeekdays.visibility = View.GONE
            }
        }

        binding.tvScheduleTitleDateSelect.setOnClickListener {
            if(!binding.switchScheduleRepeatable.isChecked){
                showDatePicker()
            }else{
                showDateRangePicker()
            }

        }

        binding.btnScheduleCreate.setOnClickListener {
            val startDate = viewModel.editableStartDate
            val endDate = viewModel.editableEndDate
            if((!binding.switchScheduleRepeatable.isChecked
                        || (binding.chipGroupScheduleWeekdays.checkedChipIds.isNotEmpty()
                        && endDate!=null))
                && startDate != null
                /*&& (!binding.switchScheduleRepeatable.isChecked || endDate!=null)*/){
                CoroutineScope(Dispatchers.IO).launch {
                    var time = Calendar.getInstance(Locale.getDefault())
                    time.set(Calendar.HOUR_OF_DAY, binding.tpScheduleTime.hour)
                    time.set(Calendar.MINUTE, binding.tpScheduleTime.minute)
                    time.set(Calendar.SECOND, 0)

                    var weekDays = ""
                    binding.chipGroupScheduleWeekdays.checkedChipIds.forEach {
                        val chip = binding.chipGroupScheduleWeekdays.findViewById<Chip>(it)
                        weekDays += chip.text.toString()
                    }
                    val result = viewModel.createEventTime(
                        EventTime(0,
                            time,
                            binding.switchScheduleRepeatable.isChecked,
                            weekDays,
                            startDate,
                            endDate ?: startDate
                        ))
                    withContext(Dispatchers.Main){
                        when(result){
                            is UIState.Success<*>->{
                                viewModel.initScheduleList()
                                viewModel.editableStartDate = null
                                viewModel.editableEndDate = null
                                dismiss()
                            }
                            is UIState.Loading->{}
                            is UIState.Error->{}
                            is UIState.ConnectionError->{}
                            is UIState.Unauthorised->{}
                            is UIState.Idle->{}
                            else->{}
                        }
                    }
                }


            }
        }

    }

    private fun showDateRangePicker() {
        val today = MaterialDatePicker.todayInUtcMilliseconds()


        // Build constraints.
        val constraintsBuilder =
            CalendarConstraints.Builder()
                .setStart(today)
        val dateRangePickerBuilder =
            MaterialDatePicker.Builder.dateRangePicker()
                .setTitleText("Выберите дату")
                .setCalendarConstraints(constraintsBuilder.build())

        viewModel.editableStartDate?.let { from ->
            viewModel.editableEndDate?.let { to ->
                dateRangePickerBuilder.setSelection(
                    androidx.core.util.Pair(
                        from.timeInMillis,
                        to.timeInMillis
                    )
                )
            }
        }

        val dateRangePicker = dateRangePickerBuilder.build()

        dateRangePicker.show(childFragmentManager, "dateBottomDialog")
        dateRangePicker.addOnPositiveButtonClickListener { dates ->

            var from = Calendar.getInstance(Locale.getDefault())
            from.timeInMillis = dates.first
            viewModel.editableStartDate = from

            var to = Calendar.getInstance(Locale.getDefault())
            to.timeInMillis = dates.second
            viewModel.editableEndDate = to

            viewModel.editableStartDate?.let { from ->
                viewModel.editableEndDate?.let { to ->
                    binding.tvScheduleDatePeriod.visibility = View.VISIBLE
                    binding.tvScheduleDatePeriod.text =
                        "${from.time.toString("dd.MM.yyyy")} - ${to.time.toString("dd.MM.yyyy")}"
                }
            }
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

        viewModel.editableStartDate?.let { from ->
            datePickerBuilder.setSelection(from.timeInMillis)
        }

        val datePicker = datePickerBuilder.build()

        datePicker.show(childFragmentManager, "dateBottomDialog")
        datePicker.addOnPositiveButtonClickListener { dates ->

            var from = Calendar.getInstance(Locale.getDefault())
            from.timeInMillis = dates
            viewModel.editableStartDate = from

            binding.tvScheduleDatePeriod.visibility = View.VISIBLE
            viewModel.editableStartDate?.let { from ->
                binding.tvScheduleDatePeriod.text = "${from.time.toString("dd.MM.yyyy")}"
            }
        }
    }
}