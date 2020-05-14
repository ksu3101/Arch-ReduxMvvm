package com.example.mvvm.arch_reduxmvvm.domain.fishs

import com.example.mvvm.arch_reduxmvvm.R
import com.example.mvvm.arch_reduxmvvm.base.BaseFragment
import com.example.mvvm.model.domain.fishs.FishsState
import com.example.mvvm.model.domain.fishs.FishsViewModel

/**
 * @author burkd
 * @since 5/10/2020
 */
class FishsFragment(
    override val vm: FishsViewModel
) : BaseFragment<FishsState>() {

    override fun getLayoutResId(): Int = R.layout.fishs_fragment

}