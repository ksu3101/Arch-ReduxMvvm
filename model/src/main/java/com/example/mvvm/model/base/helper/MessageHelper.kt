package com.example.mvvm.model.base.helper

import androidx.annotation.StringRes

/**
 * @author beemo
 * @since 2020-03-02
 */
interface MessageHelper {

    fun showingGeneralToast(
        @StringRes messageResId: Int
    )

    fun showingErrorToast(
        @StringRes errorMessageResId: Int,
        errorMessageStr: String? = null
    )

}