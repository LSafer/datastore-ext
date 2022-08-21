package net.lsafer.datastore

import androidx.compose.runtime.*
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Composable
fun <T> DataStore<Preferences>.observePreference(
    key: Preferences.Key<T>,
): MutableState<T?> {
    val dataStore = this
    val preferences: Preferences? by dataStore.data.collectAsState(initial = null)

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

@Composable
fun <T> DataStore<Preferences>.observePreference(
    key: Preferences.Key<T>,
    default: () -> T
): MutableState<T> {
    var preference by observePreference(key)
    val preferenceOrDefault = remember(preference) {
        preference ?: default()
    }

    return object : MutableState<T> {
        override var value: T
            get() = preferenceOrDefault
            set(value) = run { preference = value }

        override fun component1(): T = value
        override fun component2(): (T) -> Unit = { value = it }
    }
}

@Composable
fun DataStore<Preferences>.observeIntPreference(
    name: String
) = observePreference(intPreferencesKey(name))

@Composable
fun DataStore<Preferences>.observeIntPreference(
    name: String,
    default: Int
) = observePreference(intPreferencesKey(name)) { default }

@Composable
fun DataStore<Preferences>.observeDoublePreference(
    name: String
) = observePreference(doublePreferencesKey(name))

@Composable
fun DataStore<Preferences>.observeDoublePreference(
    name: String,
    default: Double
) = observePreference(doublePreferencesKey(name)) { default }

@Composable
fun DataStore<Preferences>.observeStringPreference(
    name: String
) = observePreference(stringPreferencesKey(name))

@Composable
fun DataStore<Preferences>.observeStringPreference(
    name: String,
    default: String
) = observePreference(stringPreferencesKey(name)) { default }

@Composable
fun DataStore<Preferences>.observeBooleanPreference(
    name: String
) = observePreference(booleanPreferencesKey(name))

@Composable
fun DataStore<Preferences>.observeBooleanPreference(
    name: String,
    default: Boolean
) = observePreference(booleanPreferencesKey(name)) { default }

@Composable
fun DataStore<Preferences>.observeFloatPreference(
    name: String
) = observePreference(floatPreferencesKey(name))

@Composable
fun DataStore<Preferences>.observeFloatPreference(
    name: String,
    default: Float
) = observePreference(floatPreferencesKey(name)) { default }

@Composable
fun DataStore<Preferences>.observeLongPreference(
    name: String
) = observePreference(longPreferencesKey(name))

@Composable
fun DataStore<Preferences>.observeLongPreference(
    name: String,
    default: Long
) = observePreference(longPreferencesKey(name)) { default }

@Composable
fun DataStore<Preferences>.observeStringSetPreference(
    name: String
) = observePreference(stringSetPreferencesKey(name))

@Composable
fun DataStore<Preferences>.observeStringSetPreference(
    name: String,
    default: Set<String>
) = observePreference(stringSetPreferencesKey(name)) { default }

@Composable
inline fun <reified T> DataStore<Preferences>.observeJsonPreference(
    name: String,
    json: Json = Json
): MutableState<T?> {
    val key = stringPreferencesKey(name)
    var preference by observePreference(key)
    val preferenceAsJson: T? = remember(preference) {
        preference?.let { json.decodeFromString<T>(it) }
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

@Composable
inline fun <reified T> DataStore<Preferences>.observeJsonPreference(
    name: String,
    json: Json = Json,
    crossinline default: @DisallowComposableCalls () -> T
): MutableState<T> {
    var preference by observeJsonPreference<T>(name, json)
    val preferenceOrDefault = remember(preference) {
        preference ?: default()
    }

    return object : MutableState<T> {
        override var value: T
            get() = preferenceOrDefault
            set(value) = run { preference = value }

        override fun component1(): T = value
        override fun component2(): (T) -> Unit = { value = it }
    }
}
