package com.example.mvvm.arch_reduxmvvm.base.helper

import android.app.Application
import com.example.mvvm.model.base.helper.ResourceHelper

/**
 * @author beemo
 * @since 2020-03-04
 */
class ResourceHelperImpl(
    val context: Application
):ResourceHelper {

    override fun getString(stringResId: Int): String =
        context.getString(stringResId)

}