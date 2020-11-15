/*
 Copyright (c) 2020 Mustafa Ozhan. All rights reserved.
 */
package com.github.mustafaozhan.ui.main.bar

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.github.mustafaozhan.data.model.Currency
import com.github.mustafaozhan.ui.base.adapter.BaseDBRecyclerViewAdapter
import mustafaozhan.github.com.mycurrencies.databinding.ItemBarBinding

class BarAdapter(
    private val barEvent: BarEvent
) : BaseDBRecyclerViewAdapter<Currency, ItemBarBinding>(CalculatorDiffer()) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = CalculatorDBViewHolder(ItemBarBinding.inflate(
        LayoutInflater.from(parent.context),
        parent,
        false)
    )

    inner class CalculatorDBViewHolder(itemBinding: ItemBarBinding) :
        BaseDBViewHolder<Currency, ItemBarBinding>(itemBinding) {

        override fun onItemBind(item: Currency) = with(itemBinding) {
            this.item = item
            this.event = barEvent
        }
    }

    class CalculatorDiffer : DiffUtil.ItemCallback<Currency>() {
        override fun areItemsTheSame(oldItem: Currency, newItem: Currency) = false

        override fun areContentsTheSame(oldItem: Currency, newItem: Currency) = false
    }
}
