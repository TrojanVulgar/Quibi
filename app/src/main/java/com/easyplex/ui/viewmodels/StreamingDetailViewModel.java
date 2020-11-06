package com.easyplex.ui.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.LiveDataReactiveStreams;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.easyplex.data.local.entity.History;
import com.easyplex.data.local.entity.Media;
import com.easyplex.data.local.entity.Stream;
import com.easyplex.data.model.MovieResponse;
import com.easyplex.data.repository.MediaRepository;

import java.util.List;

import javax.inject.Inject;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import timber.log.Timber;

/**
 * ViewModel to cache, retrieve data for StreamingFragment
 *
 * @author Yobex.
 */
public class StreamingDetailViewModel extends ViewModel {

    private MediaRepository mediaRepository;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    public final MutableLiveData<MovieResponse> latestStreamingMutableLiveData = new MutableLiveData<>();
    public final MutableLiveData<MovieResponse> featuredMoviesMutableLiveData = new MutableLiveData<>();
    public final MutableLiveData<MovieResponse> mostWatchedStreamingMutableLiveData = new MutableLiveData<>();


    public LiveData<List<Stream>> favoriteMoviesLiveData;

    @Inject
    StreamingDetailViewModel (MediaRepository mediaRepository) {


        // Get a list of Favorite Movies from the Database
        favoriteMoviesLiveData = LiveDataReactiveStreams.fromPublisher(mediaRepository.getStreamFavorites()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread()));


        this.mediaRepository = mediaRepository;

    }



    // Fetch Streaming List
    public void getStreaming() {
        compositeDisposable.add(mediaRepository.getLatestStreaming()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .cache()
                .subscribe(latestStreamingMutableLiveData::postValue, this::handleError)
        );
    }



    // Fetch Streaming List
    public void getMostWatchedStreaming() {
        compositeDisposable.add(mediaRepository.getWatchedStreaming()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .cache()
                .subscribe(mostWatchedStreamingMutableLiveData::postValue, this::handleError)
        );
    }



    // Fetch Streaming List
    public  void getFeaturedStreaming() {
        compositeDisposable.add(mediaRepository.getFeaturedStreaming()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .cache()
                .subscribe(featuredMoviesMutableLiveData::postValue, this::handleError)
        );
    }



    // Return Movies & Series & Animes in MyList
    public LiveData<List<Stream>> getStreamFavorites() {
        return favoriteMoviesLiveData;
    }



    // Handle Error for getStreaming
    private void handleError(Throwable e) {
        Timber.i("In onError()%s", e.getMessage());
    }


    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.clear();
    }
}
