package com.easyplex.ui.viewmodels;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import com.easyplex.data.model.auth.Login;
import com.easyplex.data.remote.ErrorHandling;
import com.easyplex.data.repository.AuthRepository;
import javax.inject.Inject;
import io.reactivex.rxjava3.disposables.CompositeDisposable;



/**
 * ViewModel to cache, retrieve data for RegisterActivity
 *
 * @author Yobex.
 */
public class RegisterViewModel extends ViewModel {

    private AuthRepository authRepository;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();


    @Inject
    RegisterViewModel(AuthRepository authRepository) {
        this.authRepository = authRepository;

    }



    // Handle Registration Process
    public LiveData<ErrorHandling<Login>> getRegister(String name,String email , String password) {
        return authRepository.getRegisterDetail(name,email, password);
    }





    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.clear();
    }
}
