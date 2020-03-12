package com.perrigogames.life4trials.repo

import com.perrigogames.life4trials.db.SongDB
import com.perrigogames.life4trials.db.SongDB_
import io.objectbox.BoxStore
import org.koin.core.KoinComponent
import org.koin.core.inject

class SongRepo: KoinComponent {

    private val objectBox: BoxStore by inject()
    private val songBox get() = objectBox.boxFor(SongDB::class.java)

    //
    // Management Functions
    //
    fun put(song: SongDB) = songBox.put(song)
    fun put(songs: Collection<SongDB>) = songBox.put(songs)

    fun attach(song: SongDB) = songBox.attach(song)

    fun clear() = songBox.removeAll()

    //
    // Queries
    //
    private val songTitleQuery = songBox.query()
        .equal(SongDB_.title, "").parameterAlias("title")
        .build()
    private val gameVersionQuery = songBox.query()
        .equal(SongDB_.version, -1).parameterAlias("version")
        .build()
    private val multipleGameVersionQuery = songBox.query()
        .`in`(SongDB_.version, LongArray(0)).parameterAlias("versions")
        .build()

    //
    // Access Functions
    //
    fun getSongs(): List<SongDB> = songBox.all

    fun getSongById(id: Long): SongDB? = songBox.get(id)

    fun getSongsById(ids: LongArray): MutableList<SongDB> = songBox.get(ids)

    fun getSongByName(name: String): SongDB? =
        songTitleQuery.setParameter("title", name).findFirst()

    fun findSongByTitle(title: String) = songTitleQuery.setParameter("title", title).findFirst()

    fun findBlockedSongs(titles: Array<String>, version: Long, previewVersion: Long) = songBox.query()
        .greater(SongDB_.version, previewVersion) // block everything higher than preview version
        .or().greater(SongDB_.version, version).and().equal(SongDB_.preview, false) // block non-preview songs in preview versions
        .or().`in`(SongDB_.title, titles) // block songs in the supplied list
        .build()
        .find()
}
