package com.easyplex.ui.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.easyplex.data.local.entity.Media;
import com.easyplex.data.model.report.Report;
import com.easyplex.data.repository.AnimeRepository;
import javax.inject.Inject;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import timber.log.Timber;

/**
 * ViewModel to cache, retrieve data for AnimeDetailActivity
 *
 * @author Yobex.
 */
public class AnimeViewModel extends ViewModel {

    private AnimeRepository animeRepository;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    public final MutableLiveData<Media> animeDetailMutableLiveData = new MutableLiveData<>();
    public final MutableLiveData<Report> reportMutableLiveData = new MutableLiveData<>();


    @Inject
    AnimeViewModel(AnimeRepository animeRepository) {
        this.animeRepository = animeRepository;

    }





    // send report
    public void sendReport (String title,String message) {
        compositeDisposable.add(animeRepository.getReport(title,message)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .cache()
                .subscribe(reportMutableLiveData::postValue, this::handleError)
        );
    }


    // Add Anime To Favorite
    public void addtvFavorite(Media series) {
        Timber.i("Anime Added To Watchlist");
        compositeDisposable.add(Completable.fromAction(() -> animeRepository.addFavorite(series))
                .subscribeOn(Schedulers.io())
                .subscribe());
    }



    // Remove Anime from Favorite
    public void removeTvFromFavorite(Media series) {
        Timber.i("Anime Removed From Watchlist");

        compositeDisposable.add(Completable.fromAction(() -> animeRepository.removeFavorite(series))
                .subscribeOn(Schedulers.io())
                .subscribe());
    }



    // Check if the Anime is in Favorite
    public LiveData<Media> isFavorite(int movieId) {
        Timber.i("Checking if Serie is in the Favorites");
        return animeRepository.isFavorite(movieId);
    }


    // get Anime Details
    public void getAnimeDetails(String id) {
        compositeDisposable.add(animeRepository.getAnimeDetails(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .cache()
                .subscribe(animeDetailMutableLiveData::postValue, this::handleError)
        );
    }





    // Handle Errros
    private void handleError(Throwable e) {
        Timber.i("In onError()%s", e.getMessage());
    }



    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.clear();
    }
}
