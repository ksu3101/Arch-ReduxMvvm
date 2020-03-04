package com.example.mvvm.model.base.redux

import android.util.Log
import com.example.mvvm.common.LOG_TAG
import com.example.mvvm.common.exts.getSuperClassNames

/**
 * @author beemo
 * @since 2020-02-24
 */
class AppState(
    val states: Map<String, State>
) : State {

    inline fun <reified S : State> getCurrentState(): S? {
        for (state in states.values) {
            if (state is S) return state
        }
        return null
    }

    inline fun <reified S : State> getStateBy(key: String): S? {
        val currentState = states.get(key) ?: return null
        return currentState as S
    }

    fun printStateLogs() {
        Log.d(LOG_TAG, "AppState (\n")
        for (state in states) {
            Log.d(LOG_TAG, String.format("\t[%-24s]\t%s \n", state.getSuperClassNames(), state))
        }
        Log.d(LOG_TAG, ")")
    }
}