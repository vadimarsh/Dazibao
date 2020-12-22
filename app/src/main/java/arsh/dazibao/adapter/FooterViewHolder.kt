package arsh.dazibao.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.layout_footer.view.*

class FooterViewHolder(val adapter: IdeasListAdapter, view: View) : RecyclerView.ViewHolder(view) {
    init {
        with(itemView) {
            loadMoreButton.setOnClickListener {
                loadMoreButton.isEnabled = false
                progressBar.visibility = View.VISIBLE
                adapter.loadMoreBtnClickListener?.onLoadMoreBtnClickListener(
                    adapter.items[adapter.items.size - 1].id.toLong(),
                    adapter.items.size - 1
                )
            }
        }
    }
}