package pl.akac.android.oldviewsystemfun

import androidx.recyclerview.widget.DiffUtil
import pl.akac.android.oldviewsystemfun.viewModels.Item

class ItemDiffCallback : DiffUtil.ItemCallback<Item>() {
    override fun areItemsTheSame(
        oldItem: Item,
        newItem: Item
    ): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: Item,
        newItem: Item
    ): Boolean {
        return oldItem == newItem
    }
}