package ru.gidevent.androidapp.ui.advertisement

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import dagger.hilt.android.AndroidEntryPoint
import ru.gidevent.andriodapp.R
import ru.gidevent.andriodapp.databinding.FragmentAdvertisementBinding
import ru.gidevent.androidapp.data.model.advertisement.AdvertisementCardInfo
import ru.gidevent.androidapp.ui.advertisement.adapter.AdvertReviewRecyclerViewAdapter
import ru.gidevent.androidapp.ui.advertisement.adapter.AdvertViewPagerAdapter
import ru.gidevent.androidapp.ui.state.UIState
import ru.gidevent.androidapp.utils.Utils
import ru.gidevent.androidapp.utils.showSnack

@AndroidEntryPoint
class AdvertisementFragment : Fragment() {
    private val viewModel: AdvertisementViewModel by viewModels()
    private var _binding: FragmentAdvertisementBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewPagerAdapter: AdvertViewPagerAdapter
    private lateinit var recyclerViewAdapter: AdvertReviewRecyclerViewAdapter
    private var id: Long? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAdvertisementBinding.inflate(inflater, container, false)
        val view = binding.root

        id = arguments?.getLong("ID")
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

    private fun initView() {
        id?.let { viewModel.initView(it) }
        viewPagerAdapter = AdvertViewPagerAdapter()
        binding.viewPagerAdvertisement.adapter = viewPagerAdapter

        recyclerViewAdapter = AdvertReviewRecyclerViewAdapter(
            listOf()
        )
        binding.viewPagerAdvertisement.adapter = viewPagerAdapter
    }

    private fun initObservers() {
        viewModel.data.observe(viewLifecycleOwner, Observer {
            when (it) {
                is UIState.Success<*> -> {
                    val dataSet = it.data as AdvertisementCardInfo?
                    if (dataSet != null) {
                        viewPagerAdapter.setItemList(dataSet.advertViewpagerItem)
                        recyclerViewAdapter.setItemList(dataSet.reviewsList)
                        binding.collapsingToolbar.title = dataSet.name
                        binding.ratingBarAdvert.rating = dataSet.rating
                        binding.tvAdvertFeedback.text = when (dataSet.reviewsList.size) {
                            0 -> "Нет отзывов"
                            else -> "Отзывы (${dataSet.reviewsList.size})"
                        }
                        binding.tvAdvertDuration.text = "${dataSet.duration} ч."
                        binding.tvAdvertTransportation.text = dataSet.transportation.name
                        binding.tvAdvertAge.text = "${dataSet.ageRestrictions}+"
                        binding.tvAdvertNumber.text = "${dataSet.visitorsCount}"
                        binding.tvAdvertIndividual.text = if(dataSet.isIndividual) "да" else "нет"
                        binding.tvAdvertCategory.text = dataSet.category.name
                        binding.tvAdvertCity.text = dataSet.city.name
                        binding.tvAdvertDescription.text = dataSet.description
                        binding.tvAdvertSellerName.text = "${dataSet.seller.lastName} ${dataSet.seller.firstName}"
                        Glide.with(requireContext())
                            .load("${Utils.IMAGE_URL}${dataSet.seller.photo}")
                            .placeholder(R.drawable.viewpager_item2)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .centerCrop()
                            .into(binding.ivAdvertCircularAvatar)



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

                is UIState.Unauthorised -> {
                }

                else -> {}
            }
        })
    }

    companion object {
        fun newInstance(id: Long): AdvertisementFragment {
            val fragment = AdvertisementFragment()
            val args = Bundle()
            args.putLong("ID", id)
            fragment.setArguments(args)
            return fragment
        }
    }
}