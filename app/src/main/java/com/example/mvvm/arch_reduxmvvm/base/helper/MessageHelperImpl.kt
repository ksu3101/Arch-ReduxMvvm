package com.example.mvvm.arch_reduxmvvm.base.helper

import android.content.Context
import android.widget.Toast
import com.example.mvvm.common.exts.isNotNullOrEmpty
import com.example.mvvm.model.base.helper.MessageHelper

/**
 * @author beemo
 * @since 2020-03-04
 */
class MessageHelperImpl(
    val context: Context
) : MessageHelper {

    override fun showingGeneralToast(messageResId: Int) {
        Toast.makeText(context, messageResId, Toast.LENGTH_SHORT).show()
    }

    override fun showingErrorToast(errorMessageResId: Int, errorMessageStr: String?) {
        val message = if (errorMessageResId == 0 && errorMessageStr.isNullOrEmpty()) {
            throw IllegalArgumentException("Message parameters has not avaialble.")
        } else {
            if (errorMessageStr.isNotNullOrEmpty()) errorMessageStr
            else context.getString(errorMessageResId)
        }
        // todo : set error backgeround color to Toast.
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}