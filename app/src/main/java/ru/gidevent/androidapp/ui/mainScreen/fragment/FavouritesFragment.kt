package ru.gidevent.androidapp.ui.mainScreen.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import ru.gidevent.andriodapp.R
import ru.gidevent.andriodapp.databinding.FragmentFavouritesBinding
import ru.gidevent.andriodapp.databinding.FragmentMainBinding
import ru.gidevent.androidapp.data.model.mainRecyclerviewModels.AdvertPreviewCard
import ru.gidevent.androidapp.data.model.mainRecyclerviewModels.HeaderViewpagerItem
import ru.gidevent.androidapp.data.model.mainRecyclerviewModels.MainRecyclerViewData
import ru.gidevent.androidapp.ui.mainScreen.adapter.FavouriteRecyclerViewAdapter
import ru.gidevent.androidapp.ui.mainScreen.adapter.MainRecyclerViewAdapter
import ru.gidevent.androidapp.ui.mainScreen.viewModel.FavouritesViewModel

@AndroidEntryPoint
class FavouritesFragment : Fragment() {

    private val viewModel: FavouritesViewModel by viewModels()

    private var _binding: FragmentFavouritesBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFavouritesBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


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


        val adapter = FavouriteRecyclerViewAdapter(
            cardData
        )
        binding.rvFavouriteCards.adapter = adapter


    }
}