package com.easyplex.ui.login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.easyplex.data.remote.ErrorHandling;
import com.easyplex.ui.viewmodels.LoginViewModel;
import com.easyplex.util.DialogHelper;
import com.google.android.material.textfield.TextInputLayout;
import com.easyplex.R;
import com.easyplex.ui.manager.TokenManager;
import com.easyplex.ui.base.BaseActivity;
import com.easyplex.ui.register.RegisterActivity;
import com.easyplex.util.Tools;
import javax.inject.Inject;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasAndroidInjector;
import static com.basgeekball.awesomevalidation.ValidationStyle.TEXT_INPUT_LAYOUT;
import static com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions.withCrossFade;
import static com.easyplex.util.Constants.SERVER_BASE_URL;


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


public class LoginActivity extends AppCompatActivity implements HasAndroidInjector {


    private Unbinder unbinder;

    @Inject
    DispatchingAndroidInjector<Object> androidInjector;

    @Override
    public AndroidInjector<Object> androidInjector() {
        return androidInjector;
    }

    @Inject
    SharedPreferences.Editor sharedPreferencesEditor;

    @Inject
    TokenManager tokenManager;

    @Inject
    ViewModelProvider.Factory viewModelFactory;


    private LoginViewModel loginViewModel;

    AwesomeValidation validator;

    @BindView(R.id.til_email)
    TextInputLayout tilEmail;

    @BindView(R.id.til_password)
    TextInputLayout tilPassword;

    @BindView(R.id.logo_image_top)
    ImageView logoimagetop;


    @BindView(R.id.form_container)
    LinearLayout formContainer;

    @BindView(R.id.loader)
    ProgressBar loader;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        unbinder = ButterKnife.bind(this);

        loginViewModel = new ViewModelProvider(this, viewModelFactory).get(LoginViewModel.class);


        Tools.hideSystemPlayerUi(this, true, 0);

        Tools.setSystemBarTransparent(this);

        onLoadAppLogo();
        onLoadValitator();
        onSetupRules();



        if (tokenManager.getToken().getAccessToken() != null) {
            startActivity(new Intent(LoginActivity.this, BaseActivity.class));
            finish();

        }


    }


    @OnClick(R.id.btn_skip)
    void skip(){

        startActivity(new Intent(LoginActivity.this, BaseActivity.class));
        finish();

    }


    @OnClick(R.id.btn_login)
    void login() {

        String email = tilEmail.getEditText().getText().toString();
        String password = tilPassword.getEditText().getText().toString();
        tilEmail.setError(null);
        tilPassword.setError(null);
        validator.clear();


        if (validator.validate()) {


            hideKeyboard();
            formContainer.setVisibility(View.GONE);
            loader.setVisibility(View.VISIBLE);

            loginViewModel.getLogin(email, password).observe(LoginActivity.this, login -> {

                if (login.status == ErrorHandling.Status.SUCCESS) {
                    tokenManager.saveToken(login.data);
                    startActivity(new Intent(LoginActivity.this, BaseActivity.class));
                    finish();

                } else {


                    formContainer.setVisibility(View.VISIBLE);
                    loader.setVisibility(View.GONE);

                    DialogHelper.erroLogin(this);


                }

            });

        }

    }


    private void onLoadValitator() {

        validator = new AwesomeValidation(TEXT_INPUT_LAYOUT);
        validator.setTextInputLayoutErrorTextAppearance(R.style.TextInputLayoutErrorStyle);
    }


    // Display Main Logo
    private void onLoadAppLogo() {

        Glide.with(this.getApplicationContext()).asBitmap().load(SERVER_BASE_URL +"image/minilogo")
                .fitCenter()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .transition(withCrossFade())
                .skipMemoryCache(true)
                .into(logoimagetop);

    }


    // Register Button
    @OnClick(R.id.go_to_register)
    void goToRegister() {
        startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
        Animatoo.animateFade(this);
    }


    // Hide Keyboard on Submit
    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


    // Input Email & Password Validation
    public void onSetupRules() {
        validator.addValidation(this, R.id.til_email, Patterns.EMAIL_ADDRESS, R.string.err_email);
        validator.addValidation(this, R.id.til_password, RegexTemplate.NOT_EMPTY, R.string.err_password);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        logoimagetop = null;
        unbinder.unbind();
    }
}
