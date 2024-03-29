package com.joshuahalvorson.datenight.view.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.joshuahalvorson.datenight.R
import com.joshuahalvorson.datenight.view.LoginActivity
import com.joshuahalvorson.datenight.view.MainActivity
import kotlinx.android.synthetic.main.fragment_settings.*
import org.blockstack.android.sdk.BlockstackSession
import org.blockstack.android.sdk.model.toBlockstackConfig

class SettingsFragment : Fragment() {
    private var _blockstackSession: BlockstackSession? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val config = "https://joshhalvorson.github.io/blockstack-android-web-app/public/"
            .toBlockstackConfig(arrayOf(org.blockstack.android.sdk.Scope.StoreWrite))

        _blockstackSession = BlockstackSession(context, config)

        if (blockstackSession().isUserSignedIn()) {
            log_in_button.visibility = View.GONE
            account_name.visibility = View.VISIBLE
            account_text.visibility = View.VISIBLE
            log_out_button.visibility = View.VISIBLE
            account_name.text = blockstackSession().loadUserData()?.profile?.name
            log_out_button.setOnClickListener {
                val alertDialog: AlertDialog? = activity?.let {
                    val builder = AlertDialog.Builder(it)
                    builder.setMessage("Are you sure you want to sign out?")
                    builder.apply {
                        setPositiveButton(
                            "Yes"
                        ) { dialog, id ->
                            blockstackSession().signUserOut()
                            startActivity(Intent(context, MainActivity::class.java))
                        }
                        setNegativeButton(
                            "No"
                        ) { dialog, id ->
                            // User cancelled the dialog
                        }
                    }
                    builder.create()
                }
                alertDialog?.show()
            }
        } else {
            log_in_button.visibility = View.VISIBLE
            account_name.visibility = View.GONE
            account_text.visibility = View.GONE
            log_out_button.visibility = View.GONE

            log_in_button.setOnClickListener {
                val intent = Intent(context, LoginActivity::class.java)
                intent.putExtra("from_settings", true)
                startActivity(intent)
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
