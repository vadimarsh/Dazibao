package arsh.dazibao.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import arsh.dazibao.R
import arsh.dazibao.model.Idea
import arsh.dazibao.model.Vote

class VotesListAdapter(val votes:List<Vote>):
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var authorClickListener: IdeasListAdapter.OnAuthorClickListener?=null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val voteItemView =
            LayoutInflater.from(parent.context).inflate(R.layout.vote_item, parent, false)
        return VoteItemViewHolder(this, voteItemView)
    }

    override fun getItemCount(): Int {
        return votes.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val voteIndex = position
        when (holder) {
            is VoteItemViewHolder -> holder.bind(votes[voteIndex])
        }
    }

}
