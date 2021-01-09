package arsh.dazibao.adapter

import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.layout_footer.view.*

class FooterViewHolder(val adapter: IdeasListAdapter, view: View) : RecyclerView.ViewHolder(view) {
    init {
        with(itemView) {
            loadMoreButton.setOnClickListener {
                loadMoreButton.isEnabled = false
                progressBar.visibility = View.VISIBLE
                var ideasCount = adapter.items.size - 1
                val beforeId = if(adapter.items.size>0){
                    adapter.items[adapter.items.size - 1].id.toLong()
                }else{
                    ideasCount = 0
                    -1
                }
                adapter.loadMoreBtnClickListener?.onLoadMoreBtnClickListener(
                    beforeId,
                    ideasCount
                )
                loadMoreButton.isEnabled = true
                progressBar.visibility = View.GONE
            }
        }
    }
}