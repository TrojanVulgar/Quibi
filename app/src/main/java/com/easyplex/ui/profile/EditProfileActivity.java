package com.easyplex.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.transition.TransitionManager;
import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.easyplex.R;
import com.easyplex.data.remote.ErrorHandling;
import com.easyplex.ui.viewmodels.LoginViewModel;
import com.easyplex.ui.settings.SettingsActivity;
import com.easyplex.util.Tools;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import javax.inject.Inject;

import at.favre.lib.crypto.bcrypt.BCrypt;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import dagger.android.AndroidInjection;


public class EditProfileActivity extends AppCompatActivity {


    private Unbinder unbinder;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private LoginViewModel loginViewModel;


    @BindView(R.id.til_name)
    TextInputLayout tilName;

    @BindView(R.id.til_email)
    TextInputLayout tilEmail;

    @BindView(R.id.til_password)
    TextInputLayout tilPassword;



    @BindView(R.id.editText_name)
    TextInputEditText editTextName;

    @BindView(R.id.editText_email)
    TextInputEditText editTextEmail;


    @BindView(R.id.close_profile_fragment)
    ImageView closeProfileActivity;

    @BindView(R.id.container)
    ConstraintLayout container;

    @BindView(R.id.form_container)
    LinearLayout formContainer;

    @BindView(R.id.loader)
    ProgressBar loader;

    AwesomeValidation validator;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        AndroidInjection.inject(this);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_edit_profile);

        unbinder = ButterKnife.bind(this);




        // LoginViewModel to cache, retrieve data for Authenticated User
        loginViewModel = new ViewModelProvider(this, viewModelFactory).get(LoginViewModel.class);

        onCheckAuthenticatedUser();

        onHideTaskBar();

        setupRules();


        closeProfileActivity.setOnClickListener(v -> finish());



    }

    private void onCheckAuthenticatedUser() {

        loginViewModel.getAuthDetails();
        loginViewModel.authDetailMutableLiveData.observe(this, auth -> {

            if (auth !=null) {

                editTextName.setText(auth.getName());
                editTextEmail.setText(auth.getEmail());


            }

        });
    }

    private void onHideTaskBar() {

        Tools.hideSystemPlayerUi(this,true,0);

        Tools.setSystemBarTransparent(this);
    }


    @OnClick(R.id.btn_update)
    void register(){

        String name = tilName.getEditText().getText().toString();
        String email = tilEmail.getEditText().getText().toString();
        String password = tilPassword.getEditText().getText().toString();

        tilName.setError(null);
        tilEmail.setError(null);
        tilPassword.setError(null);

        if (validator.validate()) {
            showLoading();


            String passwordHashed = BCrypt.withDefaults().hashToString(12, password.toCharArray());



            loginViewModel.updateUser(name,email, passwordHashed).observe(this, login -> {



                Toast.makeText(this, "Your profile has been updated successfully ! ", Toast.LENGTH_SHORT).show();



                if (login.status == ErrorHandling.Status.SUCCESS ) {

                    startActivity(new Intent(this, SettingsActivity.class));
                    finish();


                } else  {

                    showForms();

                    Toast.makeText(this, "Your profile is not  updated ! ", Toast.LENGTH_SHORT).show();

                }

            });

        }

    }


    // show Progressbar on Update Button Submit
    private void showLoading(){
        TransitionManager.beginDelayedTransition(container);
        formContainer.setVisibility(View.GONE);
        loader.setVisibility(View.VISIBLE);
    }


    private void showForms(){

        formContainer.setVisibility(View.VISIBLE);
        loader.setVisibility(View.GONE);

    }


    // Get the validation rules that apply to the request.
    public void setupRules(){

        validator = new AwesomeValidation(ValidationStyle.TEXT_INPUT_LAYOUT);
        validator.addValidation(this, R.id.til_name, RegexTemplate.NOT_EMPTY, R.string.err_name);
        validator.addValidation(this, R.id.til_email, Patterns.EMAIL_ADDRESS, R.string.err_email);
        validator.addValidation(this, R.id.til_password, "[a-zA-Z0-9]{6,}", R.string.err_password);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
