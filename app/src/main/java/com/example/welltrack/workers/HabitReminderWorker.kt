package com.example.welltrack.workers

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.welltrack.R

class HabitReminderWorker(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val habitName = inputData.getString(KEY_HABIT_NAME) ?: return Result.failure()
        val habitId = inputData.getString(KEY_HABIT_ID) ?: return Result.failure()

        showNotification(habitName, habitId)

        // After showing the notification, schedule the next one for the next day
        HabitReminderScheduler.scheduleNext(habitId, habitName, applicationContext)

        return Result.success()
    }

    private fun showNotification(habitName: String, habitId: String) {
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notification = NotificationCompat.Builder(applicationContext, "habit_reminders")
            .setSmallIcon(R.drawable.ic_leaf)
            .setContentTitle("Habit Reminder")
            .setContentText("Time to work on : $habitName")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        // Use a unique ID for each notification to avoid overwriting them
        notificationManager.notify(habitId.hashCode(), notification)
    }

    companion object {
        const val KEY_HABIT_NAME = "KEY_HABIT_NAME"
        const val KEY_HABIT_ID = "KEY_HABIT_ID"
    }
}