package com.example.mvvm.model.domain.common

import androidx.annotation.StringRes
import com.example.mvvm.model.base.redux.Action

/**
 * @author beemo
 * @since 2020-03-02
 */

sealed class MessageAction : Action

object HandledMessageAction: MessageAction()

data class ShowingGeneralToast(
    @StringRes val messageResId: Int
): MessageAction()

data class ShowingErrorToast(
    @StringRes val errorMessageResId: Int,
    val errorMessageStr: String? = null
): MessageAction()