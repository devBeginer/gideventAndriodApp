package ru.gidevent.androidapp.ui.advertisement

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.gidevent.RestAPI.model.City
import ru.gidevent.RestAPI.model.dto.CitySuggestion
import ru.gidevent.andriodapp.databinding.BottomsheetDialogCitySearchBinding
import ru.gidevent.andriodapp.databinding.BottomsheetDialogFeedbackBinding
import ru.gidevent.androidapp.data.model.advertisement.dto.NewFeedback
import ru.gidevent.androidapp.data.model.suggestionsRecyclerviewModels.SuggestionCity
import ru.gidevent.androidapp.data.model.suggestionsRecyclerviewModels.SuggestionRecyclerViewData
import ru.gidevent.androidapp.ui.edit.CreateAdvertViewModel
import ru.gidevent.androidapp.ui.mainScreen.adapter.CityPickRecyclerViewAdapter
import ru.gidevent.androidapp.ui.mainScreen.viewModel.SearchViewModel
import ru.gidevent.androidapp.ui.state.UIState
import ru.gidevent.androidapp.utils.queryInputAsFlow
import ru.gidevent.androidapp.utils.showSnack
@AndroidEntryPoint
class FeedbackBottomSheetDialog(private val viewModel: AdvertisementViewModel): BottomSheetDialogFragment() {
    private var _binding: BottomsheetDialogFeedbackBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomsheetDialogFeedbackBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.initFeedback()
        viewModel.feedbackData.observe(viewLifecycleOwner, Observer {
            if(it!=null){
                binding.etFeedbackReview.setText(it.text)
                binding.ratingBarFeedback.rating = it.rating.toFloat()
            }
        })

        binding.btnFeedbackPost.setOnClickListener {
            if(!binding.etFeedbackReview.text.isNullOrBlank()){
                CoroutineScope(Dispatchers.IO).launch {

                    val result = viewModel.postFeedback(NewFeedback(0, binding.ratingBarFeedback.rating.toInt(), binding.etFeedbackReview.text.toString()))
                    withContext(Dispatchers.Main){
                        when(result){
                            is UIState.Success<*>->{
                                viewModel.initView()
                                dismiss()
                            }
                            is UIState.Loading->{}
                            is UIState.Error->{}
                            is UIState.ConnectionError->{}
                            is UIState.Unauthorised->{}
                            is UIState.Idle->{}
                            else->{}
                        }
                    }
                }
            }
        }

    }
}