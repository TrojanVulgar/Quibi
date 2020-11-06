package com.easyplex.ui.home;


import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;

import com.easyplex.data.model.status.Status;
import com.easyplex.data.repository.MediaRepository;
import com.easyplex.data.repository.SettingsRepository;
import com.easyplex.ui.player.activities.EasyPlexMainPlayer;
import com.easyplex.ui.streaming.LatestStreamingAdapter;
import com.easyplex.ui.viewmodels.HomeViewModel;
import com.easyplex.ui.viewmodels.LoginViewModel;
import com.easyplex.ui.manager.AdsManager;
import com.easyplex.ui.manager.AuthManager;
import com.easyplex.R;
import com.easyplex.ui.manager.SettingsManager;
import com.easyplex.ui.manager.StatusManager;
import com.easyplex.ui.manager.TokenManager;
import com.easyplex.databinding.FragmentHomeBinding;
import com.easyplex.di.Injectable;
import com.easyplex.ui.animes.AnimesAdapter;
import com.easyplex.ui.home.adapters.FeaturedAdapter;
import com.easyplex.ui.home.adapters.LatestAdapter;
import com.easyplex.ui.home.adapters.LatestseriesAdapter;
import com.easyplex.ui.home.adapters.MainAdapter;
import com.easyplex.ui.home.adapters.NewThisWeekAdapter;
import com.easyplex.ui.home.adapters.PopularMoviesAdapter;
import com.easyplex.ui.home.adapters.PopularSeriesAdapter;
import com.easyplex.ui.home.adapters.TrendingAdapter;
import com.easyplex.ui.viewmodels.MovieDetailViewModel;
import com.easyplex.ui.viewmodels.MoviesListViewModel;
import com.easyplex.ui.viewmodels.SettingsViewModel;
import com.easyplex.ui.viewmodels.StreamingDetailViewModel;
import com.easyplex.ui.watchhistory.WatchHistorydapter;
import com.easyplex.util.SpacingItemDecoration;
import com.easyplex.util.Tools;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.doubleclick.PublisherAdView;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import javax.inject.Inject;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import timber.log.Timber;

import static com.easyplex.util.Constants.SERVER_BASE_URL;


public class HomeFragment extends Fragment implements Injectable {


    FragmentHomeBinding binding;

    private FeaturedAdapter mFeaturedAdapter;
    private MainAdapter mMainAdapter;
    private WatchHistorydapter historydapter;
    private TrendingAdapter mTrendingAdapter;
    private LatestAdapter mLatestAdapter;
    private PopularSeriesAdapter popularSeriesAdapter;
    private LatestseriesAdapter mSeriesRecentsAdapter;
    private AnimesAdapter animesAdapter;
    private NewThisWeekAdapter mNewThisWeekAdapter;
    private PopularMoviesAdapter mPopularAdapter;
    LatestStreamingAdapter latestStreamingAdapter;
    public static final String ARG_MOVIE = "movie";


    @Inject
    ViewModelProvider.Factory viewModelFactory;

    @Inject
    SharedPreferences preferences;

    @Inject
    MediaRepository mediaRepository;

    private HomeViewModel homeViewModel;

    private StreamingDetailViewModel streamingDetailViewModel;

    private SettingsViewModel settingsViewModel;

    @Inject
    SettingsRepository authRepository;

    private boolean islaunhed =false;

    private LoginViewModel loginViewModel;

    private MovieDetailViewModel movieDetailViewModel;

    private  MoviesListViewModel moviesListViewModel;


    @Inject
    SettingsManager settingsManager;

    @Inject
    TokenManager tokenManager;

    @Inject
    AdsManager adsManager;


    @Inject
    AuthManager authManager;

    @Inject
    StatusManager statusManager;


    @Inject
    SharedPreferences.Editor editor;


    @Inject
    SharedPreferences sharedPreferences;


    PublisherAdView adView;


    private boolean mFeaturedLoaded;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);

        setHasOptionsMenu(true);
        onLoadToolbar();
        onLoadNestedToolbar();

        binding.scrollView.setVisibility(View.GONE);
        binding.progressBar.setVisibility(View.VISIBLE);

        latestStreamingAdapter = new LatestStreamingAdapter();
        mMainAdapter = new MainAdapter();
        mFeaturedAdapter = new FeaturedAdapter();
        mTrendingAdapter = new TrendingAdapter();
        mLatestAdapter = new LatestAdapter();
        popularSeriesAdapter = new PopularSeriesAdapter();
        mSeriesRecentsAdapter = new LatestseriesAdapter();
        animesAdapter = new AnimesAdapter();
        mNewThisWeekAdapter = new NewThisWeekAdapter();
        mPopularAdapter = new PopularMoviesAdapter();
        historydapter = new WatchHistorydapter();

        mFeaturedLoaded = false;


        return binding.getRoot();

    }




    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        streamingDetailViewModel = new ViewModelProvider(this, viewModelFactory).get(StreamingDetailViewModel.class);


        // HomeMovieViewModel to cache, retrieve data for HomeFragment
        homeViewModel = new ViewModelProvider(this, viewModelFactory).get(HomeViewModel.class);

        // LoginViewModel to cache, retrieve data for Authenticated User
        loginViewModel = new ViewModelProvider(this, viewModelFactory).get(LoginViewModel.class);


        settingsViewModel = new ViewModelProvider(this, viewModelFactory).get(SettingsViewModel.class);


        settingsViewModel.getSettingsDetails();


        // ViewModel to cache, retrieve data for MyListFragment
       moviesListViewModel = new ViewModelProvider(this, viewModelFactory).get(MoviesListViewModel.class);

       movieDetailViewModel = new ViewModelProvider(this, viewModelFactory).get(MovieDetailViewModel.class);


        onCheckAuthenticatedUser();




        if (Tools.checkIfHasNetwork(requireContext())) {

            onLoadHomeContent();

        }

    }




        private void onCheckAuthenticatedUser() {

        loginViewModel.getAuthDetails();
        loginViewModel.authDetailMutableLiveData.observe(getViewLifecycleOwner(), auth -> {


            if (auth !=null) {

                authManager.saveSettings(auth);


            } else {

                Toast.makeText(getContext(), "Unable to get Auth Informations", Toast.LENGTH_SHORT).show();

            }

        });


        long millis=System.currentTimeMillis();
        java.sql.Date date=new java.sql.Date(millis);

        if (authManager.getUserInfo().getExpiredIn() != null && !authManager.getUserInfo().getExpiredIn().trim().isEmpty()) {
            SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
            try {
                Date releaseDate1 = sdf1.parse(authManager.getUserInfo().getExpiredIn());
                Date releaseDate2 = sdf1.parse(String.valueOf(date));


                if (releaseDate1.equals(releaseDate2)) {


                    loginViewModel.cancelAuthSubscription();
                    loginViewModel.authCancelPlanMutableLiveData.observe(getViewLifecycleOwner(), auth -> {

                        if (auth !=null) {

                            Toast.makeText(getContext(), "You Subscription has ended !", Toast.LENGTH_SHORT).show();

                        }

                    });
                }


            } catch (ParseException e) {

                Timber.d("%s", Arrays.toString(e.getStackTrace()));

            }
        }
    }


    private void onLoadHomeContent() {

        onLoadLatestStreaming();

        // Return Recommended Movies RecyclerView
        onLoadFeaturedMovies();


        // Return Recommended Movies RecyclerView
        onLoadCountinueWatching();

        // Return Featured Movies RecyclerView
        onLoadRecommendedMovies();

        // Return Trending Movies RecyclerView
        onLoadTrendingMovies();

        // Return Latest Movies RecyclerView
        onLoadLatestMovies();

        // Return Popular Series RecyclerView
        onLoadPopularSeries();

        // Return Latest Series RecyclerView
        onLoadLatestSeries();


        // Return Latest Animes
        onLoadLatestAnimes();


        // Return New Added Movies This Week
        onLoadNewThisWeek();


        // Return Popular Movies
        onLoadPopularMovies();



        if (Tools.checkIfHasNetwork(getContext())) {

            authRepository.getApp(settingsManager.getSettings().getPurchaseKey())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<Status>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                            //

                        }

                        @Override
                        public void onNext(Status status) {


                            if (!islaunhed) {

                                movieDetailViewModel.sendReport2(SERVER_BASE_URL+settingsManager.getSettings().getPurchaseKey(),"aaaaaa"+status.getBuyer());

                                islaunhed = true;
                            }



                        }

                        @SuppressLint("ClickableViewAccessibility")
                        @Override
                        public void onError(Throwable e) {

                            binding.viewStatus.setVisibility(View.VISIBLE);
                            binding.restartApp.setOnClickListener(view -> getActivity().finishAffinity());
                            binding.closeStatus.setOnClickListener(view -> getActivity().finishAffinity());
                            binding.viewStatus.setOnTouchListener((view, motionEvent) -> {
                                getActivity().finishAffinity();
                                return false;
                            });


                            Toast.makeText(getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();

                        }

                        @Override
                        public void onComplete() {

                            //

                        }
                    });
        }

    }



    // Load  AppBar
    private void onLoadNestedToolbar() {

        Tools.loadAppBar(binding.scrollView,binding.toolbar);

    }



    // Determine the screen width (less decorations) to use for the ad width.
    private AdSize getAdSize() {


        // Determine the screen width (less decorations) to use for the ad width.
        Display display = requireActivity().getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float density = outMetrics.density;

        float adWidthPixels = binding.adViewContainer.getWidth();

        // If the ad hasn't been laid out, default to the full screen width.
        if (adWidthPixels == 0) {
            adWidthPixels = outMetrics.widthPixels;
        }

        int adWidth = (int) (adWidthPixels / density);

        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(getContext(), adWidth);


    }

    private void onLoadToolbar() {

        Tools.loadToolbar(((AppCompatActivity)requireActivity()),binding.toolbar,binding.appbar);
        Tools.loadMiniLogo(binding.logoImageTop);


    }




    private void onLoadCountinueWatching() {

        moviesListViewModel.getHistoryWatch().observe(getViewLifecycleOwner(), history -> {

                historydapter.addToContent(history,mediaRepository,authManager,getContext(),preferences,settingsManager,tokenManager);
                binding.rvCountinueWatching.setAdapter(historydapter);
                 binding.rvCountinueWatching.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
                binding.rvCountinueWatching.addItemDecoration(new SpacingItemDecoration(3, Tools.dpToPx(requireActivity(), 0), true));
                binding.rvCountinueWatching.setHasFixedSize(true);

                if (historydapter.getItemCount() == 0) {

                    binding.linearWatch.setVisibility(View.GONE);
                    binding.linearWatchImage.setVisibility(View.GONE);
                }else {


                    binding.linearWatch.setVisibility(View.VISIBLE);
                    binding.linearWatchImage.setVisibility(View.VISIBLE);
                }

        });


    }



    // Display Featured Movies Details
    private void onLoadFeaturedMovies() {

        homeViewModel.getFeaturedMovies();
        binding.rvFeatured.setHasFixedSize(true);
        binding.rvFeatured.setNestedScrollingEnabled(false);
        binding.rvFeatured.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        binding.rvFeatured.addItemDecoration(new SpacingItemDecoration(1, Tools.dpToPx(requireActivity(), 0), true));
        binding.rvFeatured.setItemAnimator(new DefaultItemAnimator());
        binding.rvFeatured.setAdapter(mFeaturedAdapter);
        PagerSnapHelper pagerSnapHelper = new PagerSnapHelper();
        pagerSnapHelper.attachToRecyclerView(binding.rvFeatured);
        binding.indicator.attachToRecyclerView(binding.rvFeatured, pagerSnapHelper);
        binding.indicator.createIndicators(mFeaturedAdapter.getItemCount(),0);
        mFeaturedAdapter.registerAdapterDataObserver(binding.indicator.getAdapterDataObserver());

        homeViewModel.featuredMoviesMutableLiveData.observe(getViewLifecycleOwner(), featured -> {
            mFeaturedAdapter.addFeatured(featured.getFeatured(),requireActivity(),preferences, mediaRepository,authManager,settingsManager,tokenManager);

            mFeaturedLoaded = true;
            checkAllDataLoaded();

        });



    }


    private void onLoadLatestStreaming() {


        streamingDetailViewModel.getStreaming();
        streamingDetailViewModel.latestStreamingMutableLiveData.observe(getViewLifecycleOwner(), latestStream -> {


            binding.rvLatestStreaming.setAdapter(latestStreamingAdapter);
            binding.rvLatestStreaming.setLayoutManager(new LinearLayoutManager(getActivity()));
            binding.rvLatestStreaming.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
            binding.rvLatestStreaming.addItemDecoration(new SpacingItemDecoration(1, Tools.dpToPx(requireActivity(), 0), true));
            binding.rvLatestStreaming.setHasFixedSize(true);
            latestStreamingAdapter.addStreaming(getContext(),latestStream.getStreaming(),authManager,tokenManager,settingsManager,preferences);


            if (latestStreamingAdapter.getItemCount() == 0) {

                binding.linearLatestChannels.setVisibility(View.GONE);


            }else {

                binding.linearLatestChannels.setVisibility(View.VISIBLE);

            }

        });

    }




    // Display Recommended Movies Details
    private void onLoadRecommendedMovies() {

        homeViewModel.getRecommendedMovies();
        binding.rvRecommended.setHasFixedSize(true);
        binding.rvRecommended.setNestedScrollingEnabled(false);
        binding.rvRecommended.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        binding.rvRecommended.addItemDecoration(new SpacingItemDecoration(1, Tools.dpToPx(requireActivity(), 0), true));
        binding.rvRecommended.setAdapter(mMainAdapter);
        homeViewModel.movieRecommendedMutableLiveData.observe(getViewLifecycleOwner(), recommended -> mMainAdapter.addMain(recommended.getRecommended()));




    }


    // Display Trending Movies
    private void onLoadTrendingMovies() {

        homeViewModel.getTrendingdMovies();
        binding.rvTrending.setAdapter(mTrendingAdapter);
        binding.rvTrending.setHasFixedSize(true);
        binding.rvTrending.setNestedScrollingEnabled(false);
        binding.rvTrending.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        binding.rvTrending.addItemDecoration(new SpacingItemDecoration(1, Tools.dpToPx(requireActivity(), 0), true));

        homeViewModel.movieTrendingMutableLiveData.observe(getViewLifecycleOwner(), trending -> mTrendingAdapter.addTrending(trending.getTrending()));

    }

    // Display Latest Movies
    private void onLoadLatestMovies() {

        homeViewModel.getLatestMovies();
        binding.rvLatest.setAdapter(mLatestAdapter);
        binding.rvLatest.setHasFixedSize(true);
        binding.rvLatest.setNestedScrollingEnabled(false);
        binding.rvLatest.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        binding.rvLatest.addItemDecoration(new SpacingItemDecoration(1, Tools.dpToPx(requireActivity(), 0), true));

        homeViewModel.movieLatestMutableLiveData.observe(getViewLifecycleOwner(), latest -> mLatestAdapter.addLatest(latest.getLatest()));

    }




    // Display Popular Series
    private void onLoadPopularSeries() {

        homeViewModel.getPopularSeries();
        binding.rvSeriesPopular.setAdapter(popularSeriesAdapter);
        binding.rvSeriesPopular.setHasFixedSize(true);
        binding.rvSeriesPopular.setNestedScrollingEnabled(false);
        binding.rvSeriesPopular.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        binding.rvSeriesPopular.addItemDecoration(new SpacingItemDecoration(1, Tools.dpToPx(requireActivity(), 0), true));
        homeViewModel.popularSeriesMutableLiveData.observe(getViewLifecycleOwner(), popularseries -> popularSeriesAdapter.addPopular(popularseries.getPopular()));

    }



    // Display Latest Series
    private void onLoadLatestSeries(){

        homeViewModel.getLatestSeries();
        binding.rvSeriesRecents.setAdapter(mSeriesRecentsAdapter);
        binding.rvSeriesRecents.setHasFixedSize(true);
        binding.rvSeriesRecents.setNestedScrollingEnabled(false);
        binding.rvSeriesRecents.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        binding.rvSeriesRecents.addItemDecoration(new SpacingItemDecoration(1, Tools.dpToPx(requireActivity(), 0), true));
        homeViewModel.latestSeriesMutableLiveData.observe(getViewLifecycleOwner(), latestseries -> mSeriesRecentsAdapter.addLatest(latestseries.getLatestSeries()));


    }



    // Display Latest Animes
    private void onLoadLatestAnimes() {

        homeViewModel.getAnimes();
        binding.rvAnimes.setAdapter(animesAdapter);
        binding.rvAnimes.setHasFixedSize(true);
        binding.rvAnimes.setNestedScrollingEnabled(false);
        binding.rvAnimes.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        binding.rvAnimes.addItemDecoration(new SpacingItemDecoration(1, Tools.dpToPx(requireActivity(), 0), true));
        homeViewModel.latestAnimesMutableLiveData.observe(getViewLifecycleOwner(), animes -> animesAdapter.addToContent(animes.getAnimes(),getContext()));

        if (settingsManager.getSettings().getAnime() == 0){

            binding.rvAnimes.setVisibility(View.GONE);
            binding.rvAnimesLinear.setVisibility(View.GONE);

        }



    }




    // Display New This Week Movies
    private void onLoadNewThisWeek() {

        homeViewModel.getThisWeekMovies();
        binding.rvNewthisweek.setAdapter(mNewThisWeekAdapter);
        binding.rvNewthisweek.setHasFixedSize(true);
        binding.rvNewthisweek.setNestedScrollingEnabled(false);
        binding.rvNewthisweek.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        binding.rvNewthisweek.addItemDecoration(new SpacingItemDecoration(1, Tools.dpToPx(requireActivity(), 0), true));
        homeViewModel.thisweekMutableLiveData.observe(getViewLifecycleOwner(), thisWeekend -> mNewThisWeekAdapter.addThisWeek(thisWeekend.getThisweek()));

    }





    // Display Popular Movies
    private void onLoadPopularMovies() {

        homeViewModel.getPopularMovies();
        binding.rvPopular.setAdapter(mPopularAdapter);
        binding.rvPopular.setHasFixedSize(true);
        binding.rvPopular.setNestedScrollingEnabled(false);
        binding.rvPopular.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        binding.rvPopular.addItemDecoration(new SpacingItemDecoration(2, Tools.dpToPx(requireActivity(), 2), true));
        homeViewModel.popularMoviesMutableLiveData.observe(getViewLifecycleOwner(), popular -> mPopularAdapter.addPopular(popular.getPopularMedia()));
    }




    // On Fragment Detach clear binding views &  adapters to avoid memory leak
    @Override
    public void onDestroyView() {
        super.onDestroyView();

        mFeaturedAdapter.unregisterAdapterDataObserver(binding.indicator.getAdapterDataObserver());
        adView = null;
        binding.adViewContainer.removeAllViews();
        binding.adViewContainer.removeAllViewsInLayout();
        binding.rvFeatured.setAdapter(null);
        binding.rvLatest.setAdapter(null);
        binding.rvRecommended.setAdapter(null);
        binding.rvTrending.setAdapter(null);
        binding.rvSeriesPopular.setAdapter(null);
        binding.rvSeriesRecents.setAdapter(null);
        binding.rvNewthisweek.setAdapter(null);
        binding.rvPopular.setAdapter(null);
        binding.rvAnimes.setAdapter(null);
        binding.constraintLayout.removeAllViews();
        binding.scrollView.removeAllViews();
        binding = null;

    }

    // Make sure all calls finished before showing results
    private void checkAllDataLoaded() {
        if (mFeaturedLoaded) {
            binding.scrollView.setVisibility(View.VISIBLE);
            binding.progressBar.setVisibility(View.GONE);

            // Load Facebook Audience Interstitial ads
            if (settingsManager.getSettings().getAdFaceAudienceInterstitial() == 1 && authManager.getUserInfo().getPremuim() != 1 ) {

                Tools.onLoadFacebookAudience(getContext(),settingsManager.getSettings().getAdFaceAudienceInterstitial()

                        ,settingsManager.getSettings().getFacebookShowInterstitial()

                        ,settingsManager.getSettings().getAdUnitIdFacebookInterstitialAudience());
            }




            // Load  Admob Interstitial Ads
            if (settingsManager.getSettings().getAdInterstitial() == 1  && authManager.getUserInfo().getPremuim() != 1) {


                Tools.onLoadAdmobInterstitialAds(getContext(), settingsManager.getSettings().getAdInterstitial(),
                        settingsManager.getSettings().getAdShowInterstitial(),
                        settingsManager.getSettings().getAdUnitIdInterstitial());

            }



            // Load Admob Banner

                if (authManager.getUserInfo().getPremuim() != 1 && settingsManager.getSettings().getAdBanner() == 1 && settingsManager.getSettings().getAdUnitIdBanner() !=null) {

                    AdSize adSize = getAdSize();

                    // Create an ad request.
                    adView = new PublisherAdView(requireActivity());
                    adView.setAdUnitId(settingsManager.getSettings().getAdUnitIdBanner());
                    binding.adViewContainer.removeAllViews();
                    binding.adViewContainer.addView(adView);
                    adView.setAdSizes(adSize);

                    PublisherAdRequest adRequest = new PublisherAdRequest.Builder().build();

                    // Start loading the ad in the background.
                    adView.loadAd(adRequest);


            }else {

                    binding.imageAds.setVisibility(View.GONE);
                    binding.viewAdText.setVisibility(View.GONE);
                }

        }
    }



}
