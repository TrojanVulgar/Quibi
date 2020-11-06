package com.easyplex.data.local;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import com.easyplex.data.local.converters.CastConverter;
import com.easyplex.data.local.converters.GenreConverter;
import com.easyplex.data.local.converters.MediaStreamConverter;
import com.easyplex.data.local.converters.MediaSubstitlesConverter;
import com.easyplex.data.local.converters.SaisonConverter;
import com.easyplex.data.local.converters.VideosConverter;
import com.easyplex.data.local.dao.FavoriteDao;
import com.easyplex.data.local.dao.DownloadDao;
import com.easyplex.data.local.dao.HistoryDao;
import com.easyplex.data.local.dao.StreamListDao;
import com.easyplex.data.local.entity.History;
import com.easyplex.data.local.entity.Media;
import com.easyplex.data.local.entity.Download;
import com.easyplex.data.local.entity.Stream;


/**
 * The Room database that contains the Favorite Movies & Series & Animes table
 * Define an abstract class that extends RoomDatabase.
 * This class is annotated with @Database, lists the entities contained in the database,
 * and the DAOs which access them.
 */
@Database(entities = {Media.class, Download.class, History.class, Stream.class}, version = 16, exportSchema = false)
@TypeConverters({GenreConverter.class,
        CastConverter.class,
        VideosConverter.class,
        SaisonConverter.class,
        MediaSubstitlesConverter.class,
        MediaStreamConverter.class})
public abstract class EasyPlexDatabase extends RoomDatabase {

    public abstract FavoriteDao favoriteDao();
    public abstract DownloadDao progressDao();
    public abstract HistoryDao historyDao();
    public abstract StreamListDao streamListDao();
}
