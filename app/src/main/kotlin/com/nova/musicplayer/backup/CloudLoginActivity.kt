// CloudLoginActivity.kt

package com.nova.musicplayer.backup

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.nova.musicplayer.MainActivity
import com.nova.musicplayer.di.CloudBackupEntryPoint
import dagger.hilt.android.EntryPointAccessors

/** where dropbox's and onedrive's login pages send the browser back to; it passes the answer on and brings mochy to the front again */
class CloudLoginActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handle(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handle(intent)
    }

    private fun handle(intent: Intent?) {
        intent?.data?.let { redirect ->
            EntryPointAccessors.fromApplication(applicationContext, CloudBackupEntryPoint::class.java)
                .cloudSignIn()
                .handleRedirect(redirect)
        }
        startActivity(
            Intent(this, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP),
        )
        finish()
    }
}
