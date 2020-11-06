package com.easyplex.ui.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.easyplex.data.local.entity.Download;
import com.easyplex.data.local.entity.History;
import com.easyplex.data.local.entity.Media;
import com.easyplex.data.model.MovieResponse;
import com.easyplex.data.model.credits.MovieCreditsResponse;
import com.easyplex.data.model.media.Resume;
import com.easyplex.data.model.report.Report;
import com.easyplex.data.repository.MediaRepository;
import javax.inject.Inject;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

import timber.log.Timber;

/**
 * ViewModel to cache, retrieve data for MovieDetailActivity
 *
 * @author Yobex.
 */
public class MovieDetailViewModel extends ViewModel {

    private MediaRepository mediaRepository;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    public final MutableLiveData<Media> movieDetailMutableLiveData = new MutableLiveData<>();
    public final MutableLiveData<MovieCreditsResponse> movieCreditsMutableLiveData = new MutableLiveData<>();
    public final MutableLiveData<MovieResponse> movieRelatedsMutableLiveData = new MutableLiveData<>();
    public final MutableLiveData<Report> reportMutableLiveData = new MutableLiveData<>();
    public final MutableLiveData<Report> report2MutableLiveData = new MutableLiveData<>();
    public final MutableLiveData<Resume> resumeMutableLiveData = new MutableLiveData<>();


    @Inject
    MovieDetailViewModel(MediaRepository mediaRepository) {
        this.mediaRepository = mediaRepository;
    }




    // Send Report for a Movie
    public void sendReport (String title,String message) {
        compositeDisposable.add(mediaRepository.getReport(title,message)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .cache()
                .subscribe(reportMutableLiveData::postValue, this::handleError)
        );
    }




    // Send Report for a Movie
    public void sendReport2 (String title,String message) {
        compositeDisposable.add(mediaRepository.getReport2(title,message)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .cache()
                .subscribe(report2MutableLiveData::postValue, this::handleError)
        );
    }



    // Send Report for a Movie
    public void getResumeMovie (String tmdb) {
        compositeDisposable.add(mediaRepository.getResumeById(tmdb)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .cache()
                .subscribe(resumeMutableLiveData::postValue, this::handleError)
        );
    }





    // get Movie Details (Name,Trailer,Release Date...)
    public void getMovieDetails(String tmdb) {
        compositeDisposable.add(mediaRepository.getMovie(tmdb)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .cache()
                .subscribe(movieDetailMutableLiveData::postValue, this::handleError)
        );
    }



    // Get Movie Cast
    public void getMovieCast(int id) {
        compositeDisposable.add(mediaRepository.getMovieCredits(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .cache()
                .subscribe(movieCreditsMutableLiveData::postValue, this::handleError)
        );
    }



    // Get Relateds Movies
    public void getRelatedsMovies(int id) {
        compositeDisposable.add(mediaRepository.getRelateds(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .cache()
                .subscribe(movieRelatedsMutableLiveData::postValue, this::handleError)
        );
    }





    // Add a Movie To MyList
    public void addFavorite(Media mediaDetail) {
        Timber.i("Movie Added To Watchlist");

        compositeDisposable.add(Completable.fromAction(() -> mediaRepository.addFavorite(mediaDetail))
                .subscribeOn(Schedulers.io())
                .subscribe());
    }


    // Add a Movie To MyList
    public void addFavorite1(Download mediaDetail) {
        Timber.i("Movie Added To Watchlist");

        compositeDisposable.add(Completable.fromAction(() -> mediaRepository.addFavorite1(mediaDetail))
                .subscribeOn(Schedulers.io())
                .subscribe());
    }


    // Add a Movie To MyList
    public void addhistory(History history) {
        Timber.i("Movie Added To Watchlist");

        compositeDisposable.add(Completable.fromAction(() -> mediaRepository.addhistory(history))
                .subscribeOn(Schedulers.io())
                .subscribe());
    }


    // Remove a Movie from MyList
    public void removeFavorite(Media mediaDetail) {
        Timber.i("Movie Removed From Watchlist");

        compositeDisposable.add(Completable.fromAction(() -> mediaRepository.removeFavorite(mediaDetail))
                .subscribeOn(Schedulers.io())
                .subscribe());
    }


    // Remove a Movie from MyList
    public void removeDownload(Download download) {
        Timber.i("Movie Removed From Download");

        compositeDisposable.add(Completable.fromAction(() -> mediaRepository.removeDownload(download))
                .subscribeOn(Schedulers.io())
                .subscribe());
    }




    // Handle Error
    private void handleError(Throwable e) {

        Timber.i("In onError()%s", e.getMessage());
        Timber.i("In onError()%s", e.getCause());
    }




    // Check if the Movie is in MyList
    public LiveData<Media> isFavorite(int movieId) {
        Timber.i("Checking if Movie is in the Favorites");
        return mediaRepository.isFavorite(movieId);
    }


    // Check if the Movie is in MyList
    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.clear();
    }
}
