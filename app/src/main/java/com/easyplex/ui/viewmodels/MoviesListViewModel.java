package com.easyplex.ui.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.LiveDataReactiveStreams;
import androidx.lifecycle.ViewModel;

import com.easyplex.data.local.entity.Download;
import com.easyplex.data.local.entity.History;
import com.easyplex.data.local.entity.Media;
import com.easyplex.data.repository.MediaRepository;

import java.util.List;
import javax.inject.Inject;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import timber.log.Timber;

/**
 * ViewModel to cache, retrieve data for MyList
 *
 */
public class MoviesListViewModel extends ViewModel {


    private MediaRepository mediaRepository;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private LiveData<List<Media>> favoriteMoviesLiveData;
    public LiveData<List<Download>> downloadMoviesLiveData;
    public LiveData<List<History>> historyWatchLiveData;

    @Inject
    MoviesListViewModel(MediaRepository mediaRepository) {

        // Get a list of Favorite Movies from the Database
        favoriteMoviesLiveData = LiveDataReactiveStreams.fromPublisher(mediaRepository.getFavorites()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread()));



        // Get a list of Favorite Movies from the Database
        downloadMoviesLiveData = LiveDataReactiveStreams.fromPublisher(mediaRepository.getDownloads()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread()));



        // Get a list of Favorite Movies from the Database
        historyWatchLiveData = LiveDataReactiveStreams.fromPublisher(mediaRepository.getwatchHistory()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread()));

        this.mediaRepository = mediaRepository;

    }



    // Return Movies & Series & Animes in MyList
    public LiveData<List<Media>> getMoviesFavorites() {
        return favoriteMoviesLiveData;
    }


    // Return Movies & Series & Animes in MyList
    public LiveData<List<Download>> getMoviesDownloads() {
        return downloadMoviesLiveData;
    }



    // Return Movies & Series & Animes in MyList
    public LiveData<List<History>> getHistoryWatch() {
        return historyWatchLiveData;
    }


    // Delete All Movies from MyList
    public void deleteAllMovies() {
        Timber.i("MyList has been cleared...");

        compositeDisposable.add(Completable.fromAction(() -> mediaRepository.deleteAllFromFavorites())
                .subscribeOn(Schedulers.io())
                .subscribe());
    }


    // Delete All Movies from MyList
    public void deleteHistory() {
        Timber.i("History has been cleared...");

        compositeDisposable.add(Completable.fromAction(() -> mediaRepository.deleteAllHistory())
                .subscribeOn(Schedulers.io())
                .subscribe());
    }



    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.clear();
    }
}
