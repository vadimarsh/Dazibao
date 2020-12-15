package arsh.dazibao

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import arsh.dazibao.model.Idea

class IdeasListAdapter(val items:MutableList<Idea>):
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val ideaItemView =
            LayoutInflater.from(parent.context).inflate(R.layout.idea_item, parent, false)
        return IdeaItemViewHolder(ideaItemView)
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
    fun refreshItems(posts: MutableList<Idea>) {
        items.addAll(posts)
        notifyDataSetChanged()
    }

    fun loadNewItems(posts: MutableList<Idea>) {
        items.clear()
        items.addAll(posts)
        notifyDataSetChanged()
    }
}
