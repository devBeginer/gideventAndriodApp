package ru.gidevent.androidapp.ui.mainScreen.fragment

import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import ru.gidevent.RestAPI.model.City
import ru.gidevent.andriodapp.R
import ru.gidevent.andriodapp.databinding.FragmentSearchBinding
import ru.gidevent.androidapp.data.model.mainRecyclerviewModels.AdvertPreviewCard
import ru.gidevent.androidapp.data.model.suggestionsRecyclerviewModels.SuggestionRecyclerViewData
import ru.gidevent.androidapp.ui.SharedViewModel
import ru.gidevent.androidapp.ui.advertisement.AdvertisementFragment
import ru.gidevent.androidapp.ui.mainScreen.adapter.SearchRecyclerViewAdapter
import ru.gidevent.androidapp.ui.mainScreen.adapter.SuggestionsRecyclerViewAdapter
import ru.gidevent.androidapp.ui.mainScreen.viewModel.SearchViewModel
import ru.gidevent.androidapp.ui.state.UIState
import ru.gidevent.androidapp.ui.state.UIStateAdvertList
import ru.gidevent.androidapp.utils.showSnack
import ru.gidevent.androidapp.utils.textInputAsFlow

@AndroidEntryPoint
class SearchFragment : Fragment() {

    private val viewModel: SearchViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by viewModels({requireActivity()})

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: SearchRecyclerViewAdapter
    private lateinit var suggestionAdapter: SuggestionsRecyclerViewAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
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
    }

    private fun initView(){
        //viewModel.initView()
        adapter = SearchRecyclerViewAdapter(listOf(), {
            //requireActivity().supportFragmentManager.beginTransaction().replace(R.id.nav_host_fragment, AdvertisementFragment.newInstance(it)).addToBackStack(null).commit()
            parentFragmentManager.beginTransaction().replace(R.id.main_nav_host_fragment, AdvertisementFragment.newInstance(it)).addToBackStack(null).commit()
        },{
            viewModel.postFavourite(it)
        })
        binding.rvSearchCards.adapter = adapter
        suggestionAdapter = SuggestionsRecyclerViewAdapter(
            SuggestionRecyclerViewData(listOf(), listOf(), listOf()),
            {
                //requireActivity().supportFragmentManager.beginTransaction().replace(R.id.nav_host_fragment, AdvertisementFragment.newInstance(it)).addToBackStack(null).commit()
                parentFragmentManager.beginTransaction().replace(R.id.main_nav_host_fragment, AdvertisementFragment.newInstance(it)).addToBackStack(null).commit()
            },{
                viewModel.resetSearchOptions()
                val newValue = viewModel.searchOptions.value
                if (newValue != null) {
                    newValue.city = it
                    viewModel.postSearchOptions(newValue)
                    viewModel.approveOptions()
                    viewModel.searchByParams()
                    binding.searchBar.setText(it.name)
                    binding.searchView.hide()
                }
            },{
                viewModel.resetSearchOptions()
                val newValue = viewModel.searchOptions.value
                if (newValue != null) {
                    newValue.categories.clear()
                    newValue.categories.add(it.categoryId)
                    viewModel.postSearchOptions(newValue)
                    viewModel.approveOptions()
                    viewModel.searchByParams()
                    binding.searchBar.setText(it.name)
                    binding.searchView.hide()
                }
            }
        )
        binding.rvSearchSuggestions.adapter = suggestionAdapter

        binding.searchBar.inflateMenu(R.menu.menu_search_bar)
        binding.searchBar.setOnMenuItemClickListener { menuItem ->
            when(menuItem.itemId){
                R.id.search_options->{
                    val newFragment = OptionDialogFragment(viewModel)
                    val transaction = requireActivity().supportFragmentManager.beginTransaction()
                    transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                    transaction
                        .add(android.R.id.content, newFragment)
                        .addToBackStack(null)
                        .commit()
                }
            }
            true
        }
        binding.searchView.setupWithSearchBar(binding.searchBar)

        binding.searchView
            .editText
            .setOnEditorActionListener { v, actionId, event ->
                viewModel.searchByName(binding.searchView.text.toString())
                binding.searchBar.setText(binding.searchView.text)
                binding.searchView.hide()
                false
            }
        binding.searchView
            .editText
            .textInputAsFlow()
            .debounce(300)
            .onEach { query->
                if (!query.isNullOrBlank()){
                    viewModel.getSuggestions(query.toString())
                }
            }
            .launchIn(CoroutineScope(Dispatchers.Main))
            //.launchIn(CoroutineScope(Dispatchers.IO))

    }

    private fun initObservers(){


        viewModel.data.observe(viewLifecycleOwner, Observer {
            when(it){
                is UIStateAdvertList.Success<*> -> {
                    val dataSet = it.data as List<AdvertPreviewCard>?
                    if(dataSet!=null){
                        adapter.setItemsList(dataSet)
                    }
                    sharedViewModel.showProgressIndicator(false)
                }
                is UIStateAdvertList.Update<*> -> {
                    val advert = it.data as AdvertPreviewCard?
                    if(advert!=null){
                        adapter.updateItem(advert)
                    }
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
                is UIStateAdvertList.Loading -> {

                    sharedViewModel.showProgressIndicator(true)
                }
                else -> {}
            }
        })


        viewModel.searchSuggestions.observe(viewLifecycleOwner, Observer {
            when(it){
                is UIState.Success<*> -> {
                    val dataSet = it.data as SuggestionRecyclerViewData?
                    if(dataSet!=null){
                        suggestionAdapter.setItemsList(dataSet)
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