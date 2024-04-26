package ru.gidevent.androidapp.ui.admin.fragment

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.gidevent.RestAPI.model.Category
import ru.gidevent.andriodapp.R
import ru.gidevent.andriodapp.databinding.FragmentAdminCategoryBinding
import ru.gidevent.androidapp.ui.SharedViewModel
import ru.gidevent.androidapp.ui.admin.adapter.AdminCategoryRecyclerViewAdapter
import ru.gidevent.androidapp.ui.admin.viewModel.AdminCategoryViewModel
import ru.gidevent.androidapp.ui.state.UIState
import ru.gidevent.androidapp.utils.showSnack

@AndroidEntryPoint
class AdminCategoryFragment(): Fragment() {
    private val viewModel: AdminCategoryViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by viewModels({requireActivity()})

    private var _binding: FragmentAdminCategoryBinding? = null
    private val binding get() = _binding!!
    private lateinit var rvAdapter: AdminCategoryRecyclerViewAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAdminCategoryBinding.inflate(inflater, container, false)
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
        viewModel.data.observe(viewLifecycleOwner, Observer { it ->
            when (it) {
                is UIState.Success<*> -> {
                    val categoryList = it.data as List<Category>
                    rvAdapter.setItemList(categoryList)

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

        viewModel.initCategory()



        rvAdapter = AdminCategoryRecyclerViewAdapter(listOf(), {
            viewModel.deleteCategory(it)
        }, {id, name->
            editCategory(id, name)
        })

        binding.rvAdminCategory.adapter = rvAdapter


        binding.toolbarAdminCategory.setNavigationOnClickListener() {
            parentFragmentManager.popBackStack()
        }

        binding.toolbarAdminCategory.setOnMenuItemClickListener { menuItem->
            when(menuItem.itemId){
                R.id.add_category->{
                    addCategory()
                }
            }
            true
        }

    }

    private fun addCategory() {
        val mDialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_category, null)
        val editText = mDialogView.findViewById<EditText>(R.id.et_category_name)

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
                        val result = viewModel.addCategory(editText.text.toString())
                        withContext(Dispatchers.Main){
                            when (result) {
                                is UIState.Success<*> -> {
                                    val category = result.data as Category
                                    viewModel.initCategory()

                                    dialog.dismiss()

                                }

                                is UIState.Error -> {
                                    showSnack(requireView(), result.message, 5)
                                }

                                is UIState.ConnectionError -> {
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

    private fun editCategory(id: Long, name: String) {
        val mDialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_category, null)
        val editText = mDialogView.findViewById<EditText>(R.id.et_category_name)

        val dialogBuilder = MaterialAlertDialogBuilder(requireContext())
            .setView(mDialogView)
            .setTitle("Изменить")
            .setMessage("Введите название категории")
            .setNeutralButton("Отмена") { dialog, which ->}
            .setPositiveButton("Сохранить") { dialog, which ->

            }
        //.show()

        editText.setText(name)
        val dialog = dialogBuilder.create()

        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setOnClickListener {
                    CoroutineScope(Dispatchers.IO).launch {
                        val result = viewModel.editCategory(Category(id, editText.text.toString()))
                        withContext(Dispatchers.Main){
                            when (result) {
                                is UIState.Success<*> -> {
                                    val category = result.data as Category
                                    viewModel.initCategory()

                                    dialog.dismiss()

                                }

                                is UIState.Error -> {
                                    showSnack(requireView(), result.message, 5)
                                }

                                is UIState.ConnectionError -> {
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




    /*private fun addTransportation(){
        val mDialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_transportation, null)
        val editText = mDialogView.findViewById<EditText>(R.id.et_transportation_name)

        val dialogBuilder = MaterialAlertDialogBuilder(requireContext())
            .setView(mDialogView)
            .setTitle("Добавить")
            .setMessage("Введите название способа передвижения")
            .setNeutralButton("Отмена") { dialog, which ->}
            .setPositiveButton("Сохранить") { dialog, which ->

            }
        //.show()

        val dialog = dialogBuilder.create()

        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setOnClickListener {
                    CoroutineScope(Dispatchers.IO).launch {
                        val result = viewModel.addTransportations(editText.text.toString())
                        withContext(Dispatchers.Main){
                            when (result) {
                                is UIState.Success<*> -> {
                                    val transportation = result.data as TransportationVariant
                                    val chip = Chip(requireContext())
                                    chip.isCheckable = true
                                    chip.id = View.generateViewId()
                                    chip.text = transportation.name
                                    transports[chip.id] = transportation
                                    binding.chipGroupCreateTransportation.addView(chip)

                                    dialog.dismiss()

                                }

                                is UIState.Error -> {
                                    showSnack(requireView(), result.message, 5)
                                }

                                is UIState.ConnectionError -> {
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

    }*/


}