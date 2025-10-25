package pl.akac.android.oldviewsystemfun

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.RecyclerView

class FadeItemAnimator : DefaultItemAnimator() {

    override fun animateAdd(holder: RecyclerView.ViewHolder): Boolean {
        holder.itemView.alpha = 0f
        holder.itemView.animate()
            .alpha(1f)
            .setDuration(addDuration)
            .setListener(null)
        return super.animateAdd(holder)
    }

    override fun animateRemove(holder: RecyclerView.ViewHolder): Boolean {
        holder.itemView.animate()
            .alpha(0f)
            .setDuration(removeDuration)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    holder.itemView.alpha = 1f
                }
            })
        return super.animateRemove(holder)
    }
}