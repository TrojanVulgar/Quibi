package com.easyplex.ui.base;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import com.easyplex.ui.login.LoginActivity;
import com.easyplex.ui.manager.AdsManager;
import com.easyplex.ui.manager.SettingsManager;
import com.easyplex.ui.manager.StatusManager;
import com.easyplex.ui.manager.TokenManager;
import com.easyplex.ui.mylist.MyListFragment;
import com.easyplex.ui.settings.SettingsActivity;
import com.easyplex.R;
import com.easyplex.databinding.ActivityMainBinding;
import com.easyplex.util.Constants;
import com.easyplex.util.DialogHelper;
import com.easyplex.util.Tools;
import com.facebook.ads.AudienceNetworkAds;
import com.github.javiersantos.appupdater.AppUpdater;
import com.github.javiersantos.appupdater.enums.Display;
import com.github.javiersantos.appupdater.enums.UpdateFrom;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.messaging.FirebaseMessaging;
import javax.inject.Inject;
import dagger.android.AndroidInjection;

import static com.easyplex.util.Constants.SERVER_BASE_URL;
import static com.easyplex.util.Constants.SWITCH_PUSH_NOTIFICATION;


public class BaseActivity extends AppCompatActivity{


    private  String imdb;

    ActivityMainBinding mainBinding;

    @Inject
    TokenManager tokenManager;

    private String mMovieId;

    @Inject
    SettingsManager settingsManager;


    @Inject
    AdsManager adsManager;


    @Inject
    StatusManager statusManager;


    @Inject
    SharedPreferences sharedPreferences;

    @Inject
    SharedPreferences.Editor editor;

    @Inject ViewModelProvider.Factory viewModelFactory;

    FragmentManager manager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);

        super.onCreate(savedInstanceState);
        mainBinding = DataBindingUtil.setContentView(this,R.layout.activity_main);


        // Initialize the Audience Network SDK
        AudienceNetworkAds.initialize(this);


        // Initialize Admob Ads SDK.
        MobileAds.initialize(this, initializationStatus -> {
        });




        manager = getSupportFragmentManager();


        if (settingsManager.getSettings().getEnableCustomMessage() == 1) {

            DialogHelper.showCustomAlert(this,settingsManager.getSettings().getCustomMessage());

        }


        onLoadCheckVersion();

        notificationManager();

        onNavigationUI();

        onHideTaskBar();



        if(tokenManager.getToken() == null){
            startActivity(new Intent(this, LoginActivity.class));
            finish();

        }


    }



    private void onLoadCheckVersion() {



        // Check App Version
        AppUpdater appUpdater = new AppUpdater(this)
                .setDisplay(Display.DIALOG)
                .setUpdateFrom(UpdateFrom.JSON)
                .setUpdateJSON(String.valueOf(Uri.parse(SERVER_BASE_URL+"settings")))
                .setTitleOnUpdateAvailable(settingsManager.getSettings().getUpdateTitle())
                .setContentOnUpdateAvailable(settingsManager.getSettings().getReleaseNotes());
        appUpdater.start();

    }


    // Top level Firebase Cloud Messaging singleton that provides methods for subscribing or unsubscribing

    private void notificationManager() {

        if (sharedPreferences.getBoolean(SWITCH_PUSH_NOTIFICATION,true)){

            FirebaseMessaging.getInstance().subscribeToTopic("/topics/all");

        }else {

            FirebaseMessaging.getInstance().unsubscribeFromTopic("/topics/all");
        }
    }


    // Hide TaskBar
    private void onHideTaskBar() {

        //Hide TaskBar
        Tools.hideSystemPlayerUi(this,true,0);
        Tools.setSystemBarTransparent(this);

    }




    // NavigationUI to handle bottom navigation.
    // When a user selects a menu item,
    // the NavController calls onNavDestinationSelected()
    // and automatically updates the selected item in the bottom navigation bar.

    private void onNavigationUI() {

        NavigationUI.setupWithNavController(mainBinding.mainBottomNavView, Navigation.findNavController(this, R.id.main_nav_host_fragment));

        mainBinding.mainBottomNavView.setOnNavigationItemReselectedListener(item -> {

            // Avoid Creating Fragment on Item Reselected

        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);

    }




    @Override
    public void onBackPressed(){

        if (mainBinding.mainBottomNavView.getSelectedItemId() == R.id.navigation_home)
        {
            doExitApp();
        }
        else
        {
            mainBinding.mainBottomNavView.setSelectedItemId(R.id.navigation_home);
        }


    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.settings:
               startActivity(new Intent(this, SettingsActivity.class));


                return true;
            case R.id.myListFragment:


                MyListFragment myListFragment = new MyListFragment();
                manager.beginTransaction()
                        .replace(R.id.main_nav_host_fragment, myListFragment)
                        .addToBackStack("MyList")
                        .commit();

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }



    public void doExitApp() {

        Tools.doExitApp(this);
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        settingsManager.deleteSettings();
    }


}
