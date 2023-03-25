package com.manga.m2ng2.tools

import android.app.Application

class DayConvert : Application() {
    override fun onCreate() {
        super.onCreate()
    }

    fun formatTimeStamp(timestamp: Long): String {
        val currentTimestamp = System.currentTimeMillis()
        val timeDiff = currentTimestamp - timestamp
        if (timeDiff < 0) {
            return "Vừa xong"
        }
        val day = timeDiff / 86400000
        val hour = (timeDiff % 86400000) / 3600000
        val minute = (timeDiff % 3600000) / 60000
        val second = (timeDiff % 60000) / 1000
        return when {
            day > 0 -> "$day ngày trước"
            hour > 0 -> "$hour giờ trước"
            minute > 0 -> "$minute phút trước"
            else -> "$second giây trước"
        }
    }
}