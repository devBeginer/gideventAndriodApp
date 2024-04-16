package ru.gidevent.androidapp.ui.advertisement

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import dagger.hilt.android.AndroidEntryPoint
import ru.gidevent.andriodapp.R
import ru.gidevent.andriodapp.databinding.FragmentAdvertisementBinding
import ru.gidevent.androidapp.data.model.advertisement.AdvertisementCardInfo
import ru.gidevent.androidapp.ui.SharedViewModel
import ru.gidevent.androidapp.ui.advertisement.adapter.AdvertReviewRecyclerViewAdapter
import ru.gidevent.androidapp.ui.advertisement.adapter.AdvertViewPagerAdapter
import ru.gidevent.androidapp.ui.advertisement.adapter.PriceListRecyclerViewAdapter
import ru.gidevent.androidapp.ui.makeBooking.MakeBookingFragment
import ru.gidevent.androidapp.ui.profileScreen.EditProfileFragment
import ru.gidevent.androidapp.ui.profileScreen.SellerFragment
import ru.gidevent.androidapp.ui.state.UIState
import ru.gidevent.androidapp.utils.Utils
import ru.gidevent.androidapp.utils.showSnack

@AndroidEntryPoint
class AdvertisementFragment : Fragment() {
    private val viewModel: AdvertisementViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by viewModels({requireActivity()})
    private var _binding: FragmentAdvertisementBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewPagerAdapter: AdvertViewPagerAdapter
    private lateinit var recyclerViewAdapter: AdvertReviewRecyclerViewAdapter
    private lateinit var priceListAdapter: PriceListRecyclerViewAdapter
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
        binding.rvAdvertReviews.adapter = recyclerViewAdapter

        priceListAdapter = PriceListRecyclerViewAdapter(
            listOf()
        )
        binding.rvAdvertPricelist.adapter = priceListAdapter

        binding.tvAdvertReviewsAdd.setOnClickListener {
            FeedbackBottomSheetDialog(viewModel)
                .show(parentFragmentManager, "priceBottomSheetDialog")
        }

        binding.btnAdvertSchedule.setOnClickListener {
            AdvertScheduleBottomSheet(viewModel)
                .show(parentFragmentManager, "scheduleBottomSheetDialog")
        }
        binding.btnAdvertBuy.setOnClickListener {
            id?.let { advertId-> requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment, MakeBookingFragment.newInstance(advertId)).addToBackStack(null).commit()
            }

        }



        binding.toolbarAdvertisement.setOnMenuItemClickListener { menuItem->
            when(menuItem.itemId){
                R.id.advertisement_favourite->{
                    id?.let {
                        sharedViewModel.showProgressIndicator(true)
                        viewModel.postFavourite(it)
                    }
                }
            }
            true
        }

        binding.toolbarAdvertisement.setNavigationOnClickListener() {
            parentFragmentManager.popBackStack()
            //onBackPressed() // возврат на предыдущий activity
        }
        binding.ivAdvertCircularAvatar.setOnClickListener {
            parentFragmentManager.beginTransaction().replace(R.id.main_nav_host_fragment, SellerFragment()).addToBackStack(null).commit()
        }
    }

    private fun initObservers() {
        viewModel.data.observe(viewLifecycleOwner, Observer {
            when (it) {
                is UIState.Success<*> -> {
                    sharedViewModel.showProgressIndicator(false)
                    val dataSet = it.data as AdvertisementCardInfo?
                    if (dataSet != null) {
                        viewPagerAdapter.setItemList(dataSet.advertViewpagerItem)
                        recyclerViewAdapter.setItemList(dataSet.reviewsList)
                        dataSet.priceList?.let { it1 -> priceListAdapter.setItemList(it1) }

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
                        val menuItem = binding.toolbarAdvertisement.menu.getItem(0/*R.id.advertisement_favourite*/)
                        menuItem.icon = if (dataSet.favourite == true) ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.baseline_favorite_active_24
                        ) else ContextCompat.getDrawable(
                            requireContext(),
                            R.drawable.twotone_favorite_24
                        )



                    }
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

                is UIState.Unauthorised -> {
                    sharedViewModel.showProgressIndicator(false)
                }
                is UIState.Loading ->{
                    sharedViewModel.showProgressIndicator(true)
                }

                else -> {}
            }
        })

        viewModel.favourite.observe(viewLifecycleOwner, Observer { advert ->
            if (advert != null) {
                val menuItem =
                    binding.toolbarAdvertisement.menu.getItem(0/*R.id.advertisement_favourite*/)
                menuItem.icon = if (advert.isFavourite) ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.baseline_favorite_active_24
                ) else ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.twotone_favorite_24
                )
                sharedViewModel.showProgressIndicator(false)
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