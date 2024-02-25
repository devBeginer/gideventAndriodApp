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
import ru.gidevent.androidapp.ui.mainScreen.adapter.MainRecyclerViewAdapter
import ru.gidevent.androidapp.ui.mainScreen.viewModel.MainViewModel

@AndroidEntryPoint
class MainFragment : Fragment() {

    private val viewModel: MainViewModel by viewModels()

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: MainRecyclerViewAdapter

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

       /* val viewPagerData = listOf(
            HeaderViewpagerItem("name1", 1500, R.drawable.viewpager_item1),
            HeaderViewpagerItem("name2", 500, R.drawable.viewpager_item2),
            HeaderViewpagerItem("name3", 2000, R.drawable.viewpager_item3),
            HeaderViewpagerItem("name4", 5000, R.drawable.viewpager_item1),
            HeaderViewpagerItem("name5", 15000, R.drawable.viewpager_item2),
            HeaderViewpagerItem("name1", 1500, R.drawable.viewpager_item3),
            HeaderViewpagerItem("name3", 2000, R.drawable.viewpager_item2),
            HeaderViewpagerItem("name4", 5000, R.drawable.viewpager_item3),
            HeaderViewpagerItem("name5", 15000, R.drawable.viewpager_item1)
        )



        val cardData = listOf(
            AdvertPreviewCard(true, "name1", listOf("Cat1", "Cat2"), 1500, "url"),
            AdvertPreviewCard(false, "name2", listOf("Cat6", "Cat9"), 500, "url"),
            AdvertPreviewCard(true, "name3", listOf("Cat1", "Cat2"), 2000, "url"),
            AdvertPreviewCard(false, "name4", listOf("Cat7", "Cat5"), 5000, "url"),
            AdvertPreviewCard(true, "name5", listOf("Cat1", "Cat2"), 15000, "url"),
            AdvertPreviewCard(true, "name1", listOf("Cat1", "Cat2"), 1500, "url"),
            AdvertPreviewCard(false, "name2", listOf("Cat6", "Cat9"), 500, "url"),
            AdvertPreviewCard(true, "name3", listOf("Cat1", "Cat2"), 2000, "url"),
            AdvertPreviewCard(false, "name4", listOf("Cat7", "Cat5"), 5000, "url"),
            AdvertPreviewCard(true, "name5", listOf("Cat1", "Cat2"), 15000, "url")
        )

        val categoryData =
            listOf("Квест", "Экскурсия", "Фотопрогулка", "Речная прогулка", "Необычные экскурсии")*/

        initView()
        initObservers()

    }

    private fun initView(){
        viewModel.initView()
        adapter = MainRecyclerViewAdapter(
            MainRecyclerViewData(listOf<HeaderViewpagerItem>(), listOf<String>(), listOf<AdvertPreviewCard>())
        )
        binding.rvMainCards.adapter = adapter
    }

    private fun initObservers(){
        viewModel.data.observe(viewLifecycleOwner, Observer {dataSet->
            if(dataSet!=null){
                adapter.setItemsList(dataSet)
            }
        })
    }
}