package ru.gidevent.androidapp.ui.mainScreen.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import dagger.hilt.android.AndroidEntryPoint
import ru.gidevent.andriodapp.R
import ru.gidevent.andriodapp.databinding.FragmentMainBinding
import ru.gidevent.androidapp.data.model.mainRecyclerviewModels.AdvertPreviewCard
import ru.gidevent.androidapp.data.model.mainRecyclerviewModels.HeaderViewpagerItem
import ru.gidevent.androidapp.data.model.mainRecyclerviewModels.MainRecyclerViewData
import ru.gidevent.androidapp.ui.advertisement.AdvertisementFragment
import ru.gidevent.androidapp.ui.login.fragment.SignUpFragment
import ru.gidevent.androidapp.ui.mainScreen.adapter.MainRecyclerViewAdapter
import ru.gidevent.androidapp.ui.mainScreen.viewModel.MainViewModel

@AndroidEntryPoint
class MainFragment : Fragment() {

    private val viewModel: MainViewModel by viewModels()

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: MainRecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /*requireActivity().onBackPressedDispatcher.addCallback(this) {

        }*/
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
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
        adapter = MainRecyclerViewAdapter(
            MainRecyclerViewData(listOf<HeaderViewpagerItem>(), listOf<String>(), listOf<AdvertPreviewCard>()),
            {
                requireActivity().supportFragmentManager.beginTransaction().replace(R.id.nav_host_fragment, AdvertisementFragment.newInstance(it)).addToBackStack(null).commit()
            },{
                requireActivity().supportFragmentManager.beginTransaction().replace(R.id.nav_host_fragment, AdvertisementFragment.newInstance(it)).addToBackStack(null).commit()
            },
            {
                viewModel.postFavourite(it)
            }
        )
        binding.rvMainCards.adapter = adapter
    }

    private fun initObservers(){
        viewModel.data.observe(viewLifecycleOwner, Observer {dataSet->
            if(dataSet!=null){
                adapter.setItemsList(dataSet)
            }
        })

        viewModel.favourite.observe(viewLifecycleOwner, Observer { advert->
            if(advert!=null){
                adapter.updateItem(advert)
            }
        })
    }
}