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

    interface OnVoteBtnClickListener {
        fun onLikeBtnClicked(item: Idea, position: Int)
        fun onDisLikeBtnClicked(item: Idea, position: Int)
    }
    interface OnShowVotesClickListener{
        fun onShowVotesBtnClicked(item: Idea, position: Int)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val ideaItemView =
            LayoutInflater.from(parent.context).inflate(R.layout.idea_item, parent, false)
        return IdeaItemViewHolder(this, ideaItemView)
    }

    override fun getItemCount(): Int {
        return items.size
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
