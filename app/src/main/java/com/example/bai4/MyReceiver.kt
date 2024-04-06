package com.example.bai4

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.example.bai4.util.TAG

class MyReceiver : BroadcastReceiver() {
    override fun onReceive(p0: Context?, p1: Intent?) {
        Log.d(TAG, "onReceive: ")
        val intentToService = Intent(p0, MyService::class.java)

        if (p1?.getStringExtra("action") == "turnOff") {
            p0?.stopService(intentToService)
            return
        }

        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                intentToService.putExtra("type", "foreground")
                p0?.startForegroundService(intentToService)
            } else {
                intentToService.putExtra("type", "background")
                p0?.startService(intentToService)
            }
        } else {
            // Todo handle case version SDK_INT >= S
        }
    }
}