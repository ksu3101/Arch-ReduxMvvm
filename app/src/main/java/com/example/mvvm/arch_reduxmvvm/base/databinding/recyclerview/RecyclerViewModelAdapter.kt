package com.example.mvvm.arch_reduxmvvm.base.databinding.recyclerview

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableArrayList
import androidx.databinding.ObservableList
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.example.mvvm.common.exts.getElement
import com.example.mvvm.common.exts.ifNotNull

/**
 * recyclerview 에서 각 position 에 해당하는 item 들에 해당 하는 view resource id
 * 를 제공 하는 provider function.
 */
typealias ViewTypeProvider<T> = ((itemTypeByPosition: T) -> Int)

/**
 * @author beemo
 * @since 2020-03-04
 */
class RecyclerViewModelAdapter<E>(
    private var items: ObservableList<E> = ObservableArrayList(),
    private val viewTypeProvider: ViewTypeProvider<E>? = null,
    private val onItemClickListener: ((E) -> Unit)? = null
) : RecyclerView.Adapter<DatabindingViewHolder>() {
    private val onListChangedListener = OnListChangedListener<ObservableList<E>>(this)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DatabindingViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(viewType, parent, false)
        val bindingViewHodler = DatabindingViewHolder(view)
        onItemClickListener?.let { listener ->
            bindingViewHodler.itemView.setOnClickListener {
                listener(getItem(bindingViewHodler.adapterPosition))
            }
        }
        return bindingViewHodler
    }

    override fun onBindViewHolder(holder: DatabindingViewHolder, position: Int) {
        val item = getItem(position)
        holder.binder?.let {
            //it.setVariable(BR.vm, item)
            //it.executePendingBindings()
        }
    }

    override fun getItemViewType(position: Int): Int {
        return viewTypeProvider.ifNotNull({ viewTypeProvider ->
            viewTypeProvider(getItem(position))
        }, { 0 })
    }

    override fun getItemCount(): Int = items.size

    fun getItem(position: Int): E {
        return items.getElement(position)
            ?: throw IndexOutOfBoundsException("Elements of position [$position] has not founded.")
    }
}

class DatabindingViewHolder(
    itemView: View
) : RecyclerView.ViewHolder(itemView) {
    val binder: ViewDataBinding? = DataBindingUtil.bind(itemView)
}

private class OnListChangedListener<T : ObservableList<*>>(
    val adapter: RecyclerViewModelAdapter<*>
) : ObservableList.OnListChangedCallback<T>() {
    override fun onChanged(sender: T) {
        adapter.notifyDataSetChanged()
    }

    override fun onItemRangeRemoved(sender: T, positionStart: Int, itemCount: Int) {
        adapter.notifyItemRangeRemoved(positionStart, itemCount)
    }

    override fun onItemRangeMoved(sender: T, fromPosition: Int, toPosition: Int, itemCount: Int) {
        adapter.notifyItemMoved(fromPosition, itemCount)
    }

    override fun onItemRangeInserted(sender: T, positionStart: Int, itemCount: Int) {
        adapter.notifyItemRangeInserted(positionStart, itemCount)
    }

    override fun onItemRangeChanged(sender: T, positionStart: Int, itemCount: Int) {
        adapter.notifyItemChanged(positionStart, itemCount)
    }
}

