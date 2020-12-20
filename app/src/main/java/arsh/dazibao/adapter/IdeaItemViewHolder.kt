package arsh.dazibao.adapter

import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import arsh.dazibao.R
import arsh.dazibao.model.Idea
import arsh.dazibao.model.MediaType
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.idea_item.view.*
import splitties.toast.toast


class IdeaItemViewHolder(val adapter: IdeasListAdapter, val view: View) :
    RecyclerView.ViewHolder(view) {

    fun bind(idea: Idea) {
        with(view) {
            showVotesBtn.setOnClickListener{
                val currentPosition = adapterPosition
                if (currentPosition != RecyclerView.NO_POSITION) {
                    val item = adapter.items[currentPosition]
                    adapter.showVotesClickListener?.onShowVotesBtnClicked(item, currentPosition)
                }

            }

            likeButton.setOnClickListener {
                val currentPosition = adapterPosition
                if (currentPosition != RecyclerView.NO_POSITION) {
                    val item = adapter.items[currentPosition]
                    if (item.likeActionPerforming) {
                        context.toast(context.getString(R.string.like_in_progress))
                    } else {
                        adapter.voteBtnClickListener?.onLikeBtnClicked(item, currentPosition)

                    }
                }
            }
            dislikeButton.setOnClickListener {
                val currentPosition = adapterPosition
                if (currentPosition != RecyclerView.NO_POSITION) {
                    val item = adapter.items[currentPosition]
                    if (item.disLikeActionPerforming) {
                        context.toast(context.getString(R.string.like_in_progress))
                    } else {
                        adapter.voteBtnClickListener?.onDisLikeBtnClicked(item, currentPosition)
                        Log.d("zzz", "дизЛайк проведен!")
                    }
                }
            }

            authorNameTv.text = idea.authorName
            /*when (idea.userType) {
                UserType.HATER -> {
                    textBadge.text = context.getString(R.string.hater)
                    textBadge.setTextColor(ContextCompat.getColor(context, R.color.colorRed))
                }
                UserType.PROMOTER -> {
                    textBadge.text = context.getString(R.string.promoter)
                    textBadge.setTextColor(ContextCompat.getColor(context, R.color.colorGreen))
                }
            }*/
            if(idea.avatar!=null) {
                Log.d("zzz","avatarloading:"+idea.avatar.url)
                when (idea.avatar.mediaType) {
                    MediaType.IMAGE -> loadImageAvatar(avatarIv, idea.avatar.url)
                }
            }
            else {
                avatarIv.setImageResource(R.drawable.avatar_default)
            }
            createdTv.text = idea.created
            contentTv.text = idea.content
            likeTv.text = idea.likes.toString()
            dislikeTv.text = idea.dislikes.toString()
            if (idea.attachment != null) {
                when (idea.attachment.mediaType) {
                    MediaType.IMAGE -> loadImage(photoIv, idea.attachment.url)
                }
            } else {
                photoIv.setImageResource(0)
            }
            when {
                idea.link != null -> linkBtn.setImageResource(R.drawable.ic_link)
            }


            when {
                idea.likeActionPerforming -> likeButton.setImageResource(R.drawable.like_proc)
                idea.likedByMe -> {
                    likeButton.setImageResource(R.drawable.like_active)
                    likeTv.setTextColor(ContextCompat.getColor(context,
                        R.color.colorGreen
                    ))
                }
                else -> {
                    likeButton.setImageResource(R.drawable.like)
                    likeTv.setTextColor(ContextCompat.getColor(context,
                        R.color.colorBlack
                    ))
                }
            }
            when {
                idea.disLikeActionPerforming -> dislikeButton.setImageResource(R.drawable.dislike_proc)
                idea.dislikedByMe -> {
                    dislikeButton.setImageResource(R.drawable.dislike_active)
                    dislikeTv.setTextColor(ContextCompat.getColor(context,
                        R.color.colorRed
                    ))
                }
                else -> {
                    dislikeButton.setImageResource(R.drawable.dislike)
                    dislikeTv.setTextColor(ContextCompat.getColor(context,
                        R.color.colorBlack
                    ))
                }
            }
        }

    }

    /*
        fun bindLike(idea: Idea) {
            with(view) { *
                when{
                    idea.likeActionPerforming -> imageLike.setImageResource(R.drawable.like_wating)
                    idea.isLike -> {
                        imageLike.setImageResource(R.drawable.like_active)
                        textLike.setTextColor(ContextCompat.getColor(context, R.color.colorGreen))
                        textLike.text = idea.like.toString()
                    }
                }

            }
        }

     */
    private fun loadImage(photoImg: ImageView, url: String) {
        Glide.with(photoImg.context)
            .load(url)
            .into(photoImg)
    }
    private fun loadImageAvatar(avatarImg: ImageView, url: String) {
        Glide.with(avatarImg.context)
            .load(url)
            .into(avatarImg)
    }
}