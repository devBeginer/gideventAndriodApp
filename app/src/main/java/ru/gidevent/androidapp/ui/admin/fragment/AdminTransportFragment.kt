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
import ru.gidevent.RestAPI.model.TransportationVariant
import ru.gidevent.andriodapp.R
import ru.gidevent.andriodapp.databinding.FragmentAdminTransportBinding
import ru.gidevent.androidapp.ui.SharedViewModel
import ru.gidevent.androidapp.ui.admin.adapter.AdminTransportRecyclerViewAdapter
import ru.gidevent.androidapp.ui.admin.viewModel.AdminTransportViewModel
import ru.gidevent.androidapp.ui.state.UIState
import ru.gidevent.androidapp.utils.showSnack

@AndroidEntryPoint
class AdminTransportFragment(): Fragment() {
    private val viewModel: AdminTransportViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by viewModels({requireActivity()})

    private var _binding: FragmentAdminTransportBinding? = null
    private val binding get() = _binding!!
    private lateinit var rvAdapter: AdminTransportRecyclerViewAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAdminTransportBinding.inflate(inflater, container, false)
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
                    val categoryList = it.data as List<TransportationVariant>
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

        viewModel.initTransport()



        rvAdapter = AdminTransportRecyclerViewAdapter(listOf(), {
            viewModel.deleteTransportation(it)
        }, {id, name->
            editTransportation(id, name)
        })

        binding.rvAdminTransport.adapter = rvAdapter


        binding.toolbarAdminTransport.setNavigationOnClickListener() {
            parentFragmentManager.popBackStack()
        }

        binding.toolbarAdminTransport.setOnMenuItemClickListener { menuItem->
            when(menuItem.itemId){
                R.id.add_transport->{
                    addTransportation()
                }
            }
            true
        }

    }



    private fun editTransportation(id: Long, name: String) {
        val mDialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_transportation, null)
        val editText = mDialogView.findViewById<EditText>(R.id.et_transportation_name)

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
                        val result = viewModel.editTransportation(TransportationVariant(id, editText.text.toString()))
                        withContext(Dispatchers.Main){
                            when (result) {
                                is UIState.Success<*> -> {
                                    val category = result.data as TransportationVariant
                                    viewModel.initTransport()

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




    private fun addTransportation(){
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
                        val result = viewModel.addTransportation(editText.text.toString())
                        withContext(Dispatchers.Main){
                            when (result) {
                                is UIState.Success<*> -> {
                                    val transportation = result.data as TransportationVariant
                                    viewModel.initTransport()

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


}