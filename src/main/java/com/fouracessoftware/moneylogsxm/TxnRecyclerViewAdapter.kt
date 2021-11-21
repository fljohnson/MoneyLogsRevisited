package com.fouracessoftware.moneylogsxm

import android.content.res.Resources
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContentProviderCompat.requireContext

import com.fouracessoftware.moneylogsxm.placeholder.PlaceholderContent.PlaceholderItem
import com.fouracessoftware.moneylogsxm.databinding.FragmentTxnListRowBinding
import com.fouracessoftware.moneylogsxm.datadeal.Txn

/**
 * [RecyclerView.Adapter] that can display a [PlaceholderItem].
 * TODO: Replace the implementation with code for your data type.
 */
class TxnRecyclerViewAdapter(
    private val values: List<Txn>?
) : RecyclerView.Adapter<TxnRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            FragmentTxnListRowBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values?.get(position)
        if(item != null) {

           holder.idView.text = holder.idView.context.getString(R.string.amt_for_cat,
                MainListViewModel.currencize(item.amount),item.category_name)

            holder.contentView.text = item.date
        }
    }

    override fun getItemCount(): Int = if (values == null) {
                                                        0
                                                    }
                                                    else {
                                                        values.size
                                                    }

    inner class ViewHolder(binding: FragmentTxnListRowBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val idView: TextView = binding.itemNumber
        val contentView: TextView = binding.content

        override fun toString(): String {
            return super.toString() + " '" + contentView.text + "'"
        }
    }

}