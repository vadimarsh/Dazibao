package arsh.dazibao.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import arsh.dazibao.R
import arsh.dazibao.model.Idea

class IdeasListAdapter(val items: MutableList<Idea>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var voteBtnClickListener: OnVoteBtnClickListener? = null
    var showVotesClickListener: OnShowVotesClickListener? = null
    var loadMoreBtnClickListener: OnLoadMoreBtnClickListener? = null
    var authorClickListener: OnAuthorClickListener? = null

    companion object {
        const val ITEM_TYPE_IDEA = 1
        const val ITEM_TYPE_FOOTER = 2
    }


    class Payload {
        companion object {
            const val LIKE_ACTION = 0
            const val DISLIKE_ACTION = 1
        }
    }

    interface OnVoteBtnClickListener {
        fun onLikeBtnClicked(item: Idea, position: Int)
        fun onDisLikeBtnClicked(item: Idea, position: Int)
    }

    interface OnShowVotesClickListener {
        fun onShowVotesBtnClicked(item: Idea, position: Int)
    }

    interface OnAuthorClickListener {
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
        return items.size + 1
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            position == items.size -> ITEM_TYPE_FOOTER
            else -> ITEM_TYPE_IDEA
        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isNotEmpty()) {
            when (holder) {
                is IdeaItemViewHolder -> {
                    when (payloads[0]) {
                        Payload.LIKE_ACTION -> {
                            holder.bindLike(items[position])
                        }
                        Payload.DISLIKE_ACTION -> {
                            holder.bindDisLike(items[position])
                        }
                    }
                }
            }
        }
        super.onBindViewHolder(holder, position, payloads)
    }

    fun refreshItems(ideas: List<Idea>) {
        items.addAll(ideas)
        notifyDataSetChanged()
    }

    fun loadNewItems(ideas: List<Idea>) {
        items.clear()
        items.addAll(ideas)
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val ideaIndex = position
        when (holder) {
            is IdeaItemViewHolder -> holder.bind(items[ideaIndex])
        }
    }


}
