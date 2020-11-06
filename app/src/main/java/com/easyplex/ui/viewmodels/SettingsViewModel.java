package com.easyplex.ui.viewmodels;


import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.easyplex.data.model.MovieResponse;
import com.easyplex.data.model.ads.Ads;
import com.easyplex.data.model.settings.Settings;
import com.easyplex.data.model.status.Status;
import com.easyplex.data.repository.SettingsRepository;
import com.easyplex.ui.manager.SettingsManager;

import javax.inject.Inject;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import timber.log.Timber;

/**
 * ViewModel to cache, retrieve App Settings
 *
 * @author Yobex.
 */
public class SettingsViewModel extends ViewModel {

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    private SettingsRepository settingsRepository;
    public final MutableLiveData<Settings> settingsMutableLiveData = new MutableLiveData<>();
    public final MutableLiveData<Ads> adsMutableLiveData = new MutableLiveData<>();
    public final MutableLiveData<Status> statusMutableLiveData = new MutableLiveData<>();
    public final MutableLiveData<Status> status2MutableLiveData = new MutableLiveData<>();
    public final MutableLiveData<MovieResponse> plansMutableLiveData = new MutableLiveData<>();



    @Inject
    SettingsManager settingsManager;


    @Inject
    SettingsViewModel(SettingsRepository settingsRepository) {
        this.settingsRepository = settingsRepository;

    }


    public void getSettingsDetails() {

        // Fetch Settings Details
        compositeDisposable.add(settingsRepository.getSettings()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .cache()
                .subscribe(settingsMutableLiveData::postValue, this::handleError));



        // Fetch Ads Details
       compositeDisposable.add(settingsRepository.getAdsSettings()
               .subscribeOn(Schedulers.io())
               .observeOn(AndroidSchedulers.mainThread())
               .cache()
               .subscribe(adsMutableLiveData::postValue, this::handleError));




        // Fetch Status Details
      compositeDisposable.add(settingsRepository.getStatus()
              .subscribeOn(Schedulers.io())
              .observeOn(AndroidSchedulers.mainThread())
              .cache()
              .subscribe(statusMutableLiveData::postValue, this::handleError))
        ;


        // Fetch Status Details
        compositeDisposable.add(settingsRepository.getApiStatus(settingsManager.getSettings().getPurchaseKey())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(status2MutableLiveData::postValue, this::handleError))
        ;

    }



    public void getPlans() {

        // Fetch Settings Details
        compositeDisposable.add(settingsRepository.getPlans()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .cache()
                .subscribe(plansMutableLiveData::postValue, this::handleError));

    }



        // Handle Errors
    private void handleError(Throwable e) {
        Timber.i("In onError()%s", e.getMessage());
    }


    @Override
    public void onCleared() {
        super.onCleared();
        compositeDisposable.clear();
    }
}
