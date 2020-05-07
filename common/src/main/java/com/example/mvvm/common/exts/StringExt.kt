package com.example.mvvm.common.exts

/**
 * @author beemo
 * @since 2020-02-24
 */

private val REGEX_NUMBER: String = "-?\\d+(\\.\\d+)?"

fun String?.toNonNullString(): String {
    return this ?: ""
}

fun String?.isNotNullOrEmpty(): Boolean {
    return !(this.isNullOrEmpty())
}

fun String?.isNotNullOrBlank(): Boolean {
    return !(this.isNullOrBlank())
}

fun String?.isNumber(): Boolean {
    return if (this.isNullOrEmpty()) {
        false
    } else {
        REGEX_NUMBER.toRegex() matches this
    }
}

fun String?.isNotNumber(): Boolean {
    return !(this.isNumber())
}

inline fun String?.toInteger(integersFunc: (Int) -> Int): Int {
    this?.toIntOrNull()?.let {
        return integersFunc(it)
    }
    return 0
}

inline fun String?.ifItIntegerString(func: (Int) -> Unit) {
    this?.toIntOrNull()?.let {
        func(it)
    }
}

fun String?.notEqual(op: String?): Boolean {
    return if (this.isNullOrEmpty() || op.isNullOrEmpty()) {
        false
    } else {
        !(this.equals(op))
    }
}

fun String?.length(): Int {
    return if (this.isNullOrEmpty()) {
        0
    } else {
        this.length
    }
}

fun String?.appendOf(text: String?): String {
    return if (this == null && text == null) {
        ""
    } else if (this != null && text == null) {
        this
    } else if (this == null && text != null) {
        text
    } else {
        "$this$text"
    }
}

fun String?.startWith(text: String?): Boolean {
    if (this.isNullOrEmpty() || text.isNullOrEmpty()) {
        return false
    }
    return this.startsWith(text)
}

fun String?.contain(text: String?): Boolean {
    if (this.isNullOrEmpty() || text.isNullOrEmpty())
        return false
    return this.contains(text)
}

fun String?.substringFrom(startText: String?): String? {
    if (this.isNullOrEmpty()) return null
    if (startText.isNullOrEmpty()) return null
    if (!this.contain(startText)) return null
    return this.substring(this.lastIndexOf(startText) + 1)
}