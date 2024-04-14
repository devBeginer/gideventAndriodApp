package ru.gidevent.androidapp.ui.edit.fragment

import android.app.AlertDialog
import android.content.DialogInterface.BUTTON_POSITIVE
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.children
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.google.android.material.chip.Chip
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.gidevent.RestAPI.model.Category
import ru.gidevent.RestAPI.model.TransportationVariant
import ru.gidevent.andriodapp.R
import ru.gidevent.andriodapp.databinding.FragmentCreateAdvertisementBinding
import ru.gidevent.androidapp.data.model.advertisement.request.NewAdvertisement
import ru.gidevent.androidapp.data.model.advertisement.response.ResponsePoster
import ru.gidevent.androidapp.data.model.search.OptionsVariants
import ru.gidevent.androidapp.ui.SharedViewModel
import ru.gidevent.androidapp.ui.advertisement.AdvertisementFragment
import ru.gidevent.androidapp.ui.edit.CreateAdvertViewModel
import ru.gidevent.androidapp.ui.edit.adapter.PhotoEditRecyclerViewAdapter
import ru.gidevent.androidapp.ui.edit.adapter.PriceEditRecyclerViewAdapter
import ru.gidevent.androidapp.ui.edit.adapter.ScheduleEditRecyclerViewAdapter
import ru.gidevent.androidapp.ui.mainScreen.fragment.CityBottomSheetDialog
import ru.gidevent.androidapp.ui.state.UIState
import ru.gidevent.androidapp.utils.showSnack

@AndroidEntryPoint
class CreateAdvertisementFragment() : Fragment() {
    private val viewModel: CreateAdvertViewModel by viewModels({requireParentFragment()})
    private val sharedViewModel: SharedViewModel by viewModels({requireActivity()})

    private var _binding: FragmentCreateAdvertisementBinding? = null
    private val binding get() = _binding!!

    private lateinit var photoRVAdapter: PhotoEditRecyclerViewAdapter
    private lateinit var priceRVAdapter: PriceEditRecyclerViewAdapter
    private lateinit var scheduleRVAdapter: ScheduleEditRecyclerViewAdapter

    private val categories: HashMap<Int, Category> = hashMapOf()
    private val transports: HashMap<Int, TransportationVariant> = hashMapOf()
    private val ages: HashMap<Int, Int> = hashMapOf()
    private var currentMode: Int = CREATE_MODE
    private var id: Long? = null
    private var images = mutableListOf<String>()
    private val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if(uri!=null){
            CoroutineScope(Dispatchers.IO).launch {
                val result = viewModel.postPhoto(uri)
                withContext(Dispatchers.Main){
                    when (result) {
                        is UIState.Success<*> -> {
                            val poster = result.data as ResponsePoster?
                            if (poster != null) {
                                images.add(poster.fileUUID)
                                photoRVAdapter.addItemList(poster.fileUUID)
                            }
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
    companion object {
        const val EDIT_MODE = 0
        const val CREATE_MODE = 1
        fun newInstance(id: Long): AdvertisementFragment {
            val fragment = AdvertisementFragment()
            val args = Bundle()
            args.putLong("ID", id)
            fragment.setArguments(args)
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCreateAdvertisementBinding.inflate(inflater, container, false)
        val view = binding.root
        id = arguments?.getLong("ID")
        if (id != null) {
            currentMode = EDIT_MODE
        }
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
        viewModel.optionsVariants.observe(viewLifecycleOwner, Observer { it ->
            when (it) {
                is UIState.Success<*> -> {
                    val searchOptions = it.data as OptionsVariants
                    transports.clear()
                    categories.clear()
                    binding.chipGroupCreateTransportation.removeAllViews()
                    binding.chipGroupCreateCategory.removeAllViews()
                    searchOptions.transport.forEach {
                        val chip = Chip(requireContext())
                        chip.isCheckable = true
                        chip.id = View.generateViewId()
                        chip.text = it.name
                        transports[chip.id] = it
                        binding.chipGroupCreateTransportation.addView(chip)
                    }


                    searchOptions.category.forEach {
                        val chip = Chip(requireContext())
                        chip.isCheckable = true
                        chip.id = View.generateViewId()
                        chip.text = it.name
                        categories[chip.id] = it
                        binding.chipGroupCreateCategory.addView(chip)
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
        viewModel.postResult.observe(viewLifecycleOwner, Observer { it ->
            when (it) {
                is UIState.Success<*> -> {
                    sharedViewModel.showProgressIndicator(false)
                    currentMode = EDIT_MODE

                    parentFragmentManager
                        /*requireParentFragment().childFragmentManager*/
                            .beginTransaction()
                            .replace(R.id.edit_nav_host_fragment, CreateScheduleFragment()).addToBackStack(null)
                            .commit()

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
        viewModel.city.observe(viewLifecycleOwner, Observer {
            if(it!=null){
                binding.tvCreateCityName.text = it.name
                binding.tvCreateCityName.visibility = View.VISIBLE
            }
        })
    }

    private fun initView() {

        viewModel.initOptions()
        binding.tpCreateDuration.setIs24HourView(true)
        when(currentMode){
            EDIT_MODE->{
                binding.rvCreatePricelist.visibility = View.VISIBLE
                binding.tvCreatePricelistTitle.visibility = View.VISIBLE
                binding.rvCreateSchedule.visibility = View.VISIBLE
                binding.tvCreateScheduleTitle.visibility = View.VISIBLE
                viewModel.initFields()
            }
            CREATE_MODE->{
                binding.rvCreatePricelist.visibility = View.GONE
                binding.tvCreatePricelistTitle.visibility = View.GONE
                binding.rvCreateSchedule.visibility = View.GONE
                binding.tvCreateScheduleTitle.visibility = View.GONE
            }
        }
        binding.toolbarCreate.setNavigationOnClickListener() {
            parentFragmentManager.popBackStack()
            //onBackPressed() // возврат на предыдущий activity
        }
        photoRVAdapter = PhotoEditRecyclerViewAdapter(listOf(), {
            viewModel.delPhoto(it)
        }, {
            //loadPhoto()
            getContent.launch("image/*")
        })
        /*priceRVAdapter = PriceEditRecyclerViewAdapter(listOf(), {
            viewModel.delPrice(it)
        }, {
            editPrice(it)
        }, {
            createPrice()
        })
        scheduleRVAdapter = ScheduleEditRecyclerViewAdapter(listOf(), {
            viewModel.delTime(it)
        }, {
            editTime(it)
        }, {
            createTime()
        })
        binding.rvCreatePricelist.adapter = priceRVAdapter
        binding.rvCreateSchedule.adapter = scheduleRVAdapter*/
        binding.rvCreatePhoto.adapter = photoRVAdapter

        binding.chipGroupCreateAge.children.forEach {
            if (it is Chip) {
                it.id
                ages[it.id] = Integer.parseInt(
                    it.text.subSequence(0, it.text.length - 1)
                        .toString()
                )
            }
        }
        if(currentMode == CREATE_MODE) {
            binding.tpCreateDuration.hour = 0
            binding.tpCreateDuration.minute = 0
        }

        binding.switchCreateIndividual.setOnCheckedChangeListener { compoundButton, isChecked ->
            if(!isChecked){
                binding.tilCreatePeopleCount.visibility = View.VISIBLE
            }else{
                binding.tilCreatePeopleCount.visibility = View.GONE
            }
        }

        binding.btnCreatePost.setOnClickListener {
            val city = viewModel.city.value
            val transport = transports[binding.chipGroupCreateTransportation.checkedChipId]
            val age = ages[binding.chipGroupCreateAge.checkedChipId]
            val category = categories[binding.chipGroupCreateCategory.checkedChipId]
            if (!binding.etCreateDescription.text.isNullOrBlank()
                && !binding.etCreateName.text.isNullOrBlank()
                && (!binding.etCreatePeopleCount.text.isNullOrBlank() || binding.switchCreateIndividual.isChecked)
                && city != null
                && transport != null
                && age != null
                && category != null
                && (binding.tpCreateDuration.hour != 0 || binding.tpCreateDuration.minute != 0)
            ) {
                val newAdvertisement = NewAdvertisement(
                    0,
                    binding.etCreateName.text.toString(),
                    binding.tpCreateDuration.hour,
                    binding.etCreateDescription.text.toString(),
                    transport.transportationId,
                    age,
                    if(!binding.switchCreateIndividual.isChecked) binding.etCreatePeopleCount.text.toString().toInt() else 1,
                    binding.switchCreateIndividual.isChecked,
                    images.joinToString(","),
                    0,
                    category.categoryId,
                    city.cityId
                )

                viewModel.postAdvertisement(newAdvertisement)

            }
        }

        binding.tvCreateCitySelect.setOnClickListener {
            CityBottomSheetDialog(viewModel).show(childFragmentManager, "cityBottomDialog")
        }

        binding.tvCreateTransportationAdd.setOnClickListener {
            addTransportation()
        }

        binding.tvCreateCategoryAdd.setOnClickListener {
            addCategory()
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
                                    val chip = Chip(requireContext())
                                    chip.isCheckable = true
                                    chip.id = View.generateViewId()
                                    chip.text = category.name
                                    categories[chip.id] = category
                                    binding.chipGroupCreateCategory.addView(chip)

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
        dialog.getButton(BUTTON_POSITIVE).visibility = View.GONE

        editText.doAfterTextChanged {
            if(it.toString().isNullOrBlank()){
                dialog.getButton(BUTTON_POSITIVE).visibility = View.GONE
            }else{
                dialog.getButton(BUTTON_POSITIVE).visibility = View.VISIBLE
            }
        }
    }


    private fun loadPhoto() {
        TODO("Not yet implemented")
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
        dialog.getButton(BUTTON_POSITIVE).visibility = View.GONE

        editText.doAfterTextChanged {
            if(it.toString().isNullOrBlank()){
                dialog.getButton(BUTTON_POSITIVE).visibility = View.GONE
            }else{
                dialog.getButton(BUTTON_POSITIVE).visibility = View.VISIBLE
            }
        }

    }
}