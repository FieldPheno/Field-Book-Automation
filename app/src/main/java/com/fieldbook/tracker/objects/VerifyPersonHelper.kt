package com.fieldbook.tracker.objects

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AlertDialog
import com.fieldbook.tracker.R
import com.fieldbook.tracker.activities.PreferencesActivity
import com.fieldbook.tracker.preferences.GeneralKeys
import dagger.hilt.android.qualifiers.ActivityContext
import javax.inject.Inject

class VerifyPersonHelper @Inject constructor(@ActivityContext private val context: Context) {

    val prefs = context.getSharedPreferences(GeneralKeys.SHARED_PREF_FILE_NAME, Context.MODE_PRIVATE)

    private fun Int.hourToNano() = this * 3600 * 1e9.toLong()

    /**
     * Simple function that checks if the collect activity was opened >24hrs ago.
     * If the condition is met, it asks the user to reenter the collector id.
     */
    fun checkLastOpened() {

        val lastOpen: Long = prefs.getLong(GeneralKeys.LAST_TIME_OPENED, 0L)
        val systemTime = System.nanoTime()

        //number of hours to wait before asking for user, pref found in profile
        val interval = when (prefs.getString(GeneralKeys.REQUIRE_USER_INTERVAL, "0")) {
            "1" -> 0
            "2" -> 12
            else -> 24
        }

        val nanosToWait = 1e9.toLong() * 3600 * interval
        if (lastOpen != 0L && systemTime - lastOpen > nanosToWait) {
            val verify: Boolean = prefs.getBoolean(GeneralKeys.REQUIRE_USER_TO_COLLECT, true)
            if (verify) {
                val firstName: String = prefs.getString(GeneralKeys.FIRST_NAME, "") ?: ""
                val lastName: String = prefs.getString(GeneralKeys.LAST_NAME, "") ?: ""
                if (firstName.isNotEmpty() || lastName.isNotEmpty()) {
                    //person presumably has been set
                    showAskCollectorDialog(
                        context.getString(R.string.activity_collect_dialog_verify_collector) + " " + firstName + " " + lastName + "?",
                        context.getString(R.string.activity_collect_dialog_verify_yes_button),
                        context.getString(R.string.activity_collect_dialog_neutral_button),
                        context.getString(R.string.activity_collect_dialog_verify_no_button)
                    )
                } else {
                    //person presumably hasn't been set
                    showAskCollectorDialog(
                        context.getString(R.string.activity_collect_dialog_new_collector),
                        context.getString(R.string.activity_collect_dialog_verify_no_button),
                        context.getString(R.string.activity_collect_dialog_neutral_button),
                        context.getString(R.string.activity_collect_dialog_verify_yes_button)
                    )
                }
            }
        }
        updateLastOpenedTime()
    }

    private fun showAskCollectorDialog(
        message: String,
        positive: String,
        neutral: String,
        negative: String
    ) {
        AlertDialog.Builder(context, R.style.AppAlertDialog)
            .setTitle(message) //yes button
            .setPositiveButton(
                positive
            ) { dialog: DialogInterface, _: Int -> dialog.dismiss() } //yes, don't ask again button
            .setNeutralButton(neutral) { dialog: DialogInterface, _: Int ->
                dialog.dismiss()
                prefs.edit().putBoolean(GeneralKeys.REQUIRE_USER_TO_COLLECT, false).apply()
            } //no (navigates to the person preference)
            .setNegativeButton(negative) { dialog: DialogInterface, _: Int ->
                dialog.dismiss()
                val preferenceIntent = Intent()
                preferenceIntent.setClassName(
                    context,
                    PreferencesActivity::class.java.name
                )
                preferenceIntent.putExtra("PersonUpdate", true)
                context.startActivity(preferenceIntent)
            }
            .show()
    }

    fun updateLastOpenedTime() {
        prefs.edit().putLong(GeneralKeys.LAST_TIME_OPENED, System.nanoTime()).apply()
    }
}