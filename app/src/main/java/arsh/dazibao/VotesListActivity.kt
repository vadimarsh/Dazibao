package arsh.dazibao

import android.app.ProgressDialog
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import arsh.dazibao.adapter.IdeasListAdapter
import arsh.dazibao.adapter.VotesListAdapter
import arsh.dazibao.model.Vote
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_votes_list.*
import kotlinx.android.synthetic.main.activity_votes_list.mainTb
import kotlinx.coroutines.launch
import retrofit2.Response
import splitties.activities.start
import splitties.toast.toast
import java.io.IOException

class VotesListActivity : AppCompatActivity(), IdeasListAdapter.OnAuthorClickListener {
    private val ideaId: Long by lazy { intent.getLongExtra(INTENT_EXTRA_IDEA, 0L) }
    private var dialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_votes_list)
        setSupportActionBar(mainTb)
    }

    override fun onAuthorClicked(authorId: Long, position: Int) {
        start<MainActivity> {
            putExtra(INTENT_EXTRA_AUTHOR, authorId)
        }
        finish()
    }

    override fun onStart() {
        super.onStart()
        lifecycleScope.launch {
            try {
                dialog = ProgressDialog(this@VotesListActivity).apply {
                    setMessage(this@VotesListActivity.getString(R.string.please_wait))
                    setTitle(R.string.loading_posts)
                    setCancelable(false)
                    setProgressBarIndeterminate(true)
                    show()
                }
                val result: Response<List<Vote>> = App.repository.getVotes(ideaId)

               // dialog?.dismiss()
                if (result.isSuccessful) {
                    recViewVotes.apply {
                        layoutManager = LinearLayoutManager(this@VotesListActivity)
                        adapter = VotesListAdapter(result.body() as MutableList<Vote>)
                            .apply {
                                authorClickListener = this@VotesListActivity
                            }
                    }
                } else {
                    toast(R.string.msg_auth_err)
                }
            } catch (e: IOException) {
                toast(R.string.msg_connection_err)
            }
            finally {
                dialog?.dismiss()
            }
        }
    }
}