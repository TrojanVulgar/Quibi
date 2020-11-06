package com.easyplex.ui.viewmodels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.easyplex.data.model.MovieResponse;
import com.easyplex.data.repository.MediaRepository;

import javax.inject.Inject;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import timber.log.Timber;



/**
 * ViewModel to cache, retrieve data for HomeFragment
 *
 * @author Yobex.
 */

public class HomeViewModel extends ViewModel {


    private MediaRepository mediaRepository;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    public final MutableLiveData<MovieResponse>  movieRecommendedMutableLiveData = new MutableLiveData<>();
    public final MutableLiveData<MovieResponse>  movieTrendingMutableLiveData = new MutableLiveData<>();
    public final MutableLiveData<MovieResponse>  movieLatestMutableLiveData = new MutableLiveData<>();
    public final MutableLiveData<MovieResponse>  popularSeriesMutableLiveData = new MutableLiveData<>();
    public final MutableLiveData<MovieResponse> latestSeriesMutableLiveData = new MutableLiveData<>();
    public final MutableLiveData<MovieResponse> latestAnimesMutableLiveData = new MutableLiveData<>();
    public final MutableLiveData<MovieResponse> thisweekMutableLiveData = new MutableLiveData<>();
    public final MutableLiveData<MovieResponse> popularMoviesMutableLiveData = new MutableLiveData<>();
    public final MutableLiveData<MovieResponse> featuredMoviesMutableLiveData = new MutableLiveData<>();


    @Inject
    HomeViewModel(MediaRepository mediaRepository) {
        this.mediaRepository = mediaRepository;
    }






    public void getPopularMovies() {
        compositeDisposable.add(mediaRepository.getPopularMovies()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .cache()
                .subscribe(popularMoviesMutableLiveData::postValue, this::handleError)
        );
    }


    public void getThisWeekMovies() {
        compositeDisposable.add(mediaRepository.getThisWeek()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .cache()
                .subscribe(thisweekMutableLiveData::postValue, this::handleError)
        );
    }



    public void getAnimes() {
        compositeDisposable.add(mediaRepository.getAnimes()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .cache()
                .subscribe(latestAnimesMutableLiveData::postValue, this::handleError)
        );
    }





    public void getPopularSeries() {
        compositeDisposable.add(mediaRepository.getPopularSeries()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .cache()
                .subscribe(popularSeriesMutableLiveData::postValue, this::handleError)
        );
    }


    public void getLatestSeries() {
        compositeDisposable.add(mediaRepository.getLatestSeries()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .cache()
                .subscribe(latestSeriesMutableLiveData::postValue, this::handleError)
        );
    }


    public void getFeaturedMovies() {
        compositeDisposable.add(mediaRepository.getFeatured()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .cache()
                .subscribe(featuredMoviesMutableLiveData::postValue, this::handleError)
        );
    }


    public void getRecommendedMovies() {
        compositeDisposable.add(mediaRepository.getRecommended()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .cache()
                .subscribe(movieRecommendedMutableLiveData::postValue, this::handleError)
        );
    }



    public void getTrendingdMovies() {
        compositeDisposable.add(mediaRepository.getTrending()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .cache()
                .subscribe(movieTrendingMutableLiveData::postValue, this::handleError)
        );
    }


    public void getLatestMovies() {
        compositeDisposable.add(mediaRepository.getLatestMovies()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .cache()
                .subscribe(movieLatestMutableLiveData::postValue, this::handleError)
        );
    }



    // HandleError
    private void handleError(Throwable e) {

        Timber.i("In onError()%s", e.getMessage());
    }


    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.clear();
    }


}
