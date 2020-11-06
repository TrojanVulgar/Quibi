package com.easyplex.ui.register;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.transition.TransitionManager;
import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.bumptech.glide.Glide;
import com.easyplex.data.remote.ErrorHandling;
import com.easyplex.ui.viewmodels.RegisterViewModel;
import com.easyplex.util.DialogHelper;
import com.google.android.material.textfield.TextInputLayout;
import com.easyplex.R;
import com.easyplex.ui.manager.TokenManager;
import com.easyplex.ui.login.LoginActivity;
import com.easyplex.util.Tools;
import javax.inject.Inject;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasAndroidInjector;


/**
 * EasyPlex - Android Movie Portal App
 * @package     EasyPlex - Android Movie Portal App
 * @author      @Y0bEX
 * @copyright   Copyright (c) 2020 Y0bEX,
 * @license     http://codecanyon.net/wiki/support/legal-terms/licensing-terms/
 * @profile     https://codecanyon.net/user/yobex
 * @link        yobexd@gmail.com
 * @skype       yobexd@gmail.com
 **/


public class RegisterActivity extends AppCompatActivity implements HasAndroidInjector {


    private Unbinder unbinder;

    @Inject
    SharedPreferences.Editor sharedPreferencesEditor;

    @Inject
    TokenManager tokenManager;

    @Inject
    DispatchingAndroidInjector<Object> androidInjector;

    @Override
    public AndroidInjector<Object> androidInjector() {
        return androidInjector;
    }


    @Inject ViewModelProvider.Factory viewModelFactory;
    private RegisterViewModel registerViewModel;

    @BindView(R.id.til_name)
    TextInputLayout tilName;

    @BindView(R.id.til_email)
    TextInputLayout tilEmail;

    @BindView(R.id.til_password)
    TextInputLayout tilPassword;


    @BindView(R.id.form_container)
    LinearLayout formContainer;

    @BindView(R.id.loader)
    ProgressBar loader;

    @BindView(R.id.logo_image_top)
    ImageView logoimagetop;

    AwesomeValidation validator;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);


        unbinder = ButterKnife.bind(this);


        // ViewModel to cache, retrieve data for RegisterActivity
       registerViewModel = new ViewModelProvider(this, viewModelFactory).get(RegisterViewModel.class);

        Tools.hideSystemPlayerUi(this,true,0);

        Tools.setSystemBarTransparent(this);

        validator = new AwesomeValidation(ValidationStyle.TEXT_INPUT_LAYOUT);

        setupRules();
        onLoadAppLogo();

    }



    // Display Main Logo
    private void onLoadAppLogo() {

        Tools.loadMiniLogo(logoimagetop);
    }


    @OnClick(R.id.btn_register)
    void register(){

        String name = tilName.getEditText().getText().toString();
        String email = tilEmail.getEditText().getText().toString();
        String password = tilPassword.getEditText().getText().toString();

        tilName.setError(null);
        tilEmail.setError(null);
        tilPassword.setError(null);

        if (validator.validate()) {
            showLoading();
            registerViewModel.getRegister(name,email, password).observe(this, login -> {

                if (login.status == ErrorHandling.Status.SUCCESS ) {
                    tokenManager.saveToken(login.data);
                    startActivity(new Intent(this, RegistrationSucess.class));
                    finish();


                } else  {

                  showForms();

                DialogHelper.erroRegister(this);


            }

            });

        }

    }


    // show Progressbar on Register Button Submit
    private void showLoading(){
        formContainer.setVisibility(View.GONE);
        loader.setVisibility(View.VISIBLE);
    }


    private void showForms(){

        formContainer.setVisibility(View.VISIBLE);
        loader.setVisibility(View.GONE);

    }




    // Get the validation rules that apply to the request.
    public void setupRules(){

        validator.addValidation(this, R.id.til_name, RegexTemplate.NOT_EMPTY, R.string.err_name);
        validator.addValidation(this, R.id.til_email, Patterns.EMAIL_ADDRESS, R.string.err_email);
        validator.addValidation(this, R.id.til_password, "[a-zA-Z0-9]{6,}", R.string.err_password);
    }

    @OnClick(R.id.go_to_login)
    void goToRegister(){
        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
        Glide.get(this).clearMemory();
    }

}
