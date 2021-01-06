package arsh.dazibao.adapter

import android.content.Intent
import android.net.Uri
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
            authorNameTv.setOnClickListener {
                val currentPosition = adapterPosition
                if (currentPosition != RecyclerView.NO_POSITION) {
                    val item = adapter.items[currentPosition]
                    adapter.authorClickListener?.onAuthorClicked(item.authorId, currentPosition)
                }
            }
            showVotesBtn.setOnClickListener {
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
                        context.toast(context.getString(R.string.msg_like_in_progress))
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
                        context.toast(context.getString(R.string.msg_like_in_progress))
                    } else {
                        adapter.voteBtnClickListener?.onDisLikeBtnClicked(item, currentPosition)
                        Log.d("zzz", "дизЛайк проведен!")
                    }
                }
            }

            authorNameTv.text = idea.authorName
            if (idea.avatar != null) {
                when (idea.avatar.mediaType) {
                    MediaType.IMAGE -> loadImage(avatarIv, idea.avatar.url)
                }
            } else {
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

            if (idea.link != null) {
                linkBtn.setImageResource(R.drawable.ic_link)
                linkBtn.visibility = View.VISIBLE
                val uri: Uri = Uri.parse(idea.link)

                linkBtn.setOnClickListener {
                    ContextCompat.startActivity(
                        itemView.context,
                        Intent(Intent.ACTION_VIEW).apply { this.data = uri }, null
                    )
                }

            } else {
                linkBtn.visibility = View.GONE
            }


            when {
                idea.likeActionPerforming -> likeButton.setImageResource(R.drawable.like_proc)
                idea.likedByMe -> {
                    likeButton.setImageResource(R.drawable.like_active)
                    likeTv.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.colorGreen
                        )
                    )
                }
                else -> {
                    likeButton.setImageResource(R.drawable.like)
                    likeTv.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.colorBlack
                        )
                    )
                }
            }
            when {
                idea.disLikeActionPerforming -> dislikeButton.setImageResource(R.drawable.dislike_proc)
                idea.dislikedByMe -> {
                    dislikeButton.setImageResource(R.drawable.dislike_active)
                    dislikeTv.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.colorRed
                        )
                    )
                }
                else -> {
                    dislikeButton.setImageResource(R.drawable.dislike)
                    dislikeTv.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.colorBlack
                        )
                    )
                }
            }
        }

    }
    fun bindLike(idea: Idea) {
            with(view) {
                when {
                    idea.likeActionPerforming -> likeButton.setImageResource(R.drawable.like_proc)
                    idea.likedByMe -> {
                        likeButton.setImageResource(R.drawable.like_active)
                        likeTv.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.colorGreen
                            )
                        )
                    }
                    else -> {
                        likeButton.setImageResource(R.drawable.like)
                        likeTv.setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.colorBlack
                            )
                        )
                    }
                }

            }
    }
    fun bindDisLike(idea: Idea) {
        with(view) {when {
            idea.disLikeActionPerforming -> dislikeButton.setImageResource(R.drawable.dislike_proc)
            idea.dislikedByMe -> {
                dislikeButton.setImageResource(R.drawable.dislike_active)
                dislikeTv.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.colorRed
                    )
                )
            }
            else -> {
                dislikeButton.setImageResource(R.drawable.dislike)
                dislikeTv.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.colorBlack
                    )
                )
            }
        }}
    }

    private fun loadImage(photoImg: ImageView, url: String) {
        Glide.with(photoImg.context)
            .load(url)
            .into(photoImg)
    }


}
