package ru.gidevent.androidapp.ui.advertisement

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import ru.gidevent.andriodapp.databinding.BottomsheetDialogCustomerBookingInfoBinding
import ru.gidevent.androidapp.data.model.myAdverts.BookingInfoResponse
import ru.gidevent.androidapp.ui.SharedViewModel
import ru.gidevent.androidapp.ui.seller_management.adapter.BookingsGroupRecyclerViewAdapter
import ru.gidevent.androidapp.ui.mainScreen.viewModel.PurchasesViewModel
import ru.gidevent.androidapp.ui.state.UIStateAdvertList
import ru.gidevent.androidapp.utils.showSnack
import java.util.Calendar
import java.util.Locale
import ru.gidevent.androidapp.utils.Utils.toString

class CustomerBookingInfoBottomSheet(): BottomSheetDialogFragment() {
    private val viewModel: PurchasesViewModel by viewModels({requireParentFragment()})
    private val sharedViewModel: SharedViewModel by viewModels({requireActivity()})
    private var _binding: BottomsheetDialogCustomerBookingInfoBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: BookingsGroupRecyclerViewAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomsheetDialogCustomerBookingInfoBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        initData()

    }

    private fun initView() {
        adapter = BookingsGroupRecyclerViewAdapter(listOf())
        binding.rvBookingInfoCustomers.adapter = adapter
        binding.btnBookingInfoDecline.setOnClickListener {
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        viewModel.initView()
        super.onDismiss(dialog)
    }


    private fun initData() {
        viewModel.bottomSheetData.observe(viewLifecycleOwner, Observer {
            when(it){
                is UIStateAdvertList.Success<*> -> {
                    sharedViewModel.showProgressIndicator(false)
                    val dataSet = it.data as BookingInfoResponse?
                    if(dataSet!=null){
                        binding.tvBookingInfoBookingName.text = dataSet.advertisementName
                        val date = Calendar.getInstance(Locale.getDefault())
                        date.timeInMillis = dataSet.date
                        val eventTime = Calendar.getInstance(Locale.getDefault())
                        eventTime.timeInMillis = dataSet.eventTime
                        val bookingDate = Calendar.getInstance(Locale.getDefault())
                        bookingDate.timeInMillis = dataSet.bookingTime
                        binding.tvBookingInfoDate.text = date.time.toString("dd.MM.yy")
                        binding.tvBookingInfoTime.text = eventTime.time.toString("HH:mm")
                        binding.tvBookingInfoTimestamp.text = bookingDate.time.toString("dd.MM.yy HH:mm")
                        binding.tvBookingInfoConfirmed.text = if(dataSet.isApproved) "Подтверждено" else "Не подтверждено"
                        binding.tvBookingInfoCustomer.text = dataSet.userName
                        binding.tvBookingInfoCost.text = "${dataSet.totalPrice}₽"
                        adapter.setItemsList(dataSet.visitorGroups)
                    }
                }
                is UIStateAdvertList.Update<*> -> {
                    val confirmed = it.data as Boolean
                    binding.tvBookingInfoConfirmed.text = if(confirmed) "Подтверждено" else "Не подтверждено"
                    sharedViewModel.showProgressIndicator(false)
                }
                is UIStateAdvertList.Error -> {
                    sharedViewModel.showProgressIndicator(false)
                    showSnack(requireView(), it.message, 5)
                }
                is UIStateAdvertList.ConnectionError -> {
                    sharedViewModel.showProgressIndicator(false)
                    showSnack(requireView(), "Отсутствует интернет подключение", 3)
                }
                is UIStateAdvertList.Idle -> {

                }
                is UIStateAdvertList.Unauthorised -> {
                    binding.rvBookingInfoCustomers.visibility = View.GONE
                    sharedViewModel.showProgressIndicator(false)
                }
                is UIStateAdvertList.Loading -> {
                    sharedViewModel.showProgressIndicator(true)
                }
                else -> {}
            }
        } )
    }
}