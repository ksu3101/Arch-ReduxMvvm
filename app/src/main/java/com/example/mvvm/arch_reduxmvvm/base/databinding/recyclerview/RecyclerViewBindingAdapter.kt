package com.example.mvvm.arch_reduxmvvm.base.databinding.recyclerview

import androidx.annotation.LayoutRes
import androidx.databinding.BindingAdapter
import androidx.databinding.ObservableList
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * @author beemo
 * @since 2020/03/27
 */

@BindingAdapter(value = ["items", "ViewProvider", "onItemClickListener"], requireAll = false)
fun <E> bindItems(
    rv: RecyclerView,
    items: ObservableList<E>,
    viewTypeProvider: ViewTypeProvider<E>? = null,
    onItemClickListener: ((E) -> Unit)? = null
) {
    if (rv.adapter is RecyclerViewModelAdapter<*>) {
        rv.adapter?.notifyDataSetChanged()
    } else {
        val adapter = RecyclerViewModelAdapter(items, viewTypeProvider, onItemClickListener)
        rv.adapter = adapter
    }
}

@BindingAdapter(value = ["items", "itemViewResId", "onItemClickListener"], requireAll = false)
fun <E> bindItems(
    rv: RecyclerView,
    items: ObservableList<E>,
    @LayoutRes itemViewResId: Int,
    onItemClickListener: ((E) -> Unit)? = null
) {
    if (rv.adapter is RecyclerViewModelAdapter<*>) {
        rv.adapter?.notifyDataSetChanged()
    } else {
        val adapter = RecyclerViewModelAdapter(items, { itemViewResId }, onItemClickListener)
        rv.adapter = adapter
    }
}

@BindingAdapter("layoutVertical")
fun setVerticalLayoutManager(rv: RecyclerView, isVertical: Boolean) {
    rv.layoutManager = LinearLayoutManager(
        rv.context,
        if (isVertical) LinearLayoutManager.VERTICAL else LinearLayoutManager.HORIZONTAL,
        false
    )
}

@BindingAdapter("fixedItemSize")
fun setFixedItemSize(rv: RecyclerView, isFixedItemSize: Boolean) {
    rv.setHasFixedSize(isFixedItemSize)
}