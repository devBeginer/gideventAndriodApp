package ru.gidevent.androidapp.ui.advertisement

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
import ru.gidevent.andriodapp.R
import ru.gidevent.andriodapp.databinding.BottomsheetDialogCitySearchBinding
import ru.gidevent.andriodapp.databinding.BottomsheetDialogUnauthorizedBinding
import ru.gidevent.androidapp.data.model.suggestionsRecyclerviewModels.SuggestionCity
import ru.gidevent.androidapp.data.model.suggestionsRecyclerviewModels.SuggestionRecyclerViewData
import ru.gidevent.androidapp.ui.login.fragment.SignInFragment
import ru.gidevent.androidapp.ui.mainScreen.adapter.CityPickRecyclerViewAdapter
import ru.gidevent.androidapp.ui.mainScreen.viewModel.SearchViewModel
import ru.gidevent.androidapp.ui.state.UIState
import ru.gidevent.androidapp.utils.queryInputAsFlow
import ru.gidevent.androidapp.utils.showSnack

class UnauthBottomSheetDialog(): BottomSheetDialogFragment() {
    private var _binding: BottomsheetDialogUnauthorizedBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomsheetDialogUnauthorizedBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnBsSignIn.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment, SignInFragment()).addToBackStack(null).commit()
        }

    }

}