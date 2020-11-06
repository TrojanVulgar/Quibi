package com.easyplex.ui.download;

import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.easyplex.R;
import com.easyplex.data.repository.MediaRepository;
import com.easyplex.databinding.DownloadActivityBinding;
import com.easyplex.ui.viewmodels.MoviesListViewModel;
import com.easyplex.util.SpacingItemDecoration;
import com.easyplex.util.Tools;

import javax.inject.Inject;

import dagger.android.AndroidInjection;

public class DownloadList extends AppCompatActivity {



    DownloadActivityBinding binding;



    @Inject
    MediaRepository mediaRepository;

    @Inject
    ViewModelProvider.Factory viewModelFactory;


    @Override
    protected void onCreate(Bundle savedInstanceState) {



        AndroidInjection.inject(this);


        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.download_activity);

        onLoadToolbar();

        onLoadNestedToolbar();


        Tools.hideSystemPlayerUi(this,true,0);
        Tools.setSystemBarTransparent(this);


        loadFavMovies();

    }




    @RequiresApi(api = Build.VERSION_CODES.M)
    private void onLoadNestedToolbar() {

        Tools.loadAppBar(binding.scrollView,binding.toolbar);


    }


    private void onLoadToolbar() {

        Tools.loadToolbar(this,binding.toolbar,null);
        Tools.loadMiniLogo(binding.logoImageTop);

    }


    private void loadFavMovies() {


        // ViewModel to cache, retrieve data for MyListFragment
        MoviesListViewModel moviesListViewModel = new ViewModelProvider(this, viewModelFactory).get(MoviesListViewModel.class);


        DownloadAdapter downloadAdapter = new DownloadAdapter();

        moviesListViewModel.getMoviesDownloads().observe(this, favoriteMovies -> {
            if (favoriteMovies != null && !favoriteMovies.isEmpty()) {

                binding.noResults.setVisibility(View.GONE);

                downloadAdapter.addCasts(favoriteMovies, mediaRepository);
                binding.recyclerViewUpcoming.setAdapter(downloadAdapter);
                binding.recyclerViewUpcoming.setLayoutManager(new LinearLayoutManager(this));
                binding.recyclerViewUpcoming.addItemDecoration(new SpacingItemDecoration(1, Tools.dpToPx(this, 0), true));
                binding.recyclerViewUpcoming.setHasFixedSize(true);




            } else {
                binding.noResults.setVisibility(View.VISIBLE);
            }
        });


    }




}
