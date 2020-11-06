package com.easyplex.ui.splash;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import com.easyplex.R;
import com.easyplex.databinding.ActivitySplashBinding;
import com.easyplex.ui.login.LoginActivity;
import com.easyplex.ui.manager.AdsManager;
import com.easyplex.ui.manager.SettingsManager;
import com.easyplex.ui.manager.StatusManager;
import com.easyplex.ui.viewmodels.SettingsViewModel;
import com.easyplex.util.Constants;
import com.easyplex.util.Tools;
import javax.inject.Inject;
import dagger.android.AndroidInjection;



public class SplashActivity extends AppCompatActivity {

    ActivitySplashBinding binding;


    @Inject
    SettingsManager settingsManager;


    @Inject
    AdsManager adsManager;


    @Inject
    StatusManager statusManager;


    @Inject
    ViewModelProvider.Factory viewModelFactory;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);



        SettingsViewModel settingsViewModel = new ViewModelProvider(this, viewModelFactory).get(SettingsViewModel.class);


        binding = DataBindingUtil.setContentView(this,R.layout.activity_splash);

        settingsViewModel.getSettingsDetails();
        settingsViewModel.settingsMutableLiveData.observe(this, settings ->


         settingsManager.saveSettings(settings)


        );



        settingsViewModel.statusMutableLiveData.observe(this,status -> statusManager.saveSettings(status));


        settingsViewModel.adsMutableLiveData.observe(this, ads -> adsManager.saveSettings(ads));

        new Handler(Looper.myLooper()).postDelayed(hideUIAction, 2000);


        onHideTaskBar();
        onLoadLogo();
        onHideTaskBar();


    }



    // Lambda Runnable
    Runnable hideUIAction = () -> {

        Intent i = new Intent(SplashActivity.this, LoginActivity.class);
        startActivity(i);

        // close this activity
        finish();

    };



    // Hide TaskBar
    private void onHideTaskBar() {

        Tools.hideSystemPlayerUi(this,true,0);
    }



    // Load Logo
    private void onLoadLogo() {

        Tools.loadMiniLogo(binding.logoImageTop);


    }
}
