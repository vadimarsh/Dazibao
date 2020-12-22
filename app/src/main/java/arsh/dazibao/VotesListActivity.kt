package arsh.dazibao

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import arsh.dazibao.adapter.IdeasListAdapter
import arsh.dazibao.adapter.VotesListAdapter
import arsh.dazibao.model.Vote
import kotlinx.android.synthetic.main.activity_votes_list.*
import kotlinx.coroutines.launch
import retrofit2.Response
import splitties.toast.toast

class VotesListActivity : AppCompatActivity(), IdeasListAdapter.OnAuthorClickListener {
    private val ideaId: Long by lazy { intent.getLongExtra("ideaId", 0L) }
    private var dialog: ProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_votes_list)
    }
    override fun onAuthorClicked(authorId: Long, position: Int) {
        val intent = Intent(this@VotesListActivity, MainActivity::class.java)
        intent.putExtra("authorId", authorId)
        startActivity(intent)
        finish()
    }
    override fun onStart() {
        super.onStart()
        lifecycleScope.launch {
            dialog = ProgressDialog(this@VotesListActivity).apply {
                setMessage(this@VotesListActivity.getString(R.string.please_wait))
                setTitle(R.string.loading_posts)
                setCancelable(false)
                setProgressBarIndeterminate(true)
                show()
            }
            val result: Response<List<Vote>> = App.repository.getVotes(ideaId)

            dialog?.dismiss()
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
        }
    }
}