package arsh.dazibao.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import arsh.dazibao.R
import arsh.dazibao.model.Idea

class IdeasListAdapter(val items:MutableList<Idea>):
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var voteBtnClickListener: OnVoteBtnClickListener?=null
    var showVotesClickListener: OnShowVotesClickListener?=null
    var loadMoreBtnClickListener: OnLoadMoreBtnClickListener?=null
    var authorClickListener: OnAuthorClickListener?=null

    private val ITEM_TYPE_IDEA = 1
    private val ITEM_TYPE_FOOTER = 2

    interface OnVoteBtnClickListener {
        fun onLikeBtnClicked(item: Idea, position: Int)
        fun onDisLikeBtnClicked(item: Idea, position: Int)
    }
    interface OnShowVotesClickListener{
        fun onShowVotesBtnClicked(item: Idea, position: Int)
    }
    interface OnAuthorClickListener{
        fun onAuthorClicked(authorId: Long, position: Int)
    }
    interface OnLoadMoreBtnClickListener {
        fun onLoadMoreBtnClickListener(last: Long, size: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_TYPE_IDEA -> {
                val ideaItemView =
                    LayoutInflater.from(parent.context).inflate(R.layout.idea_item, parent, false)
                    IdeaItemViewHolder(this, ideaItemView)
            }
            else -> {
                val footerItemView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.layout_footer, parent, false)
                FooterViewHolder(this, footerItemView)
            }
        }
    }

    override fun getItemCount(): Int {
        return items.size+1
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            position == items.size -> ITEM_TYPE_FOOTER
            else -> ITEM_TYPE_IDEA
        }
    }
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val ideaIndex = position
        when (holder) {
            is IdeaItemViewHolder -> holder.bind(items[ideaIndex])
        }
    }
    fun refreshItems(ideas: MutableList<Idea>) {
        items.addAll(ideas)
        notifyDataSetChanged()
    }

    fun loadNewItems(ideas: MutableList<Idea>) {
        items.clear()
        items.addAll(ideas)
        notifyDataSetChanged()
    }
}
