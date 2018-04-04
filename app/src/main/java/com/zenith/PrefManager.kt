package com.zenith

import android.R.id.edit
import android.content.Context
import android.content.SharedPreferences



/**
 * Created by hanumaan on 4/4/18.
 */
class PrefManager(context: Context) {
    private val PREF_NAME = "ZENITH_WELCOME"
    private val IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch"

    private var pref: SharedPreferences = context.getSharedPreferences(PREF_NAME,Context.MODE_PRIVATE)
    private var editor: SharedPreferences.Editor = pref.edit()


    fun setFirstTimeLaunch(isFirstTime: Boolean) {
        editor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime)
        editor.commit()
    }

    fun isFirstTimeLaunch(): Boolean {
        return pref.getBoolean(IS_FIRST_TIME_LAUNCH, true)
    }
}