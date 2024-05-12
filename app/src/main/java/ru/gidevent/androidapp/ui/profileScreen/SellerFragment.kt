package ru.gidevent.androidapp.ui.profileScreen

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
import ru.gidevent.andriodapp.databinding.FragmentSellerBinding
import ru.gidevent.androidapp.ui.SharedViewModel
import ru.gidevent.androidapp.utils.Utils


@AndroidEntryPoint
class SellerFragment: Fragment() {
    private val sharedViewModel: SharedViewModel by viewModels({requireActivity()})

    private val viewModel: SellerViewModel by viewModels()

    private var _binding: FragmentSellerBinding? = null
    private val binding get() = _binding!!
    var sellerId: Long? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSellerBinding.inflate(inflater, container, false)
        val view = binding.root
        sellerId = arguments?.getLong("ID")
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sharedViewModel.showProgressIndicator(true)
        sellerId?.let {
            viewModel.initView(it)
        }
        binding.btnSellerBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        viewModel.data.observe(viewLifecycleOwner, Observer {
            if (it!=null){
                binding.tvSellerInfoName.text = "${it.lastName} ${it.firstName}"
                binding.tvSellerInfoAbout.text = it.about
                binding.tvSellerInfoAdvertsCount.text = it.advertsCount.toString()
                binding.tvSellerReviews.text = it.feedbackCount.toString()
                binding.tvSellerAverageRating.text = it.averageRating.toString()
                binding.tvSellerInfoApproved.text = if(it.isVerified) "Подтвержден" else "Не подтвержден"
                Glide.with(requireContext())
                    .load(/*${Utils.IMAGE_URL}*/"${it.photo}")
                    .placeholder(R.drawable.avatar_placeholder)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .centerCrop()
                    .into(binding.ivSellerInfo)
            }
            sharedViewModel.showProgressIndicator(false)
        })
    }
}