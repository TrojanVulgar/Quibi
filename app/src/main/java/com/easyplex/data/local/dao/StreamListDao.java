package com.easyplex.data.local.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.RoomWarnings;

import com.easyplex.data.local.entity.Media;
import com.easyplex.data.local.entity.Stream;
import java.util.List;
import io.reactivex.Flowable;

/**
 * Data Access Object that contains methods used for accessing the database.
 *
 * @author Yobex.
 */
@Dao
public interface StreamListDao {

    // Return Movies & Series From Favorite Table
    @Query("SELECT * FROM stream")
    Flowable<List<Stream>> getHistory();


    @SuppressWarnings({RoomWarnings.CURSOR_MISMATCH})
    @Query("SELECT * FROM stream")
    Flowable<List<Stream>> getFavorite();


    // Save the the movie or serie in the  Favorite Table
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveMediaToFavorite(Stream stream);


    // Return true if the element in the featured is in the  Favorite Table
    @Query("SELECT * FROM stream WHERE id=:id")
    boolean isStreamFavoriteMovie(int id);


    @Delete
    void deleteStream(Stream stream);





}
