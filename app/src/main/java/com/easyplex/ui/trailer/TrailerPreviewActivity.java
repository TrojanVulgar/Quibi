package com.easyplex.ui.trailer;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.SparseArray;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.easyplex.R;
import com.easyplex.data.model.genres.Genre;
import com.easyplex.data.local.entity.Media;
import com.easyplex.data.model.media.MediaModel;
import com.easyplex.databinding.UpcomingTitlesOverviewBinding;
import com.easyplex.ui.viewmodels.MovieDetailViewModel;
import com.easyplex.ui.player.activities.EasyPlexMainPlayer;
import com.easyplex.ui.player.activities.EasyPlexPlayerActivity;
import com.easyplex.ui.viewmodels.SerieDetailViewModel;
import com.easyplex.util.Constants;
import com.easyplex.util.DialogHelper;
import java.util.List;
import javax.inject.Inject;

import at.huber.youtubeExtractor.VideoMeta;
import at.huber.youtubeExtractor.YouTubeExtractor;
import at.huber.youtubeExtractor.YtFile;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasAndroidInjector;

import static com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions.withCrossFade;
import static com.easyplex.util.Constants.ARG_MOVIE;
import static com.easyplex.util.Constants.MOVIE;
import static com.easyplex.util.Constants.SERIE;
import static com.easyplex.util.Tools.*;


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

public class TrailerPreviewActivity extends AppCompatActivity implements HasAndroidInjector {


    @Inject
    DispatchingAndroidInjector<Object> androidInjector;

    @Override
    public AndroidInjector<Object> androidInjector() {
        return androidInjector;
    }
    @Inject ViewModelProvider.Factory viewModelFactory;

    UpcomingTitlesOverviewBinding binding;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.upcoming_titles_overview);

        hideSystemPlayerUi(this,true,0);

        setSystemBarTransparent(this);

        Intent intent = getIntent();
        Media media = intent.getParcelableExtra(ARG_MOVIE);


        MovieDetailViewModel movieDetailViewModel = new ViewModelProvider(this, viewModelFactory).get(MovieDetailViewModel.class);
        SerieDetailViewModel serieDetailViewModel = new ViewModelProvider(this, viewModelFactory).get(SerieDetailViewModel.class);



        // For Movies
        if ((media != null ? media.getTitle() : null) != null){

            movieDetailViewModel.getMovieDetails(media.getTmdbId());
            movieDetailViewModel.movieDetailMutableLiveData.observe(this, movieDetail -> {
                onLoadTrailer(movieDetail.getPreviewPath(),movieDetail.getTitle(),movieDetail.getBackdropPath());
                onLoaCoverPreview(movieDetail.getPosterPath());
                onLoadTitle(movieDetail.getTitle());
                onLoadGenre(movieDetail.getGenres());
                onLoadOverview(movieDetail.getOverview());
                onLoadToolbar();
                onLoadAppLogo();
                onLoadType(movieDetail.getFirstAirDate());


            });
        } else {



            // For Series
            serieDetailViewModel.getSerieDetails(media != null ? media.getTmdbId() : null);
            serieDetailViewModel.movieDetailMutableLiveData.observe(this, movieDetail -> {
                onLoadTrailer(movieDetail.getPreviewPath(),movieDetail.getName(),movieDetail.getBackdropPath());
                onLoaCoverPreview(movieDetail.getPosterPath());
                onLoadTitle(movieDetail.getName());
                onLoadGenre(movieDetail.getGenres());
                onLoadOverview(movieDetail.getOverview());
                onLoadToolbar();
                onLoadAppLogo();
                onLoadType(movieDetail.getFirstAirDate());


            });
        }

    }


    // Load Media Type (Movie , Serie)
    private void onLoadType(String firstAirDate) {

        if (firstAirDate !=null) {


            this.binding.mtv.setText(SERIE);

        }else {


            this.binding.mtv.setText(MOVIE);

        }


    }



    // Load Media Genre
    private void onLoadGenre(List<Genre> genres) {

        String genre = "";
        if (genres != null) {
            for (int i = 0; i < genres.size(); i++) {
                if (genres.get(i) == null) continue;
                if (i == genres.size() - 1) {
                    genre = genre.concat(genres.get(i).getName());
                } else {
                    genre = genre.concat(genres.get(i).getName() + ", ");
                }
            }
        }
        binding.mReleaseDate.setText(genre);
    }



    // Load Media Overview
    private void onLoadOverview(String overview) {

        binding.mplot.setText(overview);
    }



    // Load Media Title
    private void onLoadTitle(String title) {
        binding.movietitle.setText(title);

    }


    // Load Trailer
    @SuppressLint("StaticFieldLeak")
    private void onLoadTrailer(String previewPath, String title, String backdrop){

        new YouTubeExtractor(TrailerPreviewActivity.this) {
            @SuppressLint("StaticFieldLeak")
            @Override
            public void onExtractionComplete(SparseArray<YtFile> files, VideoMeta vMeta) {

                if (files != null) {
                    // 720
                    int itag = 22;
                    String downloadUrl = files.get(itag).getUrl();
                    if (downloadUrl != null && !downloadUrl.isEmpty()) {

                        Intent intent = new Intent(TrailerPreviewActivity.this, EasyPlexMainPlayer.class);
                        intent.putExtra(EasyPlexPlayerActivity.EASYPLEX_MEDIA_KEY, MediaModel.media(null, null, null, null, title, downloadUrl, backdrop, null
                                , null, null, null, null,
                                null, null,
                                null, null,null,null));
                        startActivity(intent);
                        finish();

                    } else {

                        Intent youtubeIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.YOUTUBE_WATCH_BASE_URL + title));
                        startActivity(youtubeIntent);
                        finish();
                    }

                }else {

                    DialogHelper.showNoTrailerAvailable(TrailerPreviewActivity.this,previewPath);
                }

            }

        }.extract(previewPath, true, true);


    }


    // Display Media Cover
    private void onLoaCoverPreview(String posterPath) {


        Glide.with(getApplicationContext()).asBitmap().load(posterPath)
                .centerInside()
                .transition(withCrossFade())
                .diskCacheStrategy(DiskCacheStrategy.ALL).into(binding.movieImage);

    }



    // Display App Logo
    private void onLoadAppLogo() {

        loadMiniLogo(binding.logoImageTop);

    }



    // Display Toolbar
    private void onLoadToolbar() {

        loadToolbar(this,binding.toolbar,null);


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
       binding = null;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemPlayerUi(this,true,0);
        }
    }
}
