package com.example.welltrack.workers

import android.content.Context
import androidx.work.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

object HabitReminderScheduler {

    fun schedule(habitId: String, habitName: String, reminderTime: String, context: Context) {
        // First, cancel any existing work for this habit
        cancel(habitId, context)

        val delay = calculateInitialDelay(reminderTime)

        val data = workDataOf(
            HabitReminderWorker.KEY_HABIT_ID to habitId,
            HabitReminderWorker.KEY_HABIT_NAME to habitName
        )

        val reminderRequest =
            OneTimeWorkRequestBuilder<HabitReminderWorker>()
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .setInputData(data)
                .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            "habit_reminder_$habitId",
            ExistingWorkPolicy.REPLACE,
            reminderRequest
        )
    }

    fun cancel(habitId: String, context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork("habit_reminder_$habitId")
    }

    // This is called by the worker to schedule the next day's reminder
    fun scheduleNext(habitId: String, habitName: String, context: Context) {
        val data = workDataOf(
            HabitReminderWorker.KEY_HABIT_ID to habitId,
            HabitReminderWorker.KEY_HABIT_NAME to habitName
        )

        val nextRequest =
            OneTimeWorkRequestBuilder<HabitReminderWorker>()
                .setInitialDelay(24, TimeUnit.HOURS) // Schedule for the next day
                .setInputData(data)
                .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            "habit_reminder_$habitId",
            ExistingWorkPolicy.REPLACE,
            nextRequest
        )
    }

    private fun calculateInitialDelay(reminderTime: String): Long {
        val now = Calendar.getInstance()
        val timeParts = reminderTime.split(":")
        val targetTime = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, timeParts[0].toInt())
            set(Calendar.MINUTE, timeParts[1].toInt())
            set(Calendar.SECOND, 0)
        }

        // If the target time has already passed today, schedule it for tomorrow
        if (targetTime.before(now)) {
            targetTime.add(Calendar.DAY_OF_MONTH, 1)
        }

        return targetTime.timeInMillis - now.timeInMillis
    }
}