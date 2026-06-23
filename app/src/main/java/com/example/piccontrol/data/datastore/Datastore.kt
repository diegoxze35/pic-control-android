package com.example.piccontrol.data.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
private const val ATTEMPTS_KEY = "ATTEMPTS_KEY"
private const val DELAY_KEY = "DELAY_KEY"

val RECONNECTION_ATTEMPTS = intPreferencesKey(ATTEMPTS_KEY)
val DELAY_ATTEMPTS = intPreferencesKey(DELAY_KEY)

