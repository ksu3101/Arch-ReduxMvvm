package com.example.mvvm.arch_reduxmvvm.base.helper

import android.content.SharedPreferences
import com.example.mvvm.model.base.helper.SharedPreferenceHelper

/**
 * @author beemo
 * @since 2020/03/13
 */
class SharedPreferenceHelperImpl(
    val sharedPref: SharedPreferences
) : SharedPreferenceHelper {

    // add implement function here!!

    private fun <T> saveItem(key: String, value: T) {
        val editor = sharedPref.edit()
        when (value) {
            is String -> editor.putString(key, value)
            is Long -> editor.putLong(key, value)
            is Int -> editor.putInt(key, value)
            is Boolean -> editor.putBoolean(key, value)
            is Float -> editor.putFloat(key, value)
            else -> return
        }
        editor.apply()
    }

    private fun getString(key: String, defValue: String? = null): String? {
        return sharedPref.getString(key, defValue) ?: defValue
    }

    private fun getLong(key: String, defValue: Long = 0L): Long {
        return sharedPref.getLong(key, defValue)
    }

    private fun remove(key: String) {
        sharedPref.edit().remove(key).apply()
    }

}