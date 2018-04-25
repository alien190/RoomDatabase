package elegion.com.roomdatabase.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;
import android.database.Cursor;

import java.util.List;

/**
 * @author Azret Magometov
 */

@Dao
public interface MusicDao {


    //******************************************
    // album
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAlbums(List<Album> albums);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAlbum(Album albums);

    @Query("select * from album")
    List<Album> getAlbums();

    @Query("select * from album")
    Cursor getAlbumsCursor();

    @Query("select * from album where id = :albumId")
    Cursor getAlbumWithIdCursor(int albumId);

    //обновить информацию об альбоме
    @Update
    int updateAlbumInfo(Album album);

    //удалить альбом по id
    @Query("DELETE FROM album where id = :albumId")
    int deleteAlbumById(int albumId);

    //очистка album
    @Query("DELETE FROM album")
    int clearAlbum();

    //******************************************
    //song
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertSongs(List<Song> songs);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertSong(Song song);

    @Query("select * from song")
    List<Song> getSongs();

    @Query("select * from song")
    Cursor getSongsCursor();

    @Query("select * from song where id = :songId")
    Cursor getSongWithIdCursor(int songId);

    //обновить информацию о песни
    @Update
    int updateSongInfo(Song song);

    //удалить песню по id
    @Query("DELETE FROM song where id = :songId")
    int deleteSongById(int songId);

    //очистка song
    @Query("DELETE FROM song")
    int clearSong();



    //******************************************
    //AlbumSong
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void setLinksAlbumSongs(List<AlbumSong> linksAlbumSongs);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long setLinksAlbumSong(AlbumSong linksAlbumSong);

    @Query("select * from albumsong")
    List<AlbumSong> getAlbumSongs();

    @Query("select * from albumsong")
    Cursor getAlbumSongsCursor();

    @Query("select * from albumsong where id = :albumSongId")
    Cursor getAlbumSongWithIdCursor(int albumSongId);

    @Delete
    void deleteAlbum(Album album);

    //получить список песен переданного id альбома
    @Query("select * from song inner join albumsong on song.id = albumsong.song_id where album_id = :albumId")
    List<Song> getSongsFromAlbum(int albumId);

    //обновить информацию о песни в альбоме
    @Update
    int updateAlbumSongInfo(AlbumSong albumSong);

    //удалить песню в альбоме по id
    @Query("DELETE FROM albumsong where id = :albumSongId")
    int deleteAlbumSongById(int albumSongId);

    //очистка AlbumSong
    @Query("DELETE FROM albumsong")
    int clearAlbumSong();
}
