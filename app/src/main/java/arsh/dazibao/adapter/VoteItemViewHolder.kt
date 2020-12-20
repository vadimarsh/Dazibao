package arsh.dazibao.adapter

import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import arsh.dazibao.R
import arsh.dazibao.model.MediaType
import arsh.dazibao.model.Vote
import arsh.dazibao.model.VoteType
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.vote_item.view.*


class VoteItemViewHolder(val adapter: VotesListAdapter, val view: View) :
    RecyclerView.ViewHolder(view) {

    fun bind(vote: Vote) {
        with(view) {

            authorNameTv.text = vote.authorName
            /*when (idea.userType) {
                UserType.HATER -> {
                     rankTv.text = context.getString(R.string.hater)
                     rankTv.setTextColor(ContextCompat.getColor(context, R.color.colorRed))
                }
                UserType.PROMOTER -> {
                     rankTv.text = context.getString(R.string.promoter)
                     rankTv.setTextColor(ContextCompat.getColor(context, R.color.colorGreen))
                }
            }*/
            if (vote.avatar != null) {
                Log.d("zzz", "avatarloading:" + vote.avatar.url)
                when (vote.avatar.mediaType) {
                    MediaType.IMAGE -> loadImageAvatar(avatarIv, vote.avatar.url)
                }
            } else {
                avatarIv.setImageResource(R.drawable.avatar_default)
            }
            createdTv.text = vote.date

            when {
                vote.action == VoteType.LIKE -> {
                    actionIv.setImageResource(R.drawable.like_active)
                    rankTv.setText("Адепт")
                    rankTv.setTextColor(ContextCompat.getColor(context, R.color.colorGreen))
                }
                else -> {
                    actionIv.setImageResource(R.drawable.dislike_active)
                    rankTv.setText("Критик")
                    rankTv.setTextColor(ContextCompat.getColor(context, R.color.colorRed))
                }
            }
        }
    }

    private fun loadImageAvatar(avatarImg: ImageView, url: String) {
        Glide.with(avatarImg.context)
            .load(url)
            .into(avatarImg)
    }
}
