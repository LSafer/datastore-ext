package net.lsafer.datastore

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

fun <T> DataStore<Preferences>.preference(
    key: Preferences.Key<T>
): MutableState<T?> {
    val dataStore = this
    val preferences = runBlocking { dataStore.data.firstOrNull() }

    return object : MutableState<T?> {
        override var value: T?
            get() = preferences?.get(key)
            set(value) = runBlocking {
                dataStore.edit {
                    when (value) {
                        null -> it.remove(key)
                        else -> it[key] = value
                    }
                }
            }

        override fun component1(): T? = value
        override fun component2(): (T?) -> Unit = { value = it }
    }
}

fun <T> DataStore<Preferences>.preference(
    key: Preferences.Key<T>,
    default: () -> T
): MutableState<T> {
    var preference by preference(key)
    val preferenceOrDefault = preference ?: default()

    return object : MutableState<T> {
        override var value: T
            get() = preferenceOrDefault
            set(value) = run { preference = value }

        override fun component1(): T = value
        override fun component2(): (T) -> Unit = { value = it }
    }
}

fun <T> DataStore<Preferences>.intPreference(
    name: String
) = preference(intPreferencesKey(name))

fun <T> DataStore<Preferences>.intPreference(
    name: String,
    default: Int
) = preference(intPreferencesKey(name)) { default }

fun <T> DataStore<Preferences>.doublePreference(
    name: String
) = preference(doublePreferencesKey(name))

fun <T> DataStore<Preferences>.doublePreference(
    name: String,
    default: Double
) = preference(doublePreferencesKey(name)) { default }

fun <T> DataStore<Preferences>.stringPreference(
    name: String
) = preference(stringPreferencesKey(name))

fun <T> DataStore<Preferences>.stringPreference(
    name: String,
    default: String
) = preference(stringPreferencesKey(name)) { default }

fun <T> DataStore<Preferences>.booleanPreference(
    name: String
) = preference(booleanPreferencesKey(name))

fun <T> DataStore<Preferences>.booleanPreference(
    name: String,
    default: Boolean
) = preference(booleanPreferencesKey(name)) { default }

fun <T> DataStore<Preferences>.floatPreference(
    name: String
) = preference(floatPreferencesKey(name))

fun <T> DataStore<Preferences>.floatPreference(
    name: String,
    default: Float
) = preference(floatPreferencesKey(name)) { default }

fun <T> DataStore<Preferences>.longPreference(
    name: String
) = preference(longPreferencesKey(name))

fun <T> DataStore<Preferences>.longPreference(
    name: String,
    default: Long
) = preference(longPreferencesKey(name)) { default }

fun <T> DataStore<Preferences>.stringSetPreference(
    name: String
) = preference(stringSetPreferencesKey(name))

fun <T> DataStore<Preferences>.stringSetPreference(
    name: String,
    default: Set<String>
) = preference(stringSetPreferencesKey(name)) { default }

inline fun <reified T> DataStore<Preferences>.jsonPreference(
    name: String,
    json: Json = Json
): MutableState<T?> {
    val key = stringPreferencesKey(name)
    var preference by preference(key)
    val preferenceAsJson: T? = preference?.let {
        json.decodeFromString<T>(it)
    }

    return object : MutableState<T?> {
        override var value: T?
            get() = preferenceAsJson
            set(value) = run {
                preference = when (value) {
                    null -> null
                    else -> json.encodeToString(value)
                }
            }

        override fun component1(): T? = value
        override fun component2(): (T?) -> Unit = { value = it }
    }
}

inline fun <reified T> DataStore<Preferences>.jsonPreference(
    name: String,
    json: Json = Json,
    crossinline default: () -> T
): MutableState<T> {
    var preference by jsonPreference<T>(name, json)
    val preferenceOrDefault = preference ?: default()

    return object : MutableState<T> {
        override var value: T
            get() = preferenceOrDefault
            set(value) = run { preference = value }

        override fun component1(): T = value
        override fun component2(): (T) -> Unit = { value = it }
    }
}
