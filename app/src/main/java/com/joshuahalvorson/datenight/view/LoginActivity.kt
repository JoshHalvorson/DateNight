package com.joshuahalvorson.datenight.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.joshuahalvorson.datenight.R
import kotlinx.android.synthetic.main.activity_login.*
import org.blockstack.android.sdk.BlockstackSession
import org.blockstack.android.sdk.Result
import org.blockstack.android.sdk.model.UserData
import org.blockstack.android.sdk.model.toBlockstackConfig

class LoginActivity : AppCompatActivity() {
    private val TAG = MainActivity::class.java.simpleName

    private var _blockstackSession: BlockstackSession? = null

    private var fromSettings: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val config = "https://joshhalvorson.github.io/blockstack-android-web-app/public/"
            .toBlockstackConfig(arrayOf(org.blockstack.android.sdk.Scope.StoreWrite))

        _blockstackSession = BlockstackSession(this@LoginActivity, config)
        signInButton.isEnabled = true

        continue_as_guest_button.setOnClickListener {
            progressBar.visibility = View.VISIBLE
            cardView.visibility = View.GONE
            startActivity(
                Intent(
                    applicationContext,
                    MainActivity::class.java
                ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            )
        }

        if (blockstackSession().isUserSignedIn()) {
            val intent = Intent(applicationContext, MainActivity::class.java)
            intent.putExtra("userData", blockstackSession().loadUserData()?.profile?.name)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        } else {
            signInButton.setOnClickListener {
                blockstackSession().redirectUserToSignIn { errorResult ->
                    if (errorResult.hasErrors) {
                        Toast.makeText(this, "error: " + errorResult.error, Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        }

        if (intent?.action == Intent.ACTION_VIEW) {
            handleAuthResponse(intent)
        }

    }

    private fun onSignIn(userData: UserData) {
        val intent = Intent(applicationContext, MainActivity::class.java)
        intent.putExtra("userData", userData.profile?.name)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        Log.d(TAG, "onNewIntent")

        if (intent?.action == Intent.ACTION_MAIN) {
            val userData = blockstackSession().loadUserData()
            if (userData != null) {
                runOnUiThread {
                    onSignIn(userData)
                }
            } else {
                Toast.makeText(this, "no user data", Toast.LENGTH_SHORT).show()
            }
        } else if (intent?.action == Intent.ACTION_VIEW) {
            handleAuthResponse(intent)
        }
    }

    private fun handleAuthResponse(intent: Intent) {
        cardView.visibility = View.GONE
        progressBar.visibility = View.VISIBLE
        authenticating_text.visibility = View.VISIBLE
        val response = intent.data.query
        signInButton.isEnabled = false
        continue_as_guest_button.isEnabled = false
        Log.d(TAG, "response ${response}")
        if (response != null) {
            val authResponseTokens = response.split('=')

            if (authResponseTokens.size > 1) {
                val authResponse = authResponseTokens[1]
                Log.d(TAG, "authResponse: ${authResponse}")
                blockstackSession().handlePendingSignIn(authResponse) { userDataResult: Result<UserData> ->
                    if (userDataResult.hasValue) {
                        val userData = userDataResult.value!!
                        Log.d(TAG, "signed in!")
                        runOnUiThread {
                            onSignIn(userData)
                        }
                    } else {
                        Toast.makeText(this, "error: " + userDataResult.error, Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        }
    }

    private fun blockstackSession(): BlockstackSession {
        val session = _blockstackSession
        if (session != null) {
            return session
        } else {
            throw IllegalStateException("No session.")
        }
    }

}