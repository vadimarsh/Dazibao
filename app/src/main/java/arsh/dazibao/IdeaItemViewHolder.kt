package arsh.dazibao

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import arsh.dazibao.model.Idea
import kotlinx.android.synthetic.main.idea_item.view.*


class IdeaItemViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

    fun bind(idea: Idea) {
        with(view) {
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
            /*when (idea.avatar?.mediaType) {
                AttachmentType.IMAGE -> loadImageAutor(
                    imageAutor,
                    idea.autor.attachment.urlUser
                )
            }*/
            createdTv.text = dateToStr(idea.created)
            contentTv.text = idea.content
            likeTv.text = idea.likes.toString()
            dislikeTv.text = idea.dislikes.toString()
            /*when {
                idea.link != "" -> imageLink.setImageResource(R.drawable.ic_active_link)
            }
*/
  /*          when {
                idea.likeActionPerforming -> imageLike.setImageResource(R.drawable.like_wating)
                idea.isLike -> {
                    imageLike.setImageResource(R.drawable.like_active)
                    textLike.setTextColor(ContextCompat.getColor(context, R.color.colorGreen))
                }

                else -> {
                    imageLike.setImageResource(R.drawable.like_no_active)
                    textLike.setTextColor(ContextCompat.getColor(context, R.color.colorBlack))
                }
            }
            when {
                idea.DisLikeActionPerforming -> imageDisLike.setImageResource(R.drawable.dislike_wating)
                idea.isDisLike -> {
                    imageDisLike.setImageResource(R.drawable.dislike_active)
                    textDisLike.setTextColor(ContextCompat.getColor(context, R.color.colorRed))
                }
                else -> {
                    imageDisLike.setImageResource(R.drawable.dislike_no_active)
                    textDisLike.setTextColor(
                        ContextCompat.getColor(
                            context,
                            R.color.colorBlack
                        )
                    )
                }
            }
            if (idea.attachment != null) {
                when (idea.attachment.mediaType) {
                    AttachmentType.IMAGE -> loadImage(imageIdea, idea.attachment.url)
                }
            } else {
                imageIdea.setImageResource(0)
            }

*/
        }

    }

    fun bindLike(idea: Idea) {
        with(view) {
/*
            when{
                idea.likeActionPerforming -> imageLike.setImageResource(R.drawable.like_wating)
                idea.isLike -> {
                    imageLike.setImageResource(R.drawable.like_active)
                    textLike.setTextColor(ContextCompat.getColor(context, R.color.colorGreen))
                    textLike.text = idea.like.toString()
                }
            }
*/

        }
    }
}
