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
    private var id: Long? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentContainerEditBinding.inflate(inflater, container, false)
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

        val nextFragment = id?.let{
            CreateAdvertisementFragment.newInstance(it)
        } ?: CreateAdvertisementFragment()



        childFragmentManager.beginTransaction()
            .replace(R.id.edit_nav_host_fragment, nextFragment).addToBackStack(null).commit()

    }

    companion object {
        fun newInstance(id: Long): EditContainerFragment {
            val fragment = EditContainerFragment()
            val args = Bundle()
            args.putLong("ID", id)
            fragment.setArguments(args)
            return fragment
        }
    }
}