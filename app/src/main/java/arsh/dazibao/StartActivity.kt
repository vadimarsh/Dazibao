package arsh.dazibao

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.lifecycle.lifecycleScope
import kotlinx.android.synthetic.main.activity_start.*
import kotlinx.android.synthetic.main.activity_start.mainTb
import kotlinx.coroutines.launch
import splitties.activities.start
import splitties.toast.toast

class StartActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)
        setSupportActionBar(mainTb)
        if (isAuthenticated(this)) {
            start<MainActivity>()
            finish()
        } else {
            but_login.setOnClickListener {
                if (!isValid(et_password.text.toString()) || (et_password.text.toString()
                        .isEmpty())
                ) {
                    ti_password.error = getString(R.string.msg_paswd_invalid_err)
                } else {
                    lifecycleScope.launch {
                        progressBar.isIndeterminate = true
                        progressBar.visibility = View.VISIBLE
                        window.setFlags(
                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                        )
                        try {
                            val response =
                                App.repository.authenticate(
                                    et_login.text.toString(),
                                    et_password.text.toString()

                                )
                            if (response.isSuccessful && response.code() != 400) {
                                toast(getString(R.string.msg_auth_succ))
                                setUserAuth(response.body()!!.token,this@StartActivity)

                                start<MainActivity>()
                                finish()
                            } else {
                                toast(getString(R.string.msg_auth_err))
                            }
                        } catch (e: Exception) {
                            toast(getString(R.string.msg_connection_err))
                        }
                        progressBar.visibility = View.GONE
                        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                    }
                }
            }
            but_reg.setOnClickListener {
                start<RegisterActivity>()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (isAuthenticated(this)) {
            start<MainActivity>()
            finish()
        }
    }




}