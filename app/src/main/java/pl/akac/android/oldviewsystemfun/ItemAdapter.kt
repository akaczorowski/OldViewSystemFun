package pl.akac.android.oldviewsystemfun

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.RecyclerView
import pl.akac.android.oldviewsystemfun.viewModels.Item

class ItemAdapter(val itemClickListener: (Item)->Unit) : RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {

    private val diffCallback = ItemDiffCallback()
    private val asyncDiffer = AsyncListDiffer<Item>(this, diffCallback)

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title = itemView.findViewById<TextView>(R.id.title)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ItemViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = when (viewType) {
            R.layout.item_view1 -> layoutInflater.inflate(R.layout.item_view1, parent, false)
            R.layout.item_view2 -> layoutInflater.inflate(R.layout.item_view2, parent, false)
            else -> throw IllegalStateException()
        }

        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ItemViewHolder,
        position: Int
    ) {
        val data = asyncDiffer.currentList[position]

        holder.title.text = data.title
        holder.itemView.setOnClickListener {
            val itemData = asyncDiffer.currentList[holder.bindingAdapterPosition]
            itemClickListener(itemData)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position % 2 == 0) R.layout.item_view1 else R.layout.item_view2
    }

    override fun getItemCount(): Int {
        return asyncDiffer.currentList.size
    }

    fun submitList(list: List<Item>) {
        asyncDiffer.submitList(list)
    }
}