package com.hjplan.app.notification

import android.app.Service
import android.content.Intent
import android.os.IBinder

class ReminderService : Service() {
    override fun onBind(intent: Intent?): IBinder? = null
}
