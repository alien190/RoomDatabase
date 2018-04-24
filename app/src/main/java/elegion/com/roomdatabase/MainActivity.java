package elegion.com.roomdatabase;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import elegion.com.roomdatabase.database.Album;
import elegion.com.roomdatabase.database.AlbumSong;
import elegion.com.roomdatabase.database.MusicDao;
import elegion.com.roomdatabase.database.Song;

public class MainActivity extends AppCompatActivity {
    private Button mAddBtn;
    private Button mGetBtn;

    // добавить базу данных Room ----
    // вставить данные / извлечь данные ---
    // добавить контент провайдер над Room ---

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final MusicDao musicDao = ((AppDelegate) getApplicationContext()).getMusicDatabase().getMusicDao();

        mAddBtn = (findViewById(R.id.add));
        mAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                musicDao.insertAlbums(createAlbums());
                musicDao.insertSongs(createSongs());
                musicDao.setLinksAlbumSongs(createAlbumSongs());
            }
        });

        mGetBtn = findViewById(R.id.get);
        mGetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringBuilder builder = new StringBuilder();
                builder.append(listToString(musicDao.getAlbums()))
                        .append(listToString(musicDao.getSongs()))
                        .append(listToString(musicDao.getAlbumSong()));

                Toast.makeText(MainActivity.this, builder.toString(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private List<Album> createAlbums() {
        List<Album> albums = new ArrayList<>(3);
        for (int i = 0; i < 3; i++) {
            albums.add(new Album(i, "album " + i, "release" + System.currentTimeMillis()));
        }

        return albums;
    }

    private List<Song> createSongs() {
        List<Song> songs = new ArrayList<>(3);
        for (int i =0; i < 3 ; i++) {
            songs.add(new Song(i, "song " + 1, "release" + System.currentTimeMillis()));
        }

        return songs;
    }

    private List<AlbumSong> createAlbumSongs() {
        List<AlbumSong> albumSongs = new ArrayList<>(3);
        for (int i =0; i < 3 ; i++) {
            albumSongs.add(new AlbumSong(i, i, i));
        }
        return albumSongs;
    }

    private String listToString(List list) {
        StringBuilder builder = new StringBuilder();
        builder.append("Table: ")
                .append(list.getClass().getSimpleName())
                .append("\n");
        for (int i = 0, size = list.size(); i < size; i++) {
            builder.append(list.get(i).toString()).append("\n");
        }
        return builder.toString();
    }
}
