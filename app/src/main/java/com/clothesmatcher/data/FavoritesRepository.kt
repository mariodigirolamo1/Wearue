package com.clothesmatcher.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "favorites")

data class FavoriteStyle(
    val id: String, // format: "paletteId|cat1,cat2,cat3"
    val paletteId: String,
    val categories: List<String>
)

class FavoritesRepository(private val context: Context) {
    private val FAVORITES_KEY = stringSetPreferencesKey("favorite_styles")

    val favorites: Flow<List<FavoriteStyle>> = context.dataStore.data.map { preferences ->
        preferences[FAVORITES_KEY]?.map { serialized ->
            val parts = serialized.split("|")
            val paletteId = parts[0]
            val categories = parts[1].split(",")
            FavoriteStyle(serialized, paletteId, categories)
        } ?: emptyList()
    }

    suspend fun toggleFavorite(paletteId: String, categories: List<String>) {
        val serialized = "${paletteId}|${categories.sorted().joinToString(",")}"
        context.dataStore.edit { preferences ->
            val current = preferences[FAVORITES_KEY] ?: emptySet()
            if (current.contains(serialized)) {
                preferences[FAVORITES_KEY] = current - serialized
            } else {
                preferences[FAVORITES_KEY] = current + serialized
            }
        }
    }

    fun isFavorite(paletteId: String, categories: List<String>): Flow<Boolean> {
        val serialized = "${paletteId}|${categories.sorted().joinToString(",")}"
        return context.dataStore.data.map { preferences ->
            preferences[FAVORITES_KEY]?.contains(serialized) ?: false
        }
    }
}
