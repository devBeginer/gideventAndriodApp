package ru.gidevent.androidapp.ui.edit.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import ru.gidevent.andriodapp.R
import ru.gidevent.andriodapp.databinding.FragmentContainerEditBinding
import ru.gidevent.andriodapp.databinding.FragmentMainScreenContainerBinding
import ru.gidevent.androidapp.ui.edit.CreateAdvertViewModel
import ru.gidevent.androidapp.ui.mainScreen.fragment.FavouritesFragment
import ru.gidevent.androidapp.ui.mainScreen.fragment.MainFragment
import ru.gidevent.androidapp.ui.mainScreen.fragment.ProfileFragment
import ru.gidevent.androidapp.ui.mainScreen.fragment.PurchasesFragment
import ru.gidevent.androidapp.ui.mainScreen.fragment.SearchFragment
import ru.gidevent.androidapp.ui.mainScreen.viewModel.MainViewModel

@AndroidEntryPoint
class EditContainerFragment : Fragment() {

    private val viewModel: CreateAdvertViewModel by viewModels()

    private var _binding: FragmentContainerEditBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentContainerEditBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        childFragmentManager.beginTransaction()
            .replace(R.id.edit_nav_host_fragment, CreateAdvertisementFragment()).commit()

    }
}