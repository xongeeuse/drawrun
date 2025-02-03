package com.example.drawrun.presentation

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat.startActivity
import com.google.android.gms.wearable.Wearable

class LaunchAppReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val messageClient = Wearable.getMessageClient(context!!)
        messageClient.addListener { messageEvent ->
            if (messageEvent.path == "/launch_app") {
                val launchIntent = Intent(context, DrawRunMainActivity::class.java)
                launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(context, launchIntent, null)
            }
        }
    }
}
