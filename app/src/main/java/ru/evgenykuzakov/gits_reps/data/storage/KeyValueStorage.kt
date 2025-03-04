package ru.evgenykuzakov.gits_reps.data.storage

import android.content.Context
import android.content.SharedPreferences
import javax.inject.Inject
import javax.inject.Singleton

private const val APP_PREFS = "app_prefs"

@Singleton
class KeyValueStorage @Inject constructor(
    context: Context
) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(APP_PREFS, Context.MODE_PRIVATE)
    var authToken: String?
        get() = sharedPreferences.getString(APP_PREFS, null)
        set(value) {
            sharedPreferences.edit().putString(APP_PREFS, value).apply()
        }
}