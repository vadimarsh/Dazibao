package arsh.dazibao

import android.app.ProgressDialog
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import arsh.dazibao.adapter.IdeasListAdapter
import arsh.dazibao.adapter.IdeasListAdapter.OnVoteBtnClickListener
import arsh.dazibao.model.Idea
import com.example.arshkotlin9.api.Token
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dialog.*
import kotlinx.android.synthetic.main.layout_footer.*
import kotlinx.coroutines.launch
import retrofit2.Response
import splitties.activities.start
import splitties.toast.toast
import java.io.IOException

class MainActivity : AppCompatActivity(), OnVoteBtnClickListener,
    IdeasListAdapter.OnShowVotesClickListener, IdeasListAdapter.OnLoadMoreBtnClickListener,
    IdeasListAdapter.OnAuthorClickListener {

    private lateinit var ideaListAdapter: IdeasListAdapter

    private var progressDialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(mainTb)
        requestToken()
        swipeLayout.setOnRefreshListener { refreshIdeas() }
        fab.setOnClickListener { start<NewIdeaActivity>() }
        recView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            ideaListAdapter = IdeasListAdapter(mutableListOf<Idea>())
            adapter = ideaListAdapter
                .apply {
                    voteBtnClickListener = this@MainActivity
                    showVotesClickListener = this@MainActivity
                    loadMoreBtnClickListener = this@MainActivity
                    authorClickListener = this@MainActivity
                }
        }
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

    private fun createDialog(): AlertDialog.Builder {
        val dialog = AlertDialog.Builder(this@MainActivity).setView(R.layout.dialog)
        return dialog
    }

    private fun refreshIdeas() {
        lifecycleScope.launch {
            try {
                window.setFlags(
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                )
                val newPostsResponse = App.repository.getIdeasRecent()

                if (newPostsResponse.isSuccessful) {
                    val adap = recView.adapter as IdeasListAdapter
                    adap.loadNewItems((newPostsResponse.body().orEmpty()))
                }
                swipeLayout.isRefreshing = false
                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            } catch (e: IOException) {
                swipeLayout.isRefreshing = false

                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                val dialog = createDialog().show()
                dialog.retryBtn.setOnClickListener {
                    swipeLayout.isRefreshing=true
                    refreshIdeas()
                    dialog.dismiss()
                }
                dialog.cancelBtn.setOnClickListener { dialog.dismiss() }
                return@launch
                //toast(R.string.msg_connection_err)
            }

        }
    }

    override fun onStart() {
        super.onStart()
        lifecycleScope.launch {
            try {
                progressDialog = ProgressDialog(this@MainActivity).apply {
                    setMessage(this@MainActivity.getString(R.string.please_wait))
                    setTitle(R.string.loading_posts)
                    setCancelable(false)
                    setProgressBarIndeterminate(true)
                    show()
                }
                val response = App.repository.getMe()
                if (response.isSuccessful) {
                    val user = response.body()!!
                    if (user.onlyReader) {
                        fab.visibility = View.GONE
                    }

                }

                val authorId = intent.getLongExtra(INTENT_EXTRA_AUTHOR, 0L)
                val result: Response<List<Idea>> = if (authorId != 0L) {
                    fab.visibility = View.GONE

                    App.repository.getIdeasByAuthor(authorId = authorId)
                } else {
                    App.repository.getIdeasRecent()
                }

                if (result.isSuccessful) {
                    recView.apply {
                        layoutManager = LinearLayoutManager(this@MainActivity)
                        ideaListAdapter.loadNewItems(result.body().orEmpty())
                    }
                } else {
                    toast(R.string.msg_auth_err)
                }
                progressDialog?.dismiss()
            } catch (e: IOException) {

                progressDialog?.dismiss()

                val dialog = createDialog().show()
                dialog.retryBtn.setOnClickListener {
                    swipeLayout.isRefreshing=true
                    refreshIdeas()
                    dialog.dismiss()
                }
                dialog.cancelBtn.setOnClickListener { dialog.dismiss() }
                return@launch

                //toast(R.string.msg_connection_err)
            }
        }
    }

    override fun onLikeBtnClicked(item: Idea, position: Int) {

        lifecycleScope.launch {
            try {
                if (!item.likedByMe && !item.dislikedByMe) {
                    item.likeActionPerforming = true
                    with(recView) {
                        adapter!!.notifyItemChanged(position, IdeasListAdapter.Payload.LIKE_ACTION)
                        val response = App.repository.likeIdea(item.id)
                        item.likeActionPerforming = false
                        if (response.isSuccessful) {
                            item.updateLikes(response.body()!!)
                        }
                        adapter!!.notifyItemChanged(position, IdeasListAdapter.Payload.LIKE_ACTION)
                    }

                } else {
                    toast(getString(R.string.msg_already_voted))
                }
            } catch (e: IOException) {
                toast(R.string.msg_connection_err)
            }
        }
    }

    override fun onDisLikeBtnClicked(item: Idea, position: Int) {

        lifecycleScope.launch {
            try {
                if (!item.likedByMe && !item.dislikedByMe) {
                    item.disLikeActionPerforming = true
                    with(recView) {
                        adapter!!.notifyItemChanged(
                            position,
                            IdeasListAdapter.Payload.DISLIKE_ACTION
                        )
                        val response = App.repository.dislikeIdea(item.id)
                        item.disLikeActionPerforming = false
                        if (response.isSuccessful) {
                            item.updateDisLikes(response.body()!!)
                        }
                        adapter!!.notifyItemChanged(
                            position,
                            IdeasListAdapter.Payload.DISLIKE_ACTION
                        )
                    }
                } else {
                    toast(getString(R.string.msg_already_voted))
                }
            } catch (e: IOException) {
                //createDialog()
                //toast(R.string.msg_connection_err)
            }
        }
    }

    override fun onShowVotesBtnClicked(item: Idea, position: Int) {
        start<VotesListActivity> {
            putExtra(INTENT_EXTRA_IDEA, item.id)
        }
    }

    override fun onLoadMoreBtnClickListener(last: Long, size: Int) {
        lifecycleScope.launch {
            try {
                val response =
                    App.repository.getPostsBefore(last)

                if (response.isSuccessful) {
                    val newItems = response.body().orEmpty()
                    ideaListAdapter.refreshItems(newItems)
                    ideaListAdapter.notifyItemRangeInserted(size + newItems.size, newItems.size)
                }

            } catch (e: IOException) {
                //toast(R.string.msg_connection_err)
                val dialog = createDialog().show()
                dialog.retryBtn.setOnClickListener {
                    onLoadMoreBtnClickListener(last,size)
                    dialog.dismiss()
                }
                dialog.cancelBtn.setOnClickListener { dialog.dismiss() }
                return@launch
            }
        }
    }

    override fun onAuthorClicked(authorId: Long, position: Int) {
        start<MainActivity> {
            putExtra(INTENT_EXTRA_AUTHOR, authorId)
        }
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
                    toast(R.string.msg_push_token_failed)
                }
            }
        }
    }
}