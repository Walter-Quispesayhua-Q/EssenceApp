package com.essence.essenceapp.shared.cache

import android.util.Log
import com.essence.essenceapp.feature.song.domain.model.SongSimple
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QueueCache @Inject constructor() {

    // @Volatile garantiza visibilidad cross-thread sin necesidad de
    // sincronizacion en las lecturas. Las listas son inmutables, asi
    // que las referencias se publican atomicamente.
    @Volatile
    private var items: List<SongSimple> = emptyList()

    @Synchronized
    fun set(source: String, newItems: List<SongSimple>) {
        Log.d(TAG, "Set: $source (${newItems.size} items)")
        items = newItems
    }

    fun findItem(songLookup: String): SongSimple? {
        return items.find { it.hlsMasterKey == songLookup }
    }

    companion object {
        private const val TAG = "QueueCache"
    }
}