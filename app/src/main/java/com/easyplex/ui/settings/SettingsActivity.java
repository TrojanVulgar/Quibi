package com.easyplex.ui.settings;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.easyplex.R;
import com.easyplex.databinding.ActivitySettingBinding;
import com.easyplex.ui.download.DownloadList;
import com.easyplex.ui.login.LoginActivity;
import com.easyplex.ui.profile.EditProfileActivity;
import com.easyplex.ui.viewmodels.LoginViewModel;
import com.easyplex.ui.manager.AdsManager;
import com.easyplex.ui.manager.AuthManager;
import com.easyplex.ui.manager.SettingsManager;
import com.easyplex.ui.manager.TokenManager;
import com.easyplex.ui.viewmodels.MoviesListViewModel;
import com.easyplex.ui.plans.PlansAdapter;
import com.easyplex.ui.splash.SplashActivity;
import com.easyplex.ui.viewmodels.SettingsViewModel;
import com.easyplex.util.SpacingItemDecoration;
import com.easyplex.util.Tools;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;
import javax.inject.Inject;
import dagger.android.AndroidInjection;
import timber.log.Timber;

import static android.view.View.GONE;
import static com.easyplex.R.string.build_bersion;
import static com.easyplex.util.Constants.AUTO_PLAY;
import static com.easyplex.util.Constants.PREMUIM;
import static com.easyplex.util.Constants.SUBSCRIPTIONS;
import static com.easyplex.util.Constants.SUBS_SIZE;
import static com.easyplex.util.Constants.SWITCH_PUSH_NOTIFICATION;
import static com.easyplex.util.Constants.WIFI_CHECK;

public class SettingsActivity extends AppCompatActivity {


    ActivitySettingBinding binding;


    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private MoviesListViewModel moviesListViewModel;


    @Inject
    SharedPreferences.Editor sharedPreferencesEditor;

    @Inject
    SharedPreferences sharedPreferences;

    @Inject
    TokenManager tokenManager;

    @Inject
    SettingsManager settingsManager;

    @Inject
    AdsManager adsManager;

    @Inject
    AuthManager authManager;


    private LoginViewModel loginViewModel;

    private SettingsViewModel settingsViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        AndroidInjection.inject(this);

        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_setting);


        loginViewModel = new ViewModelProvider(this, viewModelFactory).get(LoginViewModel.class);

        settingsViewModel = new ViewModelProvider(this, viewModelFactory).get(SettingsViewModel.class);

        moviesListViewModel = new ViewModelProvider(this, viewModelFactory).get(MoviesListViewModel.class);

        Tools.hideSystemPlayerUi(this, true, 0);

        Tools.setSystemBarTransparent(this);

        onLoadAppLogo();
        setButtonsUtilities();
        onLoadAppBar();
        onCheckAuth();
        onLogout();
        onLoadAboutUs();
        onLoadPrivacyPolicy();
        onClearCache();
        onLoadEditProfile();
        onClearRoomDatabase(moviesListViewModel);
        onClearWatchHistory(moviesListViewModel);
        try {
            onCancelSubscription();
        } catch (ParseException e) {
            e.printStackTrace();
        }


        settingsViewModel.getPlans();

        binding.subcribeButton.setOnClickListener(v -> binding.viewPlans.setVisibility(View.VISIBLE));
        binding.closePlans.setOnClickListener(v -> binding.viewPlans.setVisibility(GONE));


        binding.btnLogin.setOnClickListener(v -> startActivity(new Intent(SettingsActivity.this, LoginActivity.class)));



        settingsViewModel.plansMutableLiveData.observe(this, plans -> {

            PlansAdapter plansAdapter = new PlansAdapter();
            binding.recyclerViewPlans.setAdapter(plansAdapter);
            binding.recyclerViewPlans.setLayoutManager(new LinearLayoutManager(this));
            binding.recyclerViewPlans.addItemDecoration(new SpacingItemDecoration(1, Tools.dpToPx(this, 0), true));

            binding.recyclerViewPlans.setHasFixedSize(true);

            plansAdapter.addCasts(plans.getPlans(),settingsManager);


        });




        if (tokenManager.getToken().getAccessToken() == null) {
            binding.btnLogin.setVisibility(View.VISIBLE);

        }else {

            binding.btnLogin.setVisibility(GONE);
        }




    }

    private void onClearWatchHistory(MoviesListViewModel moviesListViewModel) {

        binding.clearMyWatchHistory.setOnClickListener(v -> {

            final Dialog dialog = new Dialog(SettingsActivity.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.clear_mylist);
            dialog.setCancelable(true);
            Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(dialog.getWindow().getAttributes());
            lp.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;


            dialog.findViewById(R.id.bt_getcode).setOnClickListener(x -> {

                moviesListViewModel.deleteHistory();

                Toast.makeText(this, "History has been cleared !", Toast.LENGTH_SHORT).show();

                dialog.dismiss();


            });

            dialog.findViewById(R.id.bt_close).setOnClickListener(x -> dialog.dismiss());


            dialog.show();
            dialog.getWindow().setAttributes(lp);

        });

    }

    private void onCancelSubscription() throws ParseException {


        long millis=System.currentTimeMillis();
        java.sql.Date date=new java.sql.Date(millis);


        if (authManager.getUserInfo().getExpiredIn() != null && !authManager.getUserInfo().getExpiredIn().trim().isEmpty()) {
            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
            try {
                Date releaseDate1 = sdf1.parse(authManager.getUserInfo().getExpiredIn());
                Date releaseDate2 = sdf1.parse(String.valueOf(date));


                if (releaseDate1.equals(releaseDate2)) {


                    loginViewModel.cancelAuthSubscription();
                    loginViewModel.authCancelPlanMutableLiveData.observe(this, auth -> {

                     if (auth !=null) {

                        Toast.makeText(this, SUBSCRIPTIONS, Toast.LENGTH_SHORT).show();

                        }

                    });


                    loginViewModel.cancelAuthSubscriptionPaypal();
                    loginViewModel.authCancelPaypalMutableLiveData.observe(this, auth -> {

                        if (auth !=null) {

                            Toast.makeText(this, SUBSCRIPTIONS, Toast.LENGTH_SHORT).show();

                        }

                    });
                }


            } catch (ParseException e) {

                Timber.d("%s", Arrays.toString(e.getStackTrace()));

            }
        }

    }


    private void onLoadEditProfile() {

        binding.btnEditProfile.setOnClickListener(v -> startActivity(new Intent(SettingsActivity.this, EditProfileActivity.class)));
    }


    private void onClearRoomDatabase(MoviesListViewModel moviesListViewModel) {

        binding.ClearMyList.setOnClickListener(v -> {

            final Dialog dialog = new Dialog(SettingsActivity.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.clear_mylist);
            dialog.setCancelable(true);
            Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(dialog.getWindow().getAttributes());
            lp.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;


            dialog.findViewById(R.id.bt_getcode).setOnClickListener(x -> {

                moviesListViewModel.deleteAllMovies();

                Toast.makeText(this, "My List has been cleared !", Toast.LENGTH_SHORT).show();

                dialog.dismiss();


            });

            dialog.findViewById(R.id.bt_close).setOnClickListener(x -> dialog.dismiss());


            dialog.show();
            dialog.getWindow().setAttributes(lp);

        });


    }

    public void onClearCache() {

        binding.linearLayoutCleaCache.setOnClickListener(v -> {


            final Dialog dialog = new Dialog(this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.clear_cache);
            dialog.setCancelable(true);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(dialog.getWindow().getAttributes());
            lp.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;


            dialog.findViewById(R.id.bt_getcode).setOnClickListener(x -> {
                deleteCache(this);

                Toast.makeText(this, "The App cache has been cleared !", Toast.LENGTH_SHORT).show();


                dialog.dismiss();

            });

            dialog.findViewById(R.id.bt_close).setOnClickListener(x -> dialog.dismiss());


            dialog.show();
            dialog.getWindow().setAttributes(lp);


        });


    }

    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteFile(dir);
        } catch (Exception e) {

            Timber.d("Error Deleting : %s", e.getMessage());
        }
    }

    public static boolean deleteFile(File file) {
        boolean deletedAll = true;
        if (file != null) {
            if (file.isDirectory()) {
                String[] children = file.list();
                for (String child : children) {
                    deletedAll = deleteFile(new File(file, child)) && deletedAll;
                }
            } else {
                deletedAll = file.delete();
            }
        }

        return deletedAll;
    }


    private void onLoadPrivacyPolicy() {


        binding.privacyPolicy.setOnClickListener(v -> {

            final Dialog dialog = new Dialog(this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
            dialog.setContentView(R.layout.dialog_gdpr_basic);
            dialog.setCancelable(true);
            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(dialog.getWindow().getAttributes());
            lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
            lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;

            TextView reportMovieName = dialog.findViewById(R.id.tv_content);
            reportMovieName.setText(settingsManager.getSettings().getPrivacyPolicy());

            dialog.findViewById(R.id.bt_accept).setOnClickListener(v1 -> dialog.dismiss());

            dialog.findViewById(R.id.bt_decline).setOnClickListener(v12 -> dialog.dismiss());


            dialog.show();
            dialog.getWindow().setAttributes(lp);


        });


    }

    private void onLoadAboutUs() {


        // About Us - EasyPlex
        binding.aboutus.setOnClickListener(v -> {
            final Dialog dialog = new Dialog(this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
            dialog.setContentView(R.layout.dialog_about);
            dialog.setCancelable(true);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


            ImageView imageView = dialog.findViewById(R.id.logo_aboutus);

            Tools.loadMainLogo(imageView);

            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(dialog.getWindow().getAttributes());
            lp.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;


            TextView tvVersion = dialog.findViewById(R.id.tv_version);

            tvVersion.setText(String.format("%d%s", build_bersion, settingsManager.getSettings().getLatestVersion()));


            dialog.findViewById(R.id.bt_getcode).setOnClickListener(v15 -> {
                if (settingsManager.getSettings().getAppUrlAndroid().isEmpty()) {


                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://google.com")));

                } else {

                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(settingsManager.getSettings().getAppUrlAndroid())));

                }

            });

            dialog.findViewById(R.id.bt_close).setOnClickListener(v14 -> dialog.dismiss());

            dialog.findViewById(R.id.app_url).setOnClickListener(v13 -> {

                if (settingsManager.getSettings().getAppUrlAndroid().isEmpty()) {

                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("")));

                } else {

                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(settingsManager.getSettings().getAppUrlAndroid())));

                }

            });

            dialog.show();
            dialog.getWindow().setAttributes(lp);

        });



        if (tokenManager.getToken().getAccessToken() != null) {


            binding.btnEditProfile.setVisibility(View.VISIBLE);
            binding.logout.setVisibility(View.VISIBLE);

        }else {

            binding.btnEditProfile.setVisibility(GONE);
            binding.logout.setVisibility(View.GONE);
            binding.subcribeButton.setVisibility(GONE);

        }


    }

    private void onLogout() {

        binding.logout.setOnClickListener(v -> {

            tokenManager.deleteToken();
            authManager.deleteAuth();
            settingsManager.deleteSettings();
            adsManager.deleteAds();
            moviesListViewModel.deleteHistory();
            deleteCache(this);
            moviesListViewModel.deleteAllMovies();
            startActivity(new Intent(this, SplashActivity.class));
            finish();

        });

    }

    private void onCheckAuth() {


        loginViewModel.getAuthDetails();
        loginViewModel.authDetailMutableLiveData.observe(this, auth -> {

            if (auth !=null) {

                binding.authName.setVisibility(View.VISIBLE);
                binding.authEmail.setVisibility(View.VISIBLE);
                binding.authName.setText(auth.getName());
                binding.authEmail.setText(auth.getEmail());
                binding.btnLogin.setVisibility(GONE);

                if (auth.getPremuim() == 1) {

                    binding.btnPremuim.setVisibility(View.VISIBLE);
                    sharedPreferencesEditor.putInt(PREMUIM, 1).apply();
                    binding.btnPremuim.setVisibility(View.VISIBLE);
                    binding.btnPremuim.setText(auth.getPackName());
                    binding.membershipExpireIn.setVisibility(View.VISIBLE);

                    if (auth.getExpiredIn() != null && !auth.getExpiredIn().trim().isEmpty()) {
                        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
                        try {
                            Date releaseDate = sdf1.parse(auth.getExpiredIn());
                            binding.membershipExpireIn.setText("Valid until : "+sdf1.format(releaseDate));
                        } catch (ParseException e) {

                            Timber.d("%s", Arrays.toString(e.getStackTrace()));

                        }
                    } else {
                        binding.membershipExpireIn.setText("");
                    }


                    binding.subcribeButton.setVisibility(GONE);
                    binding.cancelSubcriptionButton.setVisibility(View.VISIBLE);
                    binding.cancelSubcriptionButton.setOnClickListener(v -> {


                        final Dialog dialog = new Dialog(SettingsActivity.this);
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setContentView(R.layout.dialog_confirm_cancel_subscription);
                        dialog.setCancelable(true);
                        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                        lp.copyFrom(dialog.getWindow().getAttributes());
                        lp.width = ViewGroup.LayoutParams.WRAP_CONTENT;
                        lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;


                        dialog.findViewById(R.id.bt_getcode).setOnClickListener(x -> {


                            loginViewModel.cancelAuthSubscription();
                            loginViewModel.authCancelPlanMutableLiveData.observe(SettingsActivity.this, auth1 -> {

                                if (auth1 !=null) {

                                    Toast.makeText(SettingsActivity.this, "You Subscription has ended !", Toast.LENGTH_SHORT).show();

                                }

                            });


                            loginViewModel.cancelAuthSubscriptionPaypal();
                            loginViewModel.authCancelPaypalMutableLiveData.observe(SettingsActivity.this, auth1 -> {

                                if (auth1 !=null) {

                                    Toast.makeText(SettingsActivity.this, "You Subscription has ended !", Toast.LENGTH_SHORT).show();

                                }

                            });



                            dialog.dismiss();

                        });

                        dialog.findViewById(R.id.bt_close).setOnClickListener(x -> dialog.dismiss());


                        dialog.show();
                        dialog.getWindow().setAttributes(lp);


                    });

                } else {

                    binding.btnPremuim.setVisibility(GONE);
                    sharedPreferencesEditor.putInt(PREMUIM, 0).apply();
                    binding.authFree.setVisibility(View.VISIBLE);
                    binding.subcribeButton.setVisibility(View.VISIBLE);

                }

            }else {

                binding.authEmail.setVisibility(GONE);
                binding.authEmail.setVisibility(View.GONE);
                binding.btnLogin.setVisibility(View.VISIBLE);

            }



        });


    }


    // Load AppBar
    private void onLoadAppBar() {

        Tools.loadAppBar(binding.scrollView, binding.toolbar);

    }


    // Load App Logo
    private void onLoadAppLogo() {

        Tools.loadMiniLogo(binding.logoImageTop);

    }



    private void setButtonsUtilities() {

        onLoadwifiSwitch();
        onLoadNotificationPushSwitch();
        setAutoPlaySwitch();

        binding.currentSize.setText(String.format("current size : %s", sharedPreferences.getString(SUBS_SIZE, "16f")));

        binding.substitleSize.setOnClickListener(v -> {

            ArrayList<String> fontSize = new ArrayList<>();


            fontSize.add("10f");
            fontSize.add("12f");
            fontSize.add("14f");
            fontSize.add("16f");
            fontSize.add("20f");
            fontSize.add("24f");
            fontSize.add("30f");

            String[] charSequenceSubsSize = new String[fontSize.size()];
            for (int i = 0; i < fontSize.size(); i++) {
                charSequenceSubsSize[i] = String.valueOf(fontSize.get(i));

            }

            final AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyAlertDialogTheme);
            builder.setTitle("Font Size..");
            builder.setCancelable(true);
            builder.setItems(charSequenceSubsSize, (dialogInterface, wich) -> {

                sharedPreferencesEditor.putString(SUBS_SIZE, fontSize.get(wich)).apply();
                sharedPreferences.getString(SUBS_SIZE, "16f");
                binding.currentSize.setText(String.format("Current Size : %s", sharedPreferences.getString(SUBS_SIZE, "16f")));
                dialogInterface.dismiss();


            });

            builder.show();

        });



        binding.downloadList.setOnClickListener(v -> startActivity(new Intent(SettingsActivity.this, DownloadList.class)));


    }

    private void setAutoPlaySwitch() {

        // Detect AutoPlay ON/OFF Button switch - EasyPlex
        if (!sharedPreferences.getBoolean(AUTO_PLAY, true)) {
            binding.autoplaySwitch.setChecked(false);
        }

        binding.autoplaySwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {

                sharedPreferencesEditor.putBoolean(AUTO_PLAY, true).apply();


            } else {

                sharedPreferencesEditor.putBoolean(AUTO_PLAY, false).apply();

            }

        });

    }

    private void onLoadNotificationPushSwitch() {

        // Detect Notification ON/OFF Button switch - EasyPlex
        if (!sharedPreferences.getBoolean(SWITCH_PUSH_NOTIFICATION, true)) {

            binding.switchPushNotification.setChecked(false);
        }


        binding.switchPushNotification.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {

                sharedPreferencesEditor.putBoolean(SWITCH_PUSH_NOTIFICATION, true).apply();

                FirebaseMessaging.getInstance().subscribeToTopic("/topics/all");

            } else {

                sharedPreferencesEditor.putBoolean(SWITCH_PUSH_NOTIFICATION, false).apply();

                FirebaseMessaging.getInstance().unsubscribeFromTopic("/topics/all");

            }

        });

    }

    private void onLoadwifiSwitch() {

        // Detect Wifi-Only Button switch - EasyPlex
        if (!sharedPreferences.getBoolean(WIFI_CHECK, true)) {

            binding.wifiSwitch.setChecked(false);

        }

        binding.wifiSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {

                sharedPreferencesEditor.putBoolean(WIFI_CHECK, true).apply();

            } else {

                sharedPreferencesEditor.putBoolean(WIFI_CHECK, false).apply();
            }

        });

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        binding = null;

    }
}
