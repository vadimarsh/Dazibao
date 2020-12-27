package arsh.dazibao

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.core.content.edit
import androidx.lifecycle.lifecycleScope
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.activity_register.mainTb
import kotlinx.android.synthetic.main.activity_start.but_reg
import kotlinx.android.synthetic.main.activity_start.et_login
import kotlinx.android.synthetic.main.activity_start.progressBar
import kotlinx.coroutines.launch
import splitties.toast.longToast
import splitties.toast.toast

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        setSupportActionBar(mainTb)

        but_reg.setOnClickListener {
            val password = et_pswd.text.toString()
            val repeatedPassword = et_pswdr.text.toString()
            if (password != repeatedPassword) {
                et_pswdr.error = getString(R.string.msg_difpsw_err)

            } else if (!isValid(password) || (password.isEmpty())) {
                et_pswdr.error = getString(R.string.msg_paswd_invalid_err)

            } else {
                lifecycleScope.launch {
                    progressBar.isIndeterminate = true
                    progressBar.visibility = View.VISIBLE

                    window.setFlags(
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                    )
                    try {
                        val response = App.repository.register(et_login.text.toString(), password)

                        progressBar.visibility = View.GONE
                        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                        if (response.isSuccessful) {
                            toast(R.string.succes_reg)
                            setUserAuth(response.body()!!.token,this@RegisterActivity)
                            finish()
                        } else {
                            longToast(R.string.error_reg)
                        }
                    } catch (e: Exception) {
                        toast(getString(R.string.msg_connection_err))
                    }
                }
            }
        }
    }
}