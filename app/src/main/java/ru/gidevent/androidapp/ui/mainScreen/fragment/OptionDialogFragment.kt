package ru.gidevent.androidapp.ui.mainScreen.fragment

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.core.view.children
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import com.google.android.material.chip.Chip
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import ru.gidevent.RestAPI.model.Category
import ru.gidevent.RestAPI.model.City
import ru.gidevent.RestAPI.model.TransportationVariant
import ru.gidevent.andriodapp.R
import ru.gidevent.andriodapp.databinding.DialogFragmentOptionsBinding
import ru.gidevent.androidapp.data.model.search.OptionsVariants
import ru.gidevent.androidapp.data.model.suggestionsRecyclerviewModels.SuggestionRecyclerViewData
import ru.gidevent.androidapp.ui.mainScreen.viewModel.SearchViewModel
import ru.gidevent.androidapp.ui.state.UIState
import ru.gidevent.androidapp.utils.Utils.toString
import ru.gidevent.androidapp.utils.showSnack
import java.util.Calendar
import java.util.Locale

class OptionDialogFragment(private val viewModel: SearchViewModel) : DialogFragment() {

    //private val viewModel: SearchViewModel by viewModels({ requireParentFragment() })

    private var _binding: DialogFragmentOptionsBinding? = null
    private val binding get() = _binding!!

    private val categories: HashMap<Int, Category> = hashMapOf()
    private val transport: HashMap<Int, TransportationVariant> = hashMapOf()
    private val ages: HashMap<Int, Int> = hashMapOf()
    private var city: City? = null
    private var dateFrom: Calendar? = null
    private var dateTo: Calendar? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogFragmentOptionsBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /*setStyle(
            STYLE_NORMAL,
            android.R.style.Theme_Black_NoTitleBar_Fullscreen
        )
        setStyle(
            STYLE_NORMAL,
            android.R.style.Theme_Material_Light_NoActionBar_Fullscreen
        )
        setStyle(
            STYLE_NORMAL,
            android.R.style.Theme
        )*/
        /*setStyle(
            STYLE_NORMAL,
            R.style.FullScreenDialog
        )*/
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initData()
        initObservers()

    }

    private fun initData() {

        viewModel.initOptions()

        binding.sliderOptionsPrice.setLabelFormatter { value: Float ->
            "${value}₽"
            when {
                value < 50000 -> {
                    "${value}₽"
                }

                else -> {
                    "${value}₽+"
                }
            }
        }
        binding.sliderOptionsPrice.valueFrom = 0f
        binding.sliderOptionsPrice.valueTo = 50000f
        //binding.sliderOptionsPrice.values = listOf(viewModel.defaultSearchOptions.priceFrom, viewModel.defaultSearchOptions.priceTo)

        binding.sliderOptionsPrice.addOnChangeListener { slider, value, fromUser ->
            if (fromUser) {
                val newValue = viewModel.searchOptions.value

                if (newValue != null) {
                    newValue.priceFrom = slider.values[0]
                    newValue.priceTo = slider.values[1]
                    viewModel.postSearchOptions(newValue)
                }
            }
        }

        binding.sliderOptionsDuration.setLabelFormatter { value: Float ->
            "${value}ч"
        }
        binding.sliderOptionsDuration.valueFrom = 0f
        binding.sliderOptionsDuration.valueTo = 72f
        //binding.sliderOptionsDuration.values = listOf(viewModel.defaultSearchOptions.durationFrom, viewModel.defaultSearchOptions.durationTo)

        binding.sliderOptionsDuration.addOnChangeListener { slider, value, fromUser ->
            if (fromUser) {
                val newValue = viewModel.searchOptions.value

                if (newValue != null) {
                    newValue.durationFrom = slider.values[0]
                    newValue.durationTo = slider.values[1]
                    viewModel.postSearchOptions(newValue)
                }
            }
        }

        binding.sliderOptionsRating.setLabelFormatter { value: Float ->
            "${value}"
        }
        binding.sliderOptionsRating.valueFrom = 0f
        binding.sliderOptionsRating.valueTo = 5f
        //binding.sliderOptionsRating.values = listOf(viewModel.defaultSearchOptions.ratingFrom, viewModel.defaultSearchOptions.ratingTo)

        binding.sliderOptionsRating.addOnChangeListener { slider, value, fromUser ->
            if (fromUser) {
                val newValue = viewModel.searchOptions.value

                if (newValue != null) {
                    newValue.ratingFrom = slider.values[0]
                    newValue.ratingTo = slider.values[1]
                    viewModel.postSearchOptions(newValue)
                }
            }
        }

        binding.switchOptionsIndividual.setOnCheckedChangeListener { compoundButton, isChecked ->
            val newValue = viewModel.searchOptions.value

            if (newValue != null) {
                newValue.isIndividual = isChecked
                /*if (!binding.switchOptionsGroup.isChecked && isChecked) {
                    newValue.isIndividual = true
                } else if (binding.switchOptionsGroup.isChecked && !isChecked) {
                    newValue.isIndividual = false
                } else {
                    newValue.isIndividual = null
                }*/
                viewModel.postSearchOptions(newValue)
            }
        }



        binding.switchOptionsGroup.setOnCheckedChangeListener { compoundButton, isChecked ->
            val newValue = viewModel.searchOptions.value

            if (newValue != null) {
                newValue.isGroup = isChecked
                /*if (!isChecked && binding.switchOptionsIndividual.isChecked) {
                    newValue.isIndividual = true
                } else if (isChecked && !binding.switchOptionsIndividual.isChecked) {
                    newValue.isIndividual = false
                } else {
                    newValue.isIndividual = null
                }*/
                viewModel.postSearchOptions(newValue)
            }
        }




        binding.chipGroupDate.setOnCheckedStateChangeListener { chipGroup, ids ->
            ids.forEach { id ->
                when (id) {
                    R.id.chip_today -> {
                        val newValue = viewModel.searchOptions.value

                        if (newValue != null) {
                            newValue.dateFrom = Calendar.getInstance(Locale.getDefault())
                            newValue.dateTo = Calendar.getInstance(Locale.getDefault())
                            newValue.dateFrom?.let { from ->
                                newValue.dateTo?.let { to ->
                                    binding.tvOptionsDatePeriod.text =
                                        "${from.time.toString("dd.MM.yyyy")} - ${to.time.toString("dd.MM.yyyy")}"
                                }
                            }
                            binding.chipGroupDate.visibility = View.GONE
                            binding.tvOptionsDatePeriod.visibility = View.VISIBLE

                            viewModel.postSearchOptions(newValue)
                        }
                    }

                    R.id.chip_tomorrow -> {
                        val newValue = viewModel.searchOptions.value

                        if (newValue != null) {
                            val date = Calendar.getInstance(Locale.getDefault())
                            date.add(Calendar.DATE, 1)
                            newValue.dateFrom = date
                            newValue.dateTo = date
                            newValue.dateFrom?.let { from ->
                                newValue.dateTo?.let { to ->
                                    binding.tvOptionsDatePeriod.text =
                                        "${from.time.toString("dd.MM.yyyy")} - ${to.time.toString("dd.MM.yyyy")}"
                                }
                            }
                            binding.chipGroupDate.visibility = View.GONE
                            binding.tvOptionsDatePeriod.visibility = View.VISIBLE

                            viewModel.postSearchOptions(newValue)
                        }
                    }
                }
            }
        }

        binding.chipGroupCategory.setOnCheckedStateChangeListener { chipGroup, ids ->
            val newValue = viewModel.searchOptions.value

            if (newValue != null) {
                newValue.categories.clear()
                ids.forEach { id ->
                    categories[id]?.categoryId?.let { categoryId ->
                        newValue.categories.add(categoryId)
                    }
                }
            }

        }



        binding.chipGroupTransportation.setOnCheckedStateChangeListener { chipGroup, ids ->
            val newValue = viewModel.searchOptions.value

            if (newValue != null) {
                newValue.transport.clear()
                ids.forEach { id ->
                    transport[id]?.transportationId?.let { transportId ->
                        newValue.transport.add(transportId)
                    }
                }
            }
        }



        binding.chipGroupAge.children.forEach {
            if(it is Chip){
                it.id
                ages[it.id] = Integer.parseInt(
                    it.text.subSequence(0, it.text.length-1)
                    .toString())
            }
        }


        binding.chipGroupAge.setOnCheckedStateChangeListener { chipGroup, ids ->
            val newValue = viewModel.searchOptions.value

            if (newValue != null) {
                newValue.ageRestriction = null
                newValue.ageRestriction = ages[binding.chipGroupAge.checkedChipId]
            }
        }

        binding.btnOptionsSelect.setOnClickListener {
            /*viewModel.approvedSearchOptions.priceFrom = binding.sliderOptionsPrice.values[0]
            viewModel.approvedSearchOptions.priceTo = binding.sliderOptionsPrice.values[1]

            viewModel.approvedSearchOptions.durationFrom = binding.sliderOptionsDuration.values[0]
            viewModel.approvedSearchOptions.durationTo = binding.sliderOptionsDuration.values[1]

            viewModel.approvedSearchOptions.ratingFrom = binding.sliderOptionsRating.values[0]
            viewModel.approvedSearchOptions.ratingTo = binding.sliderOptionsRating.values[1]

            binding.chipGroupCategory.checkedChipIds.forEach { id ->
                viewModel.approvedSearchOptions.categories.clear()
                categories[id]?.categoryId?.let { categoryId ->
                    viewModel.approvedSearchOptions.categories.add(categoryId)
                }
            }

            binding.chipGroupTransportation.checkedChipIds.forEach { id ->
                viewModel.approvedSearchOptions.transport.clear()
                transport[id]?.transportationId?.let { transportId ->
                    viewModel.approvedSearchOptions.transport.add(transportId)
                }
            }

            viewModel.approvedSearchOptions.ageRestriction = ages[binding.chipGroupAge.checkedChipId]
            city?.let { viewModel.approvedSearchOptions.city = it }

            if (!binding.switchOptionsGroup.isChecked && binding.switchOptionsIndividual.isChecked) {
                viewModel.approvedSearchOptions.isIndividual = true
            } else if (binding.switchOptionsGroup.isChecked && !binding.switchOptionsIndividual.isChecked) {
                viewModel.approvedSearchOptions.isIndividual = false
            } else {
                viewModel.approvedSearchOptions.isIndividual = null
            }
            viewModel.approvedSearchOptions.dateFrom = dateFrom
            viewModel.approvedSearchOptions.dateTo = dateTo*/
            viewModel.approveOptions()
            viewModel.searchByParams()
            dismiss()
        }

        binding.toolbarParams.setOnMenuItemClickListener { menuItem->
            when(menuItem.itemId){
                R.id.menu_appbar_reset->{
                    viewModel.resetSearchOptions()
                }
            }
            true
        }

        binding.toolbarParams.setNavigationOnClickListener {
            dismiss()
        }


        binding.tvOptionsTitleDateSelect.setOnClickListener {
            showDatePicker()
        }


        binding.tvOptionsTitleCitySelect.setOnClickListener {
            CityBottomSheetDialog(viewModel).show(childFragmentManager, "cityBottomDialog")
        }

    }

    private fun initObservers() {
        viewModel.searchOptions.observe(viewLifecycleOwner, Observer { searchOptions ->
            binding.sliderOptionsPrice.values =
                listOf(searchOptions.priceFrom, searchOptions.priceTo)
            binding.sliderOptionsDuration.values =
                listOf(searchOptions.durationFrom, searchOptions.durationTo)
            binding.sliderOptionsRating.values =
                listOf(searchOptions.ratingFrom, searchOptions.ratingTo)


            categories.forEach {
                if (it.value.categoryId in searchOptions.categories) {
                    binding.chipGroupCategory.check(it.key)
                }
            }
            transport.forEach {
                if (it.value.transportationId in searchOptions.transport) {
                    binding.chipGroupTransportation.check(it.key)
                }
            }

            ages.forEach {
                if (it.value == searchOptions.ageRestriction) {
                    binding.chipGroupAge.check(it.key)
                }
            }


            searchOptions.isIndividual.let {
                binding.switchOptionsIndividual.isChecked = it
            }


            searchOptions.isGroup.let {
                binding.switchOptionsGroup.isChecked = it
            }



            searchOptions.dateFrom?.let { from ->
                searchOptions.dateTo?.let { to ->
                    binding.tvOptionsDatePeriod.text =
                        "${from.time.toString("dd.MM.yyyy")} - ${to.time.toString("dd.MM.yyyy")}"

                    binding.chipGroupDate.visibility = View.GONE
                    binding.tvOptionsDatePeriod.visibility = View.VISIBLE
                }
            }
            if(searchOptions.dateFrom==null || searchOptions.dateTo==null){
                binding.chipGroupDate.clearCheck()
                binding.chipGroupDate.visibility = View.VISIBLE
                binding.tvOptionsDatePeriod.visibility = View.GONE
            }

            searchOptions.city?.let {
                binding.tvOptionsTitleCityName.text = it.name
                binding.tvOptionsTitleCityName.visibility = View.VISIBLE
            }

            if(searchOptions.city == null){
                binding.tvOptionsTitleCityName.visibility = View.GONE
            }

        })

        viewModel.optionsVariants.observe(viewLifecycleOwner, Observer { it ->
            when(it){
                is UIState.Success<*> -> {
                    val searchOptions = it.data as OptionsVariants
                    transport.clear()
                    categories.clear()
                    binding.chipGroupTransportation.removeAllViews()
                    binding.chipGroupCategory.removeAllViews()
                    searchOptions.transport.forEach{
                        val chip = Chip(requireContext())
                        chip.isCheckable = true
                        chip.id = View.generateViewId()
                        chip.text = it.name
                        transport[chip.id] = it
                        binding.chipGroupTransportation.addView(chip)
                    }


                    searchOptions.category.forEach{
                        val chip = Chip(requireContext())
                        chip.isCheckable = true
                        chip.id = View.generateViewId()
                        chip.text = it.name
                        categories[chip.id] = it
                        binding.chipGroupCategory.addView(chip)
                    }
                }
                is UIState.Error -> {
                    showSnack(requireView(), it.message, 5)
                }
                is UIState.ConnectionError -> {
                    showSnack(requireView(), "Отсутствует интернет подключение", 3)
                }
                is UIState.Idle -> {

                }
                else -> {}
            }

        })
    }

    private fun showDatePicker() {
        val today = MaterialDatePicker.todayInUtcMilliseconds()


        // Build constraints.
        val constraintsBuilder =
            CalendarConstraints.Builder()
                .setStart(today)
        val dateRangePickerBuilder =
            MaterialDatePicker.Builder.dateRangePicker()
                .setTitleText("Выберите дату")
                .setCalendarConstraints(constraintsBuilder.build())

        viewModel.searchOptions.value?.dateFrom?.let { from ->
            viewModel.searchOptions.value?.dateTo?.let { to ->
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
            val newValue = viewModel.searchOptions.value

            if (newValue != null) {
                var from = Calendar.getInstance(Locale.getDefault())
                from.timeInMillis = dates.first
                newValue.dateFrom = from

                var to = Calendar.getInstance(Locale.getDefault())
                to.timeInMillis = dates.second
                newValue.dateTo = to

                newValue.dateFrom?.let { from ->
                    newValue.dateTo?.let { to ->
                        binding.tvOptionsDatePeriod.text =
                            "${from.time.toString("dd.MM.yyyy")} - ${to.time.toString("dd.MM.yyyy")}"
                    }
                }
                binding.chipGroupDate.visibility = View.GONE
                binding.tvOptionsDatePeriod.visibility = View.VISIBLE

                viewModel.postSearchOptions(newValue)
            }

        }
    }
}