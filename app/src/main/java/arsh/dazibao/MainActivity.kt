package arsh.dazibao

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import arsh.dazibao.adapter.IdeasListAdapter
import arsh.dazibao.adapter.IdeasListAdapter.OnVoteBtnClickListener
import arsh.dazibao.model.Idea
import com.example.arshkotlin9.api.Token
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.installations.FirebaseInstallations
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_footer.*
import kotlinx.coroutines.launch
import retrofit2.Response
import splitties.activities.start
import splitties.toast.toast
import java.io.IOException

class MainActivity : AppCompatActivity(), OnVoteBtnClickListener,
    IdeasListAdapter.OnShowVotesClickListener, IdeasListAdapter.OnLoadMoreBtnClickListener,
    IdeasListAdapter.OnAuthorClickListener

{

    private var dialog: ProgressDialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(mainTb)
requestToken()
        swipeLayout.setOnRefreshListener { refreshPosts() }
        fab.setOnClickListener { start<NewIdeaActivity>() }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_settings, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (R.id.item_settings == item.itemId) {
            start<SettingsActivity>()
        }
        return super.onOptionsItemSelected(item)
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
            val response = App.repository.getMe()
            if (response.isSuccessful) {
                val user = response.body()!!
                if(user.onlyReader){
                    fab.visibility = View.GONE
                }

            }

            val authorId = intent.getLongExtra("authorId", 0L)
            val result: Response<List<Idea>> = if (authorId != 0L) {
                fab.visibility = View.GONE

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
            } else {
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
        val int =
            Intent(this@MainActivity, VotesListActivity::class.java).putExtra("ideaId", item.id)
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
    }
    private fun requestToken() {
        with(GoogleApiAvailability.getInstance()) {
            val code = isGooglePlayServicesAvailable(this@MainActivity)
            if (code == ConnectionResult.SUCCESS) {
                return@with
            }

            if (isUserResolvableError(code)) {
                getErrorDialog(this@MainActivity, code, 9000).show()
                return
            }

            toast(getString(R.string.msg_no_googleapi))
            return
        }
        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener {
        //FirebaseInstallations.getInstance().id.addOnSuccessListener {
            lifecycleScope.launch {
                val token = Token(it.token)
                try {
                    App.repository.firebasePushToken(token)
                } catch (e: IOException) {
                    toast(R.string.push_token_failed)
                }
            }
        }
    }
}