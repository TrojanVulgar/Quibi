package com.easyplex.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import com.easyplex.data.local.entity.History;
import java.util.List;
import io.reactivex.Flowable;

/**
 * Data Access Object that contains methods used for accessing the database.
 *
 * @author Yobex.
 */
@Dao
public interface HistoryDao {

    // Return Movies & Series From Favorite Table
    @Query("SELECT * FROM history")
    Flowable<List<History>> getHistory();



    // Save the the movie or serie in the  Favorite Table
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveMediaToFavorite(History history);



    @Query("DELETE FROM history")
    void deleteHistory();




}
