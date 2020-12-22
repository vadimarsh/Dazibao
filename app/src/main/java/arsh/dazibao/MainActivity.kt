package arsh.dazibao

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import arsh.dazibao.adapter.IdeasListAdapter
import arsh.dazibao.adapter.IdeasListAdapter.OnVoteBtnClickListener
import arsh.dazibao.model.Idea
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_footer.*
import kotlinx.coroutines.launch
import retrofit2.Response
import splitties.activities.start
import splitties.toast.toast

class MainActivity : AppCompatActivity(), OnVoteBtnClickListener,
    IdeasListAdapter.OnShowVotesClickListener, IdeasListAdapter.OnLoadMoreBtnClickListener,
    IdeasListAdapter.OnAuthorClickListener
//    PostAdapter.OnLoadMoreBtnClickListener
{

    private var dialog: ProgressDialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        swipeLayout.setOnRefreshListener { refreshPosts() }
        fab.setOnClickListener { start<NewIdeaActivity>() }
    }

    private fun refreshPosts() {
        lifecycleScope.launch {
            val newPostsResponse = App.repository.getIdeasRecent()
            swipeLayout.isRefreshing = false
            if (newPostsResponse.isSuccessful) {
                val adap = recView.adapter as IdeasListAdapter
                adap.loadNewItems((newPostsResponse.body() as MutableList<Idea>?)!!)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        lifecycleScope.launch {
            dialog = ProgressDialog(this@MainActivity).apply {
                setMessage(this@MainActivity.getString(R.string.please_wait))
                setTitle(R.string.loading_posts)
                setCancelable(false)
                setProgressBarIndeterminate(true)
                show()
            }


            val authorId = intent.getLongExtra("authorId", 0L)
            val result: Response<List<Idea>> = if (authorId != 0L) {
                App.repository.getIdeasByAuthor(authorId = authorId)
            } else {
                App.repository.getIdeasRecent()
            }

            dialog?.dismiss()
            if (result.isSuccessful) {
                recView.apply {
                    layoutManager = LinearLayoutManager(this@MainActivity)
                    adapter = IdeasListAdapter(result.body() as MutableList<Idea>)
                        .apply {
                            voteBtnClickListener = this@MainActivity
                            showVotesClickListener = this@MainActivity
                            loadMoreBtnClickListener = this@MainActivity
                            authorClickListener = this@MainActivity
                        }
                }
            }
         else {
            toast(R.string.msg_auth_err)
        }
    }
}

override fun onLikeBtnClicked(item: Idea, position: Int) {

    lifecycleScope.launch {
        if (!item.likedByMe && !item.dislikedByMe) {
            item.likeActionPerforming = true
            with(recView) {
                adapter!!.notifyItemChanged(position)
                val response = App.repository.likeIdea(item.id)
                item.likeActionPerforming = false
                if (response.isSuccessful) {
                    item.updateLikes(response.body()!!)
                }
                adapter!!.notifyItemChanged(position)
            }

        } else {
            toast(getString(R.string.msg_already_voted))
        }
    }
}

override fun onDisLikeBtnClicked(item: Idea, position: Int) {

    lifecycleScope.launch {
        if (!item.likedByMe && !item.dislikedByMe) {
            item.disLikeActionPerforming = true
            with(recView) {
                adapter!!.notifyItemChanged(position)
                val response = App.repository.dislikeIdea(item.id)
                item.disLikeActionPerforming = false
                if (response.isSuccessful) {
                    item.updateDisLikes(response.body()!!)
                }
                adapter!!.notifyItemChanged(position)


            }
        } else {
            toast(getString(R.string.msg_already_voted))
        }
    }
}

override fun onShowVotesBtnClicked(item: Idea, position: Int) {
    val int = Intent(this@MainActivity, VotesListActivity::class.java).putExtra("ideaId", item.id)
    startActivity(int)
}

override fun onLoadMoreBtnClickListener(last: Long, size: Int) {

    lifecycleScope.launch {
        val response =
            App.repository.getPostsBefore(last)
        progressBar.visibility = View.INVISIBLE
        loadMoreButton.isEnabled = true

        if (response.isSuccessful) {
            val newItems = response.body() as MutableList<Idea>
            print(response.body())
            with(recView) {
                val adap = adapter as IdeasListAdapter
                adap.refreshItems(newItems)
                adap.notifyItemRangeInserted(size + newItems.size, newItems.size)
            }

        }
    }
}

override fun onAuthorClicked(authorId: Long, position: Int) {
    val intent = Intent(this@MainActivity, MainActivity::class.java)
    intent.putExtra("authorId", authorId)
    startActivity(intent)
    finish()
}

}