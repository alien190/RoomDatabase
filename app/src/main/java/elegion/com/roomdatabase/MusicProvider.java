package elegion.com.roomdatabase;

import android.arch.persistence.room.Room;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;

import elegion.com.roomdatabase.database.Album;
import elegion.com.roomdatabase.database.AlbumSong;
import elegion.com.roomdatabase.database.MusicDao;
import elegion.com.roomdatabase.database.MusicDatabase;
import elegion.com.roomdatabase.database.Song;

public class MusicProvider extends ContentProvider {

    private static final String TAG = MusicProvider.class.getSimpleName();

    private static final String AUTHORITY = "com.elegion.roomdatabase.musicprovider";
    private static final String ALBUM_TABLE = "album";
    private static final String SONG_TABLE = "song";
    private static final String ALBUMSONG_TABLE = "albumsong";

    private static final UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

    private static final int ALBUM_TABLE_CODE = 100;
    private static final int ALBUM_ROW_CODE = 101;

    private static final int SONG_TABLE_CODE = 102;
    private static final int SONG_ROW_CODE = 103;

    private static final int ALBUMSONG_TABLE_CODE = 104;
    private static final int ALBUMSONG_ROW_CODE = 105;

    static {
        URI_MATCHER.addURI(AUTHORITY, ALBUM_TABLE, ALBUM_TABLE_CODE);
        URI_MATCHER.addURI(AUTHORITY, ALBUM_TABLE + "/*", ALBUM_ROW_CODE);

        URI_MATCHER.addURI(AUTHORITY, SONG_TABLE, SONG_TABLE_CODE);
        URI_MATCHER.addURI(AUTHORITY, SONG_TABLE + "/*", SONG_ROW_CODE);

        URI_MATCHER.addURI(AUTHORITY, ALBUMSONG_TABLE, ALBUMSONG_TABLE_CODE);
        URI_MATCHER.addURI(AUTHORITY, ALBUMSONG_TABLE + "/*", ALBUMSONG_ROW_CODE);
    }

    private MusicDao mMusicDao;

    public MusicProvider() {
    }

    @Override
    public boolean onCreate() {
        if (getContext() != null) {
            mMusicDao = Room.databaseBuilder(getContext().getApplicationContext(), MusicDatabase.class, "music_database_01")
                    .build()
                    .getMusicDao();
            return true;
        }

        return false;
    }

    @Override
    public String getType(Uri uri) {
        switch (URI_MATCHER.match(uri)) {
            case ALBUM_TABLE_CODE:
                return "vnd.android.cursor.dir/" + AUTHORITY + "." + ALBUM_TABLE;
            case ALBUM_ROW_CODE:
                return "vnd.android.cursor.item/" + AUTHORITY + "." + ALBUM_TABLE;

            case SONG_TABLE_CODE:
                return "vnd.android.cursor.dir/" + AUTHORITY + "." + SONG_TABLE;
            case SONG_ROW_CODE:
                return "vnd.android.cursor.item/" + AUTHORITY + "." + SONG_TABLE;

            case ALBUMSONG_TABLE_CODE:
                return "vnd.android.cursor.dir/" + AUTHORITY + "." + ALBUMSONG_TABLE;
            case ALBUMSONG_ROW_CODE:
                return "vnd.android.cursor.item/" + AUTHORITY + "." + ALBUMSONG_TABLE;

            default:
                throw new UnsupportedOperationException("not yet implemented");
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        int code = URI_MATCHER.match(uri);

        switch (code) {
            case ALBUM_TABLE_CODE:
                return mMusicDao.getAlbumsCursor();

            case ALBUM_ROW_CODE:
                return mMusicDao.getAlbumWithIdCursor((int) ContentUris.parseId(uri));

            case SONG_TABLE_CODE:
                return mMusicDao.getSongsCursor();

            case SONG_ROW_CODE:
                return mMusicDao.getSongWithIdCursor((int) ContentUris.parseId(uri));

            case ALBUMSONG_TABLE_CODE:
                return mMusicDao.getAlbumSongsCursor();

            case ALBUMSONG_ROW_CODE:
                return mMusicDao.getAlbumSongWithIdCursor((int) ContentUris.parseId(uri));

            default: return null;
        }

    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {

        int code = URI_MATCHER.match(uri);

        switch (code) {
            case ALBUM_TABLE_CODE: {
                if(isAlbumValuesValid(values)) {
                    Album album = new Album();
                    Integer id = values.getAsInteger("id");
                    album.setId(id);
                    album.setName(values.getAsString("name"));
                    album.setReleaseDate(values.getAsString("release"));
                    mMusicDao.insertAlbum(album);
                    return ContentUris.withAppendedId(uri, id);

                } else {
                    throw new IllegalArgumentException("cant add item");
                }
            }

            case SONG_TABLE_CODE: {
                if(isSongValuesValid(values)) {
                    Song song = new Song();
                    Integer id = values.getAsInteger("id");
                    song.setId(id);
                    song.setName(values.getAsString("name"));
                    song.setDuration(values.getAsString("duration"));
                    mMusicDao.insertSong(song);
                    return ContentUris.withAppendedId(uri, id);

                } else {
                    throw new IllegalArgumentException("cant add item");
                }
            }

            case ALBUMSONG_TABLE_CODE: {
                if(isAlbumSongValuesValid(values)) {
                    AlbumSong albumSong = new AlbumSong();
                    albumSong.setAlbumId(values.getAsInteger("album_id"));
                    albumSong.setSongId(values.getAsInteger("song_id"));
                    long id = mMusicDao.setLinksAlbumSong(albumSong);
                    return ContentUris.withAppendedId(uri, id);

                } else {
                    throw new IllegalArgumentException("cant add item");
                }
            }

            default: throw new IllegalArgumentException("cant add item");
        }

    }

    private boolean isAlbumSongValuesValid(ContentValues values) {
        return values.containsKey("album_id") && values.containsKey("song_id");
    }

    private boolean isSongValuesValid(ContentValues values) {
        return values.containsKey("id") && values.containsKey("name") && values.containsKey("duration");
    }

    private boolean isAlbumValuesValid(ContentValues values) {
        return values.containsKey("id") && values.containsKey("name") && values.containsKey("release");
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        int code = URI_MATCHER.match(uri);

        switch (code) {
            case ALBUM_ROW_CODE: {
                if(isAlbumValuesValid(values)) {
                    Album album = new Album();
                    Integer id = (int) ContentUris.parseId(uri);
                    album.setId(id);
                    album.setName(values.getAsString("name"));
                    album.setReleaseDate(values.getAsString("release"));
                    return mMusicDao.updateAlbumInfo(album);

                } else {
                    throw new IllegalArgumentException("cant update item");
                }
            }

            case SONG_ROW_CODE: {
                if(isSongValuesValid(values)) {
                    Song song = new Song();
                    Integer id = (int) ContentUris.parseId(uri);
                    song.setId(id);
                    song.setName(values.getAsString("name"));
                    song.setDuration(values.getAsString("duration"));
                    return mMusicDao.updateSongInfo(song);

                } else {
                    throw new IllegalArgumentException("cant update item");
                }
            }

            case ALBUMSONG_ROW_CODE: {
                if(isAlbumSongValuesValid(values)) {
                    AlbumSong albumSong = new AlbumSong();
                    Integer id = (int) ContentUris.parseId(uri);
                    albumSong.setId(id);
                    albumSong.setAlbumId(values.getAsInteger("album_id"));
                    albumSong.setSongId(values.getAsInteger("song_id"));
                    return  mMusicDao.updateAlbumSongInfo(albumSong);

                } else {
                    throw new IllegalArgumentException("cant add item");
                }
            }

            default: throw new IllegalArgumentException("cant add item");
        }
    }


    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int code = URI_MATCHER.match(uri);

        switch (code) {
            case ALBUM_ROW_CODE: {
                int id = (int) ContentUris.parseId(uri);
                return mMusicDao.deleteAlbumById(id);
            }

            case SONG_ROW_CODE: {
                int id = (int) ContentUris.parseId(uri);
                return mMusicDao.deleteSongById(id);
            }

            case ALBUMSONG_ROW_CODE: {
                int id = (int) ContentUris.parseId(uri);
                return mMusicDao.deleteAlbumSongById(id);
            }

            default: throw new IllegalArgumentException("cant delete item");
        }

    }
}
