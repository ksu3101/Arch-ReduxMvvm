package com.example.mvvm.common.exts

/**
 * @author beemo
 * @since 2020-03-04
 */

fun <E> emptyMutableList() : MutableList<E> = arrayListOf()

fun <E> List<E>?.isNullOrEmpty(): Boolean = (this == null || this.isEmpty())

fun <E> List<E>?.isNotNullOrEmpty(): Boolean = !(this.isNullOrEmpty())

fun <E> List<E?>?.availableIndex(position: Int): Boolean {
    if (position < 0 || this == null || this.isEmpty()) return false
    return (position < this.size)
}

fun <E> List<E?>?.getElement(position: Int): E? =
    (if (this.availableIndex(position)) this?.get(position) else null)

fun <E> List<E>.addAll(list: List<E>): List<E> =
    this.toMutableList()
        .apply { this.addAll(list) }


fun <E> List<E>.add(e: E): List<E> =
    this.toMutableList()
        .apply { this.add(e) }
