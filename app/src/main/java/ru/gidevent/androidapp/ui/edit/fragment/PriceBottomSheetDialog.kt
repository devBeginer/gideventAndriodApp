package ru.gidevent.androidapp.ui.edit.fragment

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.RadioButton
import androidx.core.view.children
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.gidevent.RestAPI.model.Category
import ru.gidevent.andriodapp.R
import ru.gidevent.andriodapp.databinding.BottomsheetDialogPriceBinding
import ru.gidevent.androidapp.data.model.advertisement.dto.CustomerCategory
import ru.gidevent.androidapp.ui.SharedViewModel
import ru.gidevent.androidapp.ui.edit.CreateAdvertViewModel
import ru.gidevent.androidapp.ui.state.UIState
import ru.gidevent.androidapp.utils.Utils.toString
import ru.gidevent.androidapp.utils.showSnack
@AndroidEntryPoint
class PriceBottomSheetDialog(): BottomSheetDialogFragment() {
    private val viewModel: CreateAdvertViewModel by viewModels({requireParentFragment()})
    private val sharedViewModel: SharedViewModel by viewModels({requireActivity()})
    private var _binding: BottomsheetDialogPriceBinding? = null
    private val binding get() = _binding!!

    private var currentMode = CREATE_MODE
    private val customerCategory: HashMap<Int, CustomerCategory> = hashMapOf()
    private var id: Long? = null

    companion object{
        const val EDIT_MODE = 0
        const val CREATE_MODE = 1
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomsheetDialogPriceBinding.inflate(inflater, container, false)
        val view = binding.root
        id = arguments?.getLong("ID")
        if (id != null) {
            currentMode = EventtimeBottomSheetDialog.EDIT_MODE
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initData()
        initObservers()
    }

    private fun initData() {
        viewModel.initCustomerCategories(id)
        binding.btnPriceCreate.setOnClickListener {
            val category = customerCategory[binding.rgPriceCustomercategory.checkedRadioButtonId]
            if(!binding.etPriceAmount.text.isNullOrBlank()
                && category!=null){
                sharedViewModel.showProgressIndicator(true)
                CoroutineScope(Dispatchers.IO).launch {

                    val result = viewModel.createPrice(binding.etPriceAmount.text.toString().toInt(), category.customerCategoryId, currentMode == EDIT_MODE, id)
                    withContext(Dispatchers.Main){
                        when(result){
                            is UIState.Success<*>->{
                                viewModel.initPriceList()
                                dismiss()
                            }
                            is UIState.Loading->{}
                            is UIState.Error->{
                                sharedViewModel.showProgressIndicator(false)}
                            is UIState.ConnectionError->{
                                sharedViewModel.showProgressIndicator(false)}
                            is UIState.Unauthorised->{
                                sharedViewModel.showProgressIndicator(false)}
                            is UIState.Idle->{}
                            else->{}
                        }
                    }
                }
            }
        }

        binding.tvPriceAdd.setOnClickListener {
            addCategory()
        }
    }

    private fun initObservers() {
        viewModel.customerVariants.observe(viewLifecycleOwner, Observer { it ->
            when (it) {
                is UIState.Success<*> -> {
                    val customerCategoryList = it.data as List<CustomerCategory>
                    customerCategory.clear()
                    binding.rgPriceCustomercategory.removeAllViews()
                    customerCategoryList.forEach {
                        val radioButton = RadioButton(requireContext())

                        radioButton.id = View.generateViewId()
                        radioButton.text = it.name
                        customerCategory[radioButton.id] = it
                        binding.rgPriceCustomercategory.addView(radioButton)
                    }
                    if(binding.rgPriceCustomercategory.childCount>0){
                        binding.rgPriceCustomercategory.check(binding.rgPriceCustomercategory.getChildAt(0).id)
                    }
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

        viewModel.ticketPriceDate.observe(viewLifecycleOwner, Observer { ticketPriceResponse ->
            if(ticketPriceResponse!=null){
                binding.etPriceAmount.setText(ticketPriceResponse.price.toString())
                customerCategory.forEach {
                    if(it.value == ticketPriceResponse.customerCategory){
                        binding.rgPriceCustomercategory.check(it.key)
                    }
                }
            }
        })

    }

    private fun addCategory() {
        val mDialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_customer_category, null)
        val editText = mDialogView.findViewById<EditText>(R.id.et_customer_category_name)

        val dialogBuilder = MaterialAlertDialogBuilder(requireContext())
            .setView(mDialogView)
            .setTitle("Добавить")
            .setMessage("Введите название категории")
            .setNeutralButton("Отмена") { dialog, which ->}
            .setPositiveButton("Сохранить") { dialog, which ->

            }
        //.show()

        val dialog = dialogBuilder.create()

        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setOnClickListener {
                    CoroutineScope(Dispatchers.IO).launch {
                        sharedViewModel.showProgressIndicator(true)
                        val result = viewModel.addCustomerCategory(editText.text.toString())
                        withContext(Dispatchers.Main){
                            when (result) {
                                is UIState.Success<*> -> {
                                    val category = result.data as CustomerCategory
                                    val radioButton = RadioButton(requireContext())
                                    radioButton.id = View.generateViewId()
                                    radioButton.text = category.name
                                    customerCategory[radioButton.id] = category
                                    binding.rgPriceCustomercategory.addView(radioButton)

                                    sharedViewModel.showProgressIndicator(false)
                                    dialog.dismiss()

                                }

                                is UIState.Error -> {
                                    sharedViewModel.showProgressIndicator(false)
                                    showSnack(requireView(), result.message, 5)
                                }

                                is UIState.ConnectionError -> {
                                    sharedViewModel.showProgressIndicator(false)
                                    showSnack(requireView(), "Отсутствует интернет подключение", 3)
                                }

                                is UIState.Idle -> {

                                }

                                else -> {}
                            }
                        }
                    }
                }
        }

        dialog.show()
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).visibility = View.GONE

        editText.doAfterTextChanged {
            if(it.toString().isNullOrBlank()){
                dialog.getButton(DialogInterface.BUTTON_POSITIVE).visibility = View.GONE
            }else{
                dialog.getButton(DialogInterface.BUTTON_POSITIVE).visibility = View.VISIBLE
            }
        }
    }
}