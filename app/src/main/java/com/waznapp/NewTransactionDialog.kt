package com.waznapp

import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.waznapp.Utilities.DEFAULT_CURRENCY
import com.waznapp.Utilities.PURCHASE
import com.waznapp.database.Transaction
import com.waznapp.database.TransactionViewModel
import com.waznapp.databinding.NewTransactionDialogBinding

class NewTransactionDialog () : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val viewModel = ViewModelProvider(this).get(TransactionViewModel::class.java)
        viewModel.pendingTransaction = readTransactionFromArguments()


        val binding  : NewTransactionDialogBinding = DataBindingUtil.inflate (inflater, R.layout.new_transaction_dialog, container, false)
        binding.transactionViewModel =  viewModel
        binding.lifecycleOwner = this
        binding.purposeListAdapter = PurposeListAdapter()

        viewModel.transactionState.observe(viewLifecycleOwner, Observer {handleTransactionState(it)})

        return binding.root
    }


    /**
     *  Handles the state of the transaction. Dismisses the dialog when transaction is Done.
     *  @param state TransactionState
     * */
    private fun handleTransactionState (state : TransactionViewModel.TransactionState){
        when (state) {
            TransactionViewModel.TransactionState.Done -> dismiss()
            TransactionViewModel.TransactionState.Failed -> dismiss()
        }
    }


    private fun readTransactionFromArguments () : Transaction {
        // Read extra details coming from notification.
        arguments?.let {
            args ->
            val amount = args.getDouble(getString(R.string.amount_intent_key), 0.0)
            val merchant = args.getString(getString(R.string.merchant_intent_key), "")
            val currency = args.getString(getString(R.string.currency_intent_key), DEFAULT_CURRENCY)
            val date = args.getLong(getString(R.string.date_intent_key), 0)
            val transactionType = args.getString(getString(R.string.transaction_type_intent_key), PURCHASE)
            val transaction = Transaction ()
            transaction.amount = amount
            transaction.merchant = merchant
            transaction.currency = currency
            transaction.date = date
            transaction.type = transactionType
            return transaction
        }
        // Return empty transaction
        return Transaction ()
    }

}