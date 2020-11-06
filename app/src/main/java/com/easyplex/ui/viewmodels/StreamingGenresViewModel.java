package com.easyplex.ui.viewmodels;


import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.easyplex.data.model.genres.GenresByID;
import com.easyplex.data.model.genres.GenresData;
import com.easyplex.data.repository.MediaRepository;

import javax.inject.Inject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import timber.log.Timber;

/**
 * ViewModel to cache, retrieve data for MoviesFragment & SeriesFragment
 *
 * @author Yobex.
 */
public class StreamingGenresViewModel extends ViewModel {

    private MediaRepository mediaRepository;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    public final MutableLiveData<GenresByID> streamingDetailMutableLiveData = new MutableLiveData<>();
    public final MutableLiveData<GenresData> streamGenresDataMutableLiveData = new MutableLiveData<>();





    @Inject
    StreamingGenresViewModel(MediaRepository mediaRepository) {
        this.mediaRepository = mediaRepository;
    }




    // Fetch Movies Genres List
    public void getStreamingGenresList() {
        compositeDisposable.add(mediaRepository.getStreamingGenres()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .cache()
                .subscribe(streamingDetailMutableLiveData::postValue, this::handleError)
        );
    }



    public void getStreamingByGenres(int id) {
        compositeDisposable.add(mediaRepository.getStreamingByGenre(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .cache()
                .subscribe(streamGenresDataMutableLiveData::postValue, this::handleError)
        );
    }



    // Handle Errors
    private void handleError(Throwable e) {
        Timber.i("In onError()%s", e.getMessage());
    }



    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.clear();
    }
}
