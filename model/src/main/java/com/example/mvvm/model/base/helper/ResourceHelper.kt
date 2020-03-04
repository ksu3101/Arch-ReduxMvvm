package com.example.mvvm.model.base.helper

import androidx.annotation.StringRes

/**
 * @author beemo
 * @since 2020-03-04
 */
interface ResourceHelper {

    fun getString(@StringRes stringResId: Int): String

}