package com.benyq.tikbili.scene.horizontal.layer

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.benyq.tikbili.R
import com.benyq.tikbili.player.playback.layer.base.DialogLayer


/**
 *
 * @author benyq
 * @date 4/26/2024
 *
 */
abstract class DialogListLayer<T>: DialogLayer() {

    private var _recyclerView: RecyclerView? = null
    private val listAdapter = ListAdapter<T>()

    protected fun adapter(): ListAdapter<T> {
        return listAdapter
    }

    protected var _title: String = ""

    fun setTitle(title: String) {
        _title = title
    }


    override fun onCreateLayerView(parent: ViewGroup): View? {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.view_dialog_list_layer, parent, false)
        view.setOnClickListener {
            animateDismiss()
        }
        val textView = view.findViewById<TextView>(R.id.title)
        textView.text = _title
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.adapter = listAdapter
        recyclerView.layoutManager = CenterLayoutManager(parent.context)
        _recyclerView = recyclerView
        return view
    }

    protected fun smoothMoveToPosition(position: Int) {
        _recyclerView?.smoothScrollToPosition(position)
    }


    data class Item<T>(
        val obj: T,
        val text: String,
    )

    class ListAdapter<T>: RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        private val items = mutableListOf<Item<T>>()
        private var _selected: Item<T>? = null
        private var itemAction: ((Int, T)->Unit)? = null

        fun setItemAction(action: (Int, T)->Unit) {
            itemAction = action
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return object: RecyclerView.ViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_dialog_list_layer, parent, false)
            ){}
        }

        override fun getItemCount() = items.size

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            holder.itemView.findViewById<TextView>(R.id.text).text = items[position].text
            holder.itemView.setOnClickListener {
                itemAction?.invoke(position, items[position].obj)
            }
            holder.itemView.isSelected = _selected == items[position]
        }

        fun getSelected(): Item<T>? {
            return _selected
        }

        fun findItem(obj: T): Item<T>? {
            for (item in items) {
                if (obj == item.obj) return item
            }
            return null
        }

        fun findIndex(obj: T): Int {
            items.forEachIndexed { index, item ->
                if (item.obj == obj) return index
            }
            return -1
        }

        fun setList(data: List<Item<T>>) {
            items.clear()
            items.addAll(data)
            notifyDataSetChanged()
        }

        fun setSelected(item: Item<T>?) {
            if (_selected != item) {
                _selected = item
                notifyDataSetChanged()
            }
        }

    }


    class CenterLayoutManager : LinearLayoutManager {
        constructor(context: Context?) : super(context)
        constructor(context: Context?, orientation: Int, reverseLayout: Boolean) : super(
            context,
            orientation,
            reverseLayout
        )

        constructor(
            context: Context?,
            attrs: AttributeSet?,
            defStyleAttr: Int,
            defStyleRes: Int,
        ) : super(context, attrs, defStyleAttr, defStyleRes)

        override fun smoothScrollToPosition(
            recyclerView: RecyclerView,
            state: RecyclerView.State,
            position: Int,
        ) {
            val smoothScroller: RecyclerView.SmoothScroller =
                CenterSmoothScroller(recyclerView.context)
            smoothScroller.targetPosition = position
            startSmoothScroll(smoothScroller)
        }

        private class CenterSmoothScroller internal constructor(context: Context?) :
            LinearSmoothScroller(context) {
            override fun calculateDtToFit(
                viewStart: Int,
                viewEnd: Int,
                boxStart: Int,
                boxEnd: Int,
                snapPreference: Int,
            ): Int {
                return boxStart + (boxEnd - boxStart) / 2 - (viewStart + (viewEnd - viewStart) / 2)
            }
        }
    }

}