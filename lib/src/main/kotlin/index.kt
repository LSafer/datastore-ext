package net.lsafer.datastore

import android.content.Context
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Composable
fun <T> rememberPreference(
    key: Preferences.Key<T>,
    store: (Context) -> DataStore<Preferences>,
): MutableState<T?> {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val dataStore = store(context)
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
fun <T> rememberPreference(
    key: Preferences.Key<T>,
    store: (Context) -> DataStore<Preferences>,
    default: () -> T
): MutableState<T> {
    var preference by rememberPreference(key, store)
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
fun rememberIntPreference(
    name: String,
    store: (Context) -> DataStore<Preferences>
) = rememberPreference(intPreferencesKey(name), store)

@Composable
fun rememberIntPreference(
    name: String,
    store: (Context) -> DataStore<Preferences>,
    default: Int
) = rememberPreference(intPreferencesKey(name), store) { default }

@Composable
fun rememberDoublePreference(
    name: String,
    store: (Context) -> DataStore<Preferences>
) = rememberPreference(doublePreferencesKey(name), store)

@Composable
fun rememberDoublePreference(
    name: String,
    store: (Context) -> DataStore<Preferences>,
    default: Double
) = rememberPreference(doublePreferencesKey(name), store) { default }

@Composable
fun rememberStringPreference(
    name: String,
    store: (Context) -> DataStore<Preferences>
) = rememberPreference(stringPreferencesKey(name), store)

@Composable
fun rememberStringPreference(
    name: String,
    store: (Context) -> DataStore<Preferences>,
    default: String
) = rememberPreference(stringPreferencesKey(name), store) { default }

@Composable
fun rememberBooleanPreference(
    name: String,
    store: (Context) -> DataStore<Preferences>
) = rememberPreference(booleanPreferencesKey(name), store)

@Composable
fun rememberBooleanPreference(
    name: String,
    store: (Context) -> DataStore<Preferences>,
    default: Boolean
) = rememberPreference(booleanPreferencesKey(name), store) { default }

@Composable
fun rememberFloatPreference(
    name: String,
    store: (Context) -> DataStore<Preferences>
) = rememberPreference(floatPreferencesKey(name), store)

@Composable
fun rememberFloatPreference(
    name: String,
    store: (Context) -> DataStore<Preferences>,
    default: Float
) = rememberPreference(floatPreferencesKey(name), store) { default }

@Composable
fun rememberLongPreference(
    name: String,
    store: (Context) -> DataStore<Preferences>
) = rememberPreference(longPreferencesKey(name), store)

@Composable
fun rememberLongPreference(
    name: String,
    store: (Context) -> DataStore<Preferences>,
    default: Long
) = rememberPreference(longPreferencesKey(name), store) { default }

@Composable
fun rememberStringSetPreference(
    name: String,
    store: (Context) -> DataStore<Preferences>
) = rememberPreference(longPreferencesKey(name), store)

@Composable
fun rememberStringSetPreference(
    name: String,
    store: (Context) -> DataStore<Preferences>,
    default: Set<String>
) = rememberPreference(stringSetPreferencesKey(name), store) { default }

@Composable
inline fun <reified T> rememberJsonPreference(
    name: String,
    noinline store: (Context) -> DataStore<Preferences>,
    json: Json = Json
): MutableState<T?> {
    val key = stringPreferencesKey(name)
    var preference by rememberPreference(key, store)
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
inline fun <reified T> rememberJsonPreference(
    name: String,
    noinline store: (Context) -> DataStore<Preferences>,
    json: Json = Json,
    crossinline default: @DisallowComposableCalls () -> T
): MutableState<T> {
    var preference by rememberJsonPreference<T>(name, store, json)
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
