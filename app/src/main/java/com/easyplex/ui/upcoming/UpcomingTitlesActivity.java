package com.easyplex.ui.upcoming;

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
import com.easyplex.data.model.media.MediaModel;
import com.easyplex.data.model.upcoming.Upcoming;
import com.easyplex.databinding.UpcomingTitlesOverviewBinding;
import com.easyplex.ui.player.activities.EasyPlexMainPlayer;
import com.easyplex.ui.player.activities.EasyPlexPlayerActivity;
import com.easyplex.ui.viewmodels.UpcomingViewModel;
import com.easyplex.util.Constants;
import javax.inject.Inject;
import at.huber.youtubeExtractor.VideoMeta;
import at.huber.youtubeExtractor.YouTubeExtractor;
import at.huber.youtubeExtractor.YtFile;
import dagger.android.AndroidInjection;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasAndroidInjector;
import static com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions.withCrossFade;
import static com.easyplex.util.Tools.hideSystemPlayerUi;
import static com.easyplex.util.Tools.setSystemBarTransparent;


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

public class UpcomingTitlesActivity extends AppCompatActivity implements HasAndroidInjector {


    public static final String ARG_MOVIE = "movie";




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
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this,R.layout.upcoming_titles_overview);

        Intent movieIntent = getIntent();
        Upcoming upcoming = movieIntent.getParcelableExtra(ARG_MOVIE);

        hideSystemPlayerUi(this,true,0);

        setSystemBarTransparent(this);


        UpcomingViewModel upcomingViewModel = new ViewModelProvider(this, viewModelFactory).get(UpcomingViewModel.class);

        upcomingViewModel.getUpcomingMovieDetail(upcoming.getId());
        upcomingViewModel.upcomingMutableLiveData.observe(this, upcomingDetails -> {

            if (upcoming !=null) {

                onLoadDetails(upcomingDetails.getPosterPath(),upcomingDetails.getTitle(),upcomingDetails.getReleaseDate(),upcomingDetails.getOverview());
                onLoadTrailer(upcomingDetails.getTrailerId(),upcomingDetails.getTitle(),upcomingDetails.getBackdropPath());

            }

        });


    }

    private void onLoadDetails(String backdropPath, String title, String releaseDate, String overview) {


        // Load Upcoming Title
        binding.movietitle.setText(title);


        // Load Upcoming Cover

        Glide.with(getApplicationContext()).asBitmap().load(backdropPath)
                .centerInside()
                .transition(withCrossFade())
                .diskCacheStrategy(DiskCacheStrategy.ALL).into(binding.movieImage);


        // Load Upcoming Release Date
        binding.mReleaseDate.setText(releaseDate);

        // Load Upcoming Overview
        binding.mplot.setText(overview);

    }

    private void onLoadTrailer(String previewPath,String title,String backdrop){

        new YouTubeExtractor(UpcomingTitlesActivity.this) {
            @SuppressLint("StaticFieldLeak")
            @Override
            public void onExtractionComplete(SparseArray<YtFile> files, VideoMeta vMeta) {

                if (files != null) {
                    // 720
                    int itag = 22;
                    String downloadUrl = files.get(itag).getUrl();
                    if (downloadUrl != null && !downloadUrl.isEmpty()) {

                        Intent intent = new Intent(UpcomingTitlesActivity.this, EasyPlexMainPlayer.class);
                        intent.putExtra(EasyPlexPlayerActivity.EASYPLEX_MEDIA_KEY, MediaModel.media(null, null, null, null, title, downloadUrl, backdrop, null
                                , null, null, null,
                                null,
                                null, null, null, null,null,null));
                        startActivity(intent);
                        finish();

                    } else {

                        Intent youtubeIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.YOUTUBE_WATCH_BASE_URL + previewPath));
                        startActivity(youtubeIntent);
                        finish();
                    }

                }

            }

        }.extract(previewPath, true, true);


    }


}
