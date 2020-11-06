package com.easyplex.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.easyplex.data.remote.ApiInterface;
import com.easyplex.ui.manager.TokenManager;
import com.easyplex.data.model.auth.Login;
import com.easyplex.data.model.auth.UserAuthInfo;
import com.easyplex.data.remote.ErrorHandling;
import javax.inject.Inject;
import javax.inject.Named;
import io.reactivex.rxjava3.core.Observable;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.easyplex.util.Constants.ERROR;


/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */
public class AuthRepository {


    @Inject
    @Named("Auth")
    ApiInterface requestAuth;


    @Inject
    TokenManager tokenManager;


    @Inject
    AuthRepository (ApiInterface requestLogin, TokenManager tokenManager) {
        this.tokenManager = tokenManager;
        this.requestAuth = requestLogin;
    }





    public LiveData<ErrorHandling<UserAuthInfo>> editUserProfile(String name,String email, String password) {
        final MutableLiveData<ErrorHandling<UserAuthInfo>> register = new MutableLiveData<>();


        Call<UserAuthInfo> call = requestAuth.updateUserProfile(name,email,password);
        call.enqueue(new Callback<UserAuthInfo>() {

            @Override
            public void onResponse(Call<UserAuthInfo> call, Response<UserAuthInfo> response) {
                if (response.body() != null) {
                    UserAuthInfo body = response.body();
                    register.setValue(ErrorHandling.success(body));
                }
            }

            @Override
            public void onFailure(Call<UserAuthInfo> call, Throwable t) {
                register.setValue(ErrorHandling.error(t.getMessage(),null));
            }
        });

        return register;
    }





    // Update User to Premuim
    public LiveData<ErrorHandling<UserAuthInfo>> getUpgradePlan(String transactionId,String stripePlanId,String stripePlanPrice,String packName,String packDuration) {
        final MutableLiveData<ErrorHandling<UserAuthInfo>> login = new MutableLiveData<>();


        Call<UserAuthInfo> call = requestAuth.upgradePlan(transactionId,stripePlanId,stripePlanPrice,packName,packDuration);
        call.enqueue(new Callback<UserAuthInfo>() {

            @Override
            public void onResponse(Call<UserAuthInfo> call, Response<UserAuthInfo> response) {


                if (response.isSuccessful()) {
                    UserAuthInfo body = response.body();
                    login.setValue(ErrorHandling.success(body));


                }else {

                    UserAuthInfo body = response.body();

                    login.setValue(ErrorHandling.error(ERROR,body));
                }
            }

            @Override
            public void onFailure(Call<UserAuthInfo> call, Throwable t) {
                login.setValue(ErrorHandling.error(t.getMessage(),null));
            }
        });

        return login;
    }




    public LiveData<ErrorHandling<UserAuthInfo>> getUpgradePaypal(String packId,String transactionId,String packName,String packDuration) {
        final MutableLiveData<ErrorHandling<UserAuthInfo>> login = new MutableLiveData<>();


        Call<UserAuthInfo> call = requestAuth.userPaypalUpdate(packId,transactionId,packName,packDuration);
        call.enqueue(new Callback<UserAuthInfo>() {

            @Override
            public void onResponse(Call<UserAuthInfo> call, Response<UserAuthInfo> response) {


                if (response.isSuccessful()) {
                    UserAuthInfo body = response.body();
                    login.setValue(ErrorHandling.success(body));


                }else {

                    UserAuthInfo body = response.body();

                    login.setValue(ErrorHandling.error("Error",body));
                }
            }

            @Override
            public void onFailure(Call<UserAuthInfo> call, Throwable t) {
                login.setValue(ErrorHandling.error(t.getMessage(),null));
            }
        });

        return login;
    }




    // Return Authenticated User with informations(Name,Email,etc...)
    public Observable<UserAuthInfo> getAuth() {
        return requestAuth.userAuthInfo();
    }


    // Cancel User Subcription
    public Observable<UserAuthInfo> cancelAuthSubcription() {
        return requestAuth.cancelUserAuthInfo();
    }


    // Cancel User Subcription
    public Observable<UserAuthInfo> cancelAuthSubcriptionPaypal() {
        return requestAuth.cancelUserAuthInfoPaypal();
    }





    // Handle User Login
    public LiveData<ErrorHandling<Login>> getDetail(String username,String password) {
        final MutableLiveData<ErrorHandling<Login>> login = new MutableLiveData<>();


        Call<Login> call = requestAuth.login(username,password);
        call.enqueue(new Callback<Login>() {

            @Override
            public void onResponse(Call<Login> call, Response<Login> response) {


                if (response.isSuccessful()) {
                    Login body = response.body();
                    login.setValue(ErrorHandling.success(body));


                }else {

                    Login body = response.body();

                    login.setValue(ErrorHandling.error("Error",body));
                }
            }

            @Override
            public void onFailure(Call<Login> call, Throwable t) {
                login.setValue(ErrorHandling.error(t.getMessage(),null));
            }
        });

        return login;
    }





    // Handle User Registration
    public LiveData<ErrorHandling<Login>> getRegisterDetail(String name,String email, String password) {
        final MutableLiveData<ErrorHandling<Login>> register = new MutableLiveData<>();


        Call<Login> call = requestAuth.register(name,email,password);
        call.enqueue(new Callback<Login>() {

            @Override
            public void onResponse(Call<Login> call, Response<Login> response) {
                if (response.body() != null) {
                    Login body = response.body();
                    register.setValue(ErrorHandling.success(body));
                }
            }

            @Override
            public void onFailure(Call<Login> call, Throwable t) {
                register.setValue(ErrorHandling.error(t.getMessage(),null));
            }
        });

        return register;
    }

}

