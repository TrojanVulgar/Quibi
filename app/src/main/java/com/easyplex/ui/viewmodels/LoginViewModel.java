package com.easyplex.ui.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.easyplex.data.model.auth.Login;
import com.easyplex.data.model.auth.UserAuthInfo;
import com.easyplex.data.remote.ErrorHandling;
import com.easyplex.data.repository.AuthRepository;

import javax.inject.Inject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import timber.log.Timber;


/**
 * ViewModel to cache, retrieve data for LoginActivity
 *
 * @author Yobex.
 */
public class LoginViewModel extends ViewModel {

    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private AuthRepository authRepository;


    public final MutableLiveData<UserAuthInfo> authDetailMutableLiveData = new MutableLiveData<>();

    public final MutableLiveData<UserAuthInfo> authUpgradeMutableLiveData = new MutableLiveData<>();

    public final MutableLiveData<UserAuthInfo> authCancelPlanMutableLiveData = new MutableLiveData<>();

    public final MutableLiveData<UserAuthInfo> authCancelPaypalMutableLiveData = new MutableLiveData<>();




    @Inject
    LoginViewModel(AuthRepository authRepository) {
        this.authRepository = authRepository;

    }







    // Update User Profile
    public LiveData<ErrorHandling<UserAuthInfo>> updateUser(String name,String email , String password) {
        return authRepository.editUserProfile(name,email, password);
    }




    // Update User to Premuim

    public LiveData<ErrorHandling<UserAuthInfo>> getSubscribePlan(String transactionId,String
            stripePlanId,String stripePlanPrice,String
            packName,String packDuration) {
        return authRepository.getUpgradePlan(transactionId,stripePlanId,stripePlanPrice,packName,packDuration);
    }


    // Update User to Premuim

    public LiveData<ErrorHandling<UserAuthInfo>> getSubscribePaypal(String packId,String transactionId,String packName,String packDuration) {
        return authRepository.getUpgradePaypal(packId,transactionId,packName,packDuration);
    }





    // Return Login Route
    public LiveData<ErrorHandling<Login>> getLogin(String username , String password) {
        return authRepository.getDetail(username, password);
    }



    // Return the Authenticated User Details
    public void getAuthDetails() {
        compositeDisposable.add(authRepository.getAuth()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .cache()
                .subscribe(authDetailMutableLiveData::postValue, this::handleError)
        );
    }



    public void cancelAuthSubscription() {
        compositeDisposable.add(authRepository.cancelAuthSubcription()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .cache()
                .subscribe(authCancelPlanMutableLiveData::postValue, this::handleError)
        );
    }


    public void cancelAuthSubscriptionPaypal() {
        compositeDisposable.add(authRepository.cancelAuthSubcriptionPaypal()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .cache()
                .subscribe(authCancelPaypalMutableLiveData::postValue, this::handleError)
        );
    }




    private void handleError(Throwable e) {
        Timber.i("In onError()%s", e.getMessage());
    }


    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.clear();
    }
}
