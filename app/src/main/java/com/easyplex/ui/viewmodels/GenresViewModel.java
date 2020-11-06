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
public class GenresViewModel extends ViewModel {

    private MediaRepository mediaRepository;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    public final MutableLiveData<GenresByID> movieDetailMutableLiveData = new MutableLiveData<>();
    public final MutableLiveData<GenresData> movieGenresDataMutableLiveData = new MutableLiveData<>();




    @Inject
    GenresViewModel(MediaRepository mediaRepository) {
        this.mediaRepository = mediaRepository;
    }




    // Fetch Movies Genres List
    public void getMoviesGenresList() {
        compositeDisposable.add(mediaRepository.getMoviesGenres()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .cache()
                .subscribe(movieDetailMutableLiveData::postValue, this::handleError)
        );
    }




    public void getMoviesAllMovies() {
        compositeDisposable.add(mediaRepository.getAllMovies()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .cache()
                .subscribe(movieGenresDataMutableLiveData::postValue, this::handleError)
        );
    }



    public void getMoviesLatestSeries() {
        compositeDisposable.add(mediaRepository.getLatestAddedSeries()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .cache()
                .subscribe(movieGenresDataMutableLiveData::postValue, this::handleError)
        );
    }



    public void getMoviesLatestAnimes() {
        compositeDisposable.add(mediaRepository.getLatestAddedAnimes()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .cache()
                .subscribe(movieGenresDataMutableLiveData::postValue, this::handleError)
        );
    }



    public void getMoviesLatestMovies() {
        compositeDisposable.add(mediaRepository.getLatestAdded()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .cache()
                .subscribe(movieGenresDataMutableLiveData::postValue, this::handleError)
        );
    }


    public void getByYear() {
        compositeDisposable.add(mediaRepository.getByYear()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .cache()
                .subscribe(movieGenresDataMutableLiveData::postValue, this::handleError)
        );
    }


    public void getByYeartv() {
        compositeDisposable.add(mediaRepository.getByYeartv()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .cache()
                .subscribe(movieGenresDataMutableLiveData::postValue, this::handleError)
        );
    }



    public void getByYearAnimes() {
        compositeDisposable.add(mediaRepository.getByYearAnimes()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .cache()
                .subscribe(movieGenresDataMutableLiveData::postValue, this::handleError)
        );
    }


    public void getByRating() {
        compositeDisposable.add(mediaRepository.getByRating()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .cache()
                .subscribe(movieGenresDataMutableLiveData::postValue, this::handleError)
        );
    }



    public void getByRatingtv() {
        compositeDisposable.add(mediaRepository.getByRatingTv()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .cache()
                .subscribe(movieGenresDataMutableLiveData::postValue, this::handleError)
        );
    }


    public void getByRatingAnimes() {
        compositeDisposable.add(mediaRepository.getByRatingAnimes()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .cache()
                .subscribe(movieGenresDataMutableLiveData::postValue, this::handleError)
        );
    }


    public void getByViews() {
        compositeDisposable.add(mediaRepository.getByViews()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .cache()
                .subscribe(movieGenresDataMutableLiveData::postValue, this::handleError)
        );
    }

    public void getByViewstv() {
        compositeDisposable.add(mediaRepository.getByViewstv()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .cache()
                .subscribe(movieGenresDataMutableLiveData::postValue, this::handleError)
        );
    }


    public void getByViewsAnimes() {
        compositeDisposable.add(mediaRepository.getByViewsAnimes()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .cache()
                .subscribe(movieGenresDataMutableLiveData::postValue, this::handleError)
        );
    }

    public void getMoviesAllSeries() {
        compositeDisposable.add(mediaRepository.getAllSeries()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .cache()
                .subscribe(movieGenresDataMutableLiveData::postValue, this::handleError)
        );
    }



    public void getMoviesAllAnimes() {
        compositeDisposable.add(mediaRepository.getAllAnimes()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .cache()
                .subscribe(movieGenresDataMutableLiveData::postValue, this::handleError)
        );
    }


    // Fetch Movie By Genre
    public void getMoviesByGenres(int id) {
        compositeDisposable.add(mediaRepository.getMovieByGenre(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .cache()
                .subscribe(movieGenresDataMutableLiveData::postValue, this::handleError)
        );
    }

    // Fetch Serie By Genre
    public void getSeriesByGenres(int id) {
        compositeDisposable.add(mediaRepository.getSerieByGenre(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .cache()
                .subscribe(movieGenresDataMutableLiveData::postValue, this::handleError)
        );
    }



    // Fetch Anime By Genre
    public void getAnimesByGenres(int id) {
        compositeDisposable.add(mediaRepository.getAnimeByGenre(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .cache()
                .subscribe(movieGenresDataMutableLiveData::postValue, this::handleError)
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
