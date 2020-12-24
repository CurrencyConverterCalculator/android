/*
 * Copyright (c) 2020 Mustafa Ozhan. All rights reserved.
 */

package com.github.mustafaozhan.ccc.android.ui.currencies

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.DiffUtil
import com.github.mustafaozhan.basemob.adapter.BaseVBRecyclerViewAdapter
import com.github.mustafaozhan.ccc.android.util.setBackgroundByName
import com.github.mustafaozhan.ccc.client.ui.currencies.CurrenciesEvent
import com.github.mustafaozhan.ccc.common.model.Currency
import mustafaozhan.github.com.mycurrencies.R
import mustafaozhan.github.com.mycurrencies.databinding.ItemCurrenciesBinding

class CurrenciesAdapter(
    private val currenciesEvent: CurrenciesEvent
) : BaseVBRecyclerViewAdapter<Currency, ItemCurrenciesBinding>(CurrenciesDiffer()) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = RatesVBViewHolder(
        ItemCurrenciesBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
    )

    override fun onBindViewHolder(
        holder: BaseVBViewHolder<Currency, ItemCurrenciesBinding>,
        position: Int
    ) {
        holder.itemView.startAnimation(
            AnimationUtils.loadAnimation(
                holder.itemView.context,
                R.anim.fall_down
            )
        )
        super.onBindViewHolder(holder, position)
    }

    override fun onViewDetachedFromWindow(holder: BaseVBViewHolder<Currency, ItemCurrenciesBinding>) {
        super.onViewDetachedFromWindow(holder)
        holder.itemView.clearAnimation()
    }

    inner class RatesVBViewHolder(itemBinding: ItemCurrenciesBinding) :
        BaseVBViewHolder<Currency, ItemCurrenciesBinding>(itemBinding) {

        override fun onItemBind(item: Currency) = with(itemBinding) {
            imgIcon.setBackgroundByName(item.name)
            txtSettingItem.text = item.getVariablesOneLine()
            checkBox.isChecked = item.isActive
            root.setOnClickListener { currenciesEvent.onItemClick(item) }
            root.setOnLongClickListener { currenciesEvent.onItemLongClick() }
        }
    }

    class CurrenciesDiffer : DiffUtil.ItemCallback<Currency>() {
        override fun areItemsTheSame(oldItem: Currency, newItem: Currency) = oldItem == newItem

        override fun areContentsTheSame(oldItem: Currency, newItem: Currency) =
            oldItem.isActive == newItem.isActive
    }
}
