package arsh.dazibao

import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import arsh.dazibao.model.Idea
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.launch
import retrofit2.Response
import splitties.activities.start
import splitties.toast.toast

class MainActivity : AppCompatActivity() {
    private var dialog: ProgressDialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        swipeLayout.setOnRefreshListener { refreshPosts() }
    }

    private fun refreshPosts() {
        lifecycleScope.launch {
            val newPostsResponse = App.repository.getPostsRecent()
            swipeLayout.isRefreshing = false
            if (newPostsResponse.isSuccessful) {
                val adap = recView.adapter as IdeasListAdapter
                adap?.loadNewItems((newPostsResponse.body() as MutableList<Idea>?)!!)
            }
        }
    }
    override fun onStart() {
        super.onStart()
        //fab.setOnClickListener(View.OnClickListener { start<NewPostActivity>() })
        lifecycleScope.launch {
            dialog = ProgressDialog(this@MainActivity).apply {
                setMessage(this@MainActivity.getString(R.string.please_wait))
                setTitle(R.string.loading_posts)
                setCancelable(false)
                setProgressBarIndeterminate(true)
                show()
            }
            val result: Response<List<Idea>> = App.repository.getPostsRecent()
            //println(result.body())
            dialog?.dismiss()
            if (result.isSuccessful) {
                recView.apply {
                    layoutManager = LinearLayoutManager(this@MainActivity)
                    adapter = IdeasListAdapter(result.body() as MutableList<Idea>).apply {
//                        likeBtnClickListener = this@PostsActivity
  //                      commentBtnClickListener = this@PostsActivity
    //                    loadMoreBtnClickListener = this@PostsActivity
                    }
                }
            } else {
                toast(R.string.msg_auth_err)
            }
        }
    }
        //initActivity()


    /*private fun initActivity() {
        val list = ArrayList<Idea>()
        list.add(Idea(1,"Vadim",1,System.currentTimeMillis(),"some content",67,3))
        list.add(Idea(2,"Admin",2,System.currentTimeMillis()))
        recView.layoutManager = LinearLayoutManager(this@MainActivity)
        recView.adapter = IdeasListAdapter(list);
        swipeLayout.setOnRefreshListener { refreshPosts() }
    }
*/

}