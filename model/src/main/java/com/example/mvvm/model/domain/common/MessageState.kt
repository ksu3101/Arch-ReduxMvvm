package com.example.mvvm.model.domain.common

import androidx.annotation.StringRes
import com.example.mvvm.model.base.redux.State

/**
 * @author beemo
 * @since 2020-03-02
 */

sealed class MessageState : State

object HandledMessageState: MessageState()

data class ShowingGeneralToastState(
    @StringRes val messageResId: Int
): MessageState()

data class ShowingErrorToastState(
    @StringRes val errorMessageResId: Int,
    val errorMessageStr: String? = null
): MessageState()