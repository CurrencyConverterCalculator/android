package mustafaozhan.github.com.mycurrencies.ui.main.fragment.settings

import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.jakewharton.rxbinding2.widget.textChanges
import io.reactivex.rxkotlin.addTo
import mustafaozhan.github.com.mycurrencies.R
import mustafaozhan.github.com.mycurrencies.base.fragment.BaseViewBindingFragment
import mustafaozhan.github.com.mycurrencies.databinding.FragmentSettingsBinding
import mustafaozhan.github.com.mycurrencies.function.extension.checkAd
import mustafaozhan.github.com.mycurrencies.function.extension.gone
import mustafaozhan.github.com.mycurrencies.function.extension.reObserve
import mustafaozhan.github.com.mycurrencies.function.extension.visible
import mustafaozhan.github.com.mycurrencies.model.Currency
import mustafaozhan.github.com.mycurrencies.tool.Toasty.showToasty
import timber.log.Timber

/**
 * Created by Mustafa Ozhan on 2018-07-12.
 */
class SettingsFragment : BaseViewBindingFragment<SettingsViewModel, FragmentSettingsBinding>() {

    companion object {
        fun newInstance(): SettingsFragment = SettingsFragment()
    }

    override fun bind() {
        binding = FragmentSettingsBinding.inflate(layoutInflater)
    }

    override fun getLayoutResId(): Int = R.layout.fragment_settings

    private val settingsAdapter: SettingsAdapter by lazy { SettingsAdapter() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setSupportActionBar(binding.toolbarFragmentSettings)
        viewModel.initData()
        initViews()
        initViewState()
        initRx()
        setListeners()
    }

    private fun initViewState() = viewModel.settingsViewStateLiveData
        .reObserve(this, Observer { settingsViewState ->
            binding.txtNoResult.gone()
            when (settingsViewState) {
                SettingsViewState.FewCurrency -> showToasty(requireContext(), R.string.choose_at_least_two_currency)
                SettingsViewState.NoResult -> {
                    settingsAdapter.refreshList(mutableListOf())
                    binding.txtNoResult.visible()
                }
                is SettingsViewState.Success -> settingsAdapter.refreshList(settingsViewState.currencyList)
            }
        })

    private fun initRx() = binding.editTextSearch
        .textChanges()
        .map { it.toString() }
        .subscribe(
            { viewModel.filterList(it) },
            { Timber.e(it) }
        ).addTo(compositeDisposable)

    private fun initViews() = with(binding) {
        recyclerViewSettings.apply {
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
            adapter = settingsAdapter
        }
        editTextSearch.setText("")
        adView.checkAd(R.string.banner_ad_unit_id_settings, viewModel.isRewardExpired)
    }

    private fun setListeners() {
        with(binding) {
            btnSelectAll.setOnClickListener {
                viewModel.updateCurrencyState(1)
                editTextSearch.setText("")
            }
            btnDeSelectAll.setOnClickListener {
                viewModel.updateCurrencyState(0)
                editTextSearch.setText("")
                viewModel.setCurrentBase(null)
            }
        }

        settingsAdapter.onItemClickListener = { currency: Currency, itemBinding, _ ->
            when (currency.isActive) {
                0 -> {
                    currency.isActive = 1
                    viewModel.updateCurrencyState(1, currency.name)
                    itemBinding.checkBox.isChecked = true
                }
                1 -> {
                    currency.isActive = 0
                    viewModel.updateCurrencyState(0, currency.name)
                    itemBinding.checkBox.isChecked = false
                }
            }
        }
    }
}
