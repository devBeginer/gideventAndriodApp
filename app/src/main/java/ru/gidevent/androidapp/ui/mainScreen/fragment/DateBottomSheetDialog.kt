package ru.gidevent.androidapp.ui.mainScreen.fragment

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import ru.gidevent.andriodapp.databinding.BottomsheetDialogDatePickBinding
import ru.gidevent.androidapp.ui.mainScreen.viewModel.SearchViewModel
import ru.gidevent.androidapp.utils.showSnack
import java.util.Calendar
import java.util.Locale

class DateBottomSheetDialog(private val viewModel: SearchViewModel): BottomSheetDialogFragment() {
    //private val viewModel: SearchViewModel by viewModels({ requireActivity().supportFragmentManager.findFragmentByTag("") })
    //private val viewModel = ViewModelProviders.of(requireActivity())[SearchViewModel::class.java]
    private var _binding: BottomsheetDialogDatePickBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomsheetDialogDatePickBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initData()

    }


    private fun initData() {
        val now = Calendar.getInstance(Locale.getDefault())
        binding.calendarViewBottomDate.date = now.timeInMillis
        binding.calendarViewBottomDate.isClickable = true
        binding.calendarViewBottomDate.setOnDateChangeListener(CalendarView.OnDateChangeListener { calendarView, i, i2, i3 ->
            showSnack(requireView(), "DATA", 5)
        })

        /*binding.btnBottomDate.setOnClickListener {
            viewModel.searchOptions.dateFrom.timeInMillis = binding.calendarViewBottomDate.minDate
            viewModel.searchOptions.dateTo.timeInMillis = binding.calendarViewBottomDate.maxDate
            viewModel.initOptions()
            dismiss()
        }*/
    }
}