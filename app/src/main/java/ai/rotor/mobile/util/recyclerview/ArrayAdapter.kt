package ai.rotor.mobile.util.recyclerview

import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import java.util.*

abstract class ArrayAdapter<Item, ItemViewHolder : ViewHolder> : androidx.recyclerview.widget.RecyclerView.Adapter<ItemViewHolder>() {

    private var items: List<Item> = ArrayList()

    fun setItems(items: List<Item>) {
        this.items = items
        notifyDataSetChanged()
    }

    override fun getItemCount() = items.size

    protected fun getItemAtPosition(position: Int) = items[position]
}
