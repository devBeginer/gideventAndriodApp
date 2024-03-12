package ru.gidevent.androidapp.ui.mainScreen.fragment

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import ru.gidevent.RestAPI.model.City
import ru.gidevent.RestAPI.model.dto.CitySuggestion
import ru.gidevent.andriodapp.databinding.BottomsheetDialogCitySearchBinding
import ru.gidevent.androidapp.data.model.suggestionsRecyclerviewModels.SuggestionCity
import ru.gidevent.androidapp.data.model.suggestionsRecyclerviewModels.SuggestionRecyclerViewData
import ru.gidevent.androidapp.ui.mainScreen.adapter.CityPickRecyclerViewAdapter
import ru.gidevent.androidapp.ui.mainScreen.viewModel.SearchViewModel
import ru.gidevent.androidapp.ui.state.UIState
import ru.gidevent.androidapp.utils.queryInputAsFlow
import ru.gidevent.androidapp.utils.showSnack

class CityBottomSheetDialog(private val viewModel: SearchViewModel): BottomSheetDialogFragment() {
    private var _binding: BottomsheetDialogCitySearchBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: CityPickRecyclerViewAdapter


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomsheetDialogCitySearchBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initData()

    }



    private fun initData() {
        adapter = CityPickRecyclerViewAdapter(listOf()){suggestionCity->
            val newValue = viewModel.searchOptions.value

            if (newValue != null) {
                newValue.city = City(suggestionCity.id, suggestionCity.name)

                viewModel.postSearchOptions(newValue)
            }
            dismiss()
        }
        binding.rvBottomCitySuggestions.adapter = adapter

        binding.searchViewBottomCity
            .queryInputAsFlow()
            .debounce(300)
            .onEach {
                //showSnack(requireView(), it, 5)
                viewModel.getCitySuggestions(it)
            }
            .launchIn(CoroutineScope(Dispatchers.Main))

            viewModel.citySuggestions.observe(viewLifecycleOwner, Observer {
                when(it){
                    is UIState.Success<*> -> {
                        val dataSet = it.data as List<SuggestionCity>
                        if(dataSet!=null){
                            adapter.setItemsList(dataSet)
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
}