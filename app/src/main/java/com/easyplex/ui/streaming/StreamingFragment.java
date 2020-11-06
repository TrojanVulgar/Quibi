package com.easyplex.ui.streaming;


import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;

import com.easyplex.R;
import com.easyplex.data.model.genres.Genre;
import com.easyplex.data.repository.MediaRepository;
import com.easyplex.data.repository.SettingsRepository;
import com.easyplex.databinding.FragmentStreamingBinding;
import com.easyplex.di.Injectable;
import com.easyplex.ui.manager.AuthManager;
import com.easyplex.ui.manager.SettingsManager;
import com.easyplex.ui.manager.TokenManager;
import com.easyplex.ui.viewmodels.StreamingDetailViewModel;
import com.easyplex.ui.viewmodels.StreamingGenresViewModel;
import com.easyplex.util.SpacingItemDecoration;
import com.easyplex.util.Tools;
import javax.inject.Inject;


public class StreamingFragment extends Fragment implements Injectable {


    @Inject
    ViewModelProvider.Factory viewModelFactory;


    @Inject
    SettingsManager settingsManager;

    @Inject
    SharedPreferences preferences;

    @Inject
    SettingsRepository authRepository;



    @Inject
    MediaRepository mediaRepository;

    private StreamingGenresViewModel genresViewModel;


    @Inject
    AuthManager authManager;


    @Inject
    TokenManager tokenManager;

    FragmentStreamingBinding binding;
    StreamingAdapter mStreamingAdapter;
    FavoriteStreamingAdapter favoriteStreamingAdapter;
    LatestStreamingAdapter latestStreamingAdapter;
    MostWatchedStreamingAdapter mostWatchedStreamingAdapter;
    private FeaturedStreamingAdapter mFeaturedAdapter;
    private StreamingDetailViewModel streamingDetailViewModel;




    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_streaming, container, false);

        streamingDetailViewModel = new ViewModelProvider(this, viewModelFactory).get(StreamingDetailViewModel.class);

        genresViewModel = new ViewModelProvider(this, viewModelFactory).get(StreamingGenresViewModel.class);

        genresViewModel.getStreamingGenresList();


        mFeaturedAdapter = new FeaturedStreamingAdapter();
        latestStreamingAdapter = new LatestStreamingAdapter();
        mStreamingAdapter = new StreamingAdapter();
        mostWatchedStreamingAdapter = new MostWatchedStreamingAdapter();
        favoriteStreamingAdapter = new FavoriteStreamingAdapter();



        binding.filterBtnFavorite.setOnClickListener(v -> onLoadFavoriteList());

        return binding.getRoot();

    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        setHasOptionsMenu(true);
        onLoadLatestStreaming();
        onLoadFeaturedMovies();
        onLoadLatestStreaming();
        onLoadFavoriteList();
        onLoadMostWatchedStreaming();



    }


    private void onLoadFavoriteList() {

        streamingDetailViewModel.getStreamFavorites().observe(getViewLifecycleOwner(), favoriteMovies -> {

                binding.noResults.setVisibility(View.GONE);
                favoriteStreamingAdapter.addStreaming(getContext(),favoriteMovies,authManager,tokenManager,settingsManager,preferences);
                binding.rvFavoriteStreamning.setAdapter(favoriteStreamingAdapter);
                binding.rvFavoriteStreamning.setLayoutManager(new GridLayoutManager(getActivity(), 3));
                binding.rvFavoriteStreamning.addItemDecoration(new SpacingItemDecoration(3, Tools.dpToPx(getActivity(), 0), true));
                binding.rvFavoriteStreamning.setHasFixedSize(true);


                if (favoriteStreamingAdapter.getItemCount() == 0) {


                        binding.noResultsList.setVisibility(View.VISIBLE);

                }else {

                    binding.noResultsList.setVisibility(View.GONE);

                }

        });

    }


    // Display Featured Movies Details
    private void onLoadFeaturedMovies() {

        streamingDetailViewModel.getFeaturedStreaming();
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
        streamingDetailViewModel.featuredMoviesMutableLiveData.observe(getViewLifecycleOwner(), featured -> mFeaturedAdapter.addFeatured(featured.getFeatured(),requireActivity(),preferences,authManager,settingsManager,tokenManager));


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

        });




        if (mStreamingAdapter.getItemCount() == 0)

            binding.noResults.setVisibility(View.VISIBLE);
        else {
            binding.noResults.setVisibility(View.GONE);
        }


        binding.filterBtn.setOnClickListener(v -> {

            onLoadGenres();
            binding.planetsSpinner.performClick();
        });

    }


    private void onLoadMostWatchedStreaming() {


        streamingDetailViewModel.getMostWatchedStreaming();
        streamingDetailViewModel.mostWatchedStreamingMutableLiveData.observe(getViewLifecycleOwner(), most -> {


            binding.rvMostWatchedStreamning.setAdapter(mostWatchedStreamingAdapter);
            binding.rvMostWatchedStreamning.setLayoutManager(new LinearLayoutManager(getActivity()));
            binding.rvMostWatchedStreamning.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
            binding.rvMostWatchedStreamning.addItemDecoration(new SpacingItemDecoration(1, Tools.dpToPx(requireActivity(), 0), true));
            binding.rvMostWatchedStreamning.setHasFixedSize(true);
            mostWatchedStreamingAdapter.addStreaming(getContext(),most.getWatched(),authManager,tokenManager,settingsManager,preferences);

        });



        if (mStreamingAdapter.getItemCount() == 0)

            binding.noResults.setVisibility(View.VISIBLE);
        else {
            binding.noResults.setVisibility(View.GONE);
        }

    }


    private void onLoadGenres() {

        genresViewModel.streamingDetailMutableLiveData.observe(getViewLifecycleOwner(), streaming -> {


            if (!streaming.getGenres().isEmpty()) {


                binding.recyclerView.setVisibility(View.VISIBLE);

                binding.planetsSpinner.setItem(streaming.getGenres());
                binding.planetsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {

                        binding.planetsSpinner.setVisibility(View.GONE);
                        binding.filterBtn.setVisibility(View.VISIBLE);
                        Genre genre = (Genre) adapterView.getItemAtPosition(position);
                        int genreId = genre.getId();
                        String genreName = genre.getName();

                        binding.selectedGenre.setText(genreName);

                        genresViewModel.getStreamingByGenres(genreId);
                        genresViewModel.streamGenresDataMutableLiveData.observe(getViewLifecycleOwner(), movieDetail -> {


                            mStreamingAdapter = new StreamingAdapter();
                            binding.recyclerView.setAdapter(mStreamingAdapter);
                            binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                            binding.recyclerView.addItemDecoration(new SpacingItemDecoration(1, Tools.dpToPx(getActivity(), 0), true));
                            binding.recyclerView.setHasFixedSize(true);
                            mStreamingAdapter.addStreaming(getContext(),movieDetail.getGenres(),authManager,tokenManager,settingsManager,preferences);

                            if (mStreamingAdapter.getItemCount() == 0) {

                                binding.noMoviesFound.setVisibility(View.VISIBLE);
                                binding.noResults.setText(String.format("No Results found for %s", genreName));
                                binding.rvFeatured.setVisibility(View.GONE);
                                binding.rvLatestStreaming.setVisibility(View.GONE);
                                binding.rvMostWatchedStreamning.setVisibility(View.GONE);
                                binding.linearWatch.setVisibility(View.GONE);
                                binding.linearWatch2.setVisibility(View.GONE);
                                binding.indicator.setVisibility(View.GONE);
                                binding.linearWatchImage.setVisibility(View.GONE);
                                binding.linearWatchImage2.setVisibility(View.GONE);
                                binding.linearFavorite.setVisibility(View.GONE);
                                binding.linearFavoriteImage2.setVisibility(View.GONE);
                                binding.rvFavoriteStreamning.setVisibility(View.GONE);
                                binding.noResultsList.setVisibility(View.GONE);
                            }else {

                                binding.noMoviesFound.setVisibility(View.GONE);
                                binding.rvFeatured.setVisibility(View.GONE);
                                binding.rvLatestStreaming.setVisibility(View.GONE);
                                binding.rvMostWatchedStreamning.setVisibility(View.GONE);
                                binding.noMoviesFound.setVisibility(View.GONE);
                                binding.linearWatch.setVisibility(View.GONE);
                                binding.linearWatch2.setVisibility(View.GONE);
                                binding.indicator.setVisibility(View.GONE);
                                binding.linearWatchImage.setVisibility(View.GONE);
                                binding.linearWatchImage2.setVisibility(View.GONE);
                                binding.linearFavorite.setVisibility(View.GONE);
                                binding.linearFavoriteImage2.setVisibility(View.GONE);
                                binding.rvFavoriteStreamning.setVisibility(View.GONE);
                                binding.noResultsList.setVisibility(View.GONE);
                            }


                        });


                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                        // Invoked when a network exception occurred talking to the server or when an unexpected exception occurred creating the request or processing the response.

                    }
                });


            }else {

                binding.noMoviesFound.setVisibility(View.VISIBLE);
                binding.progressBar.setVisibility(View.GONE);

            }


        });




    }





    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.clear();
    }




    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding.recyclerView.setAdapter(null);
        binding.constraintLayout.removeAllViews();
        binding.scrollView.removeAllViews();
        binding =null;

    }
}
