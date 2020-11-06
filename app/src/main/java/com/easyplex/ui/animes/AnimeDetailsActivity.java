package com.easyplex.ui.animes;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.bumptech.glide.Glide;
import com.easyplex.data.local.entity.Media;
import com.easyplex.data.model.genres.Genre;
import com.easyplex.data.model.serie.Season;
import com.easyplex.data.repository.MediaRepository;
import com.easyplex.databinding.ItemAnimeDetailBinding;
import com.easyplex.R;
import com.easyplex.ui.manager.AuthManager;
import com.easyplex.ui.manager.SettingsManager;
import com.easyplex.ui.manager.TokenManager;
import com.easyplex.ui.viewmodels.AnimeViewModel;
import com.easyplex.util.Tools;
import java.util.Iterator;
import java.util.List;
import javax.inject.Inject;
import dagger.android.AndroidInjection;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static com.easyplex.util.Constants.ARG_MOVIE;
import static com.easyplex.util.Constants.SEASONS;
import static com.easyplex.util.Constants.SPECIALS;


/**
 * EasyPlex - Movies - Live Streaming - TV Series, Anime
 *
 * @author @Y0bEX
 * @name  EasyPlex - Movies - Live Streaming - TV Series, Anime
 * @copyright Copyright (c) 2020 Y0bEX,
 * @license http://codecanyon.net/wiki/support/legal-terms/licensing-terms/
 * @profile https://codecanyon.net/user/yobex
 * @link yobexd@gmail.com
 * @skype yobexd@gmail.com
 **/


public class AnimeDetailsActivity extends AppCompatActivity {



    private boolean mAnime;
    private boolean mEpisodes;


    ItemAnimeDetailBinding binding;


    @Inject ViewModelProvider.Factory viewModelFactory;


    private AnimeViewModel animeViewModel;



    private AnimesEpisodeAdapter mEPAdapter;

    @Inject
    SharedPreferences sharedPreferences;

    @Inject
    TokenManager tokenManager;

    @Inject
    SettingsManager settingsManager;

    @Inject
    AuthManager authManager;

    @Inject
    MediaRepository mediaRepository;





    @Override
    @SuppressWarnings("DEPRECATION")
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this,R.layout.item_anime_detail);

        mAnime = false;
        mEpisodes = false;
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.scrollView.setVisibility(View.GONE);

        Intent intent = getIntent();

        Media anime = intent.getParcelableExtra(ARG_MOVIE);

        Tools.hideSystemPlayerUi(this,true,0);

        Tools.setSystemBarTransparent(this);

        animeViewModel = new ViewModelProvider(this, viewModelFactory).get(AnimeViewModel.class);


        if (anime != null){


            // Fetch Anime Details by TMDB
            animeViewModel.getAnimeDetails(anime.getTmdbId());


            // Observe data from AnimeViewmodel
            animeViewModel.animeDetailMutableLiveData.observe(this, animeDetail -> {
                onLoadImage(animeDetail.getPosterPath());
                onLoadTitle(animeDetail.getName());
                onLoadSeasons(animeDetail.getSeasons(),animeDetail.getTmdbId(),animeDetail.getName(),animeDetail.getPremuim(),animeDetail.getPosterPath());
                onLoadDate(animeDetail.getFirstAirDate());
                onLoadSynopsis(animeDetail.getOverview());
                onLoadAppBar(animeDetail.getName());
                onLoadRating(animeDetail.getVoteAverage());
                onLoadGenres(animeDetail.getGenres());
                onLoadBackButton();



                // Report Anime if something didn't work
                binding.report.setOnClickListener(v -> onLoadReport(animeViewModel,animeDetail.getName(),animeDetail.getPosterPath()));


                // Button to handle Trailer
                binding.ButtonPlayTrailer.setOnClickListener(v -> onLoadTrailer(animeDetail.getPreviewPath(),animeDetail.getName(),animeDetail.getBackdropPath()));



                // Share button function
                binding.shareIcon.setOnClickListener(v -> onLoadShare(

                        animeDetail.getName(),Integer.parseInt(animeDetail.getTmdbId())

                ));



                // Favorite button function
                binding.favoriteIcon.setOnClickListener(view -> {
                    binding.favoriteIcon.toggleWishlisted();
                    onFavoriteClick(animeDetail);
                });



                // Check if the anime is Favorite Table
                animeViewModel.isFavorite(Integer.parseInt(animeDetail.getTmdbId())).observe(this, favAnime -> {
                    binding.favoriteIcon.setActivated(favAnime != null);
                    binding.favoriteIcon.setVisibility(View.VISIBLE);
                });


                mAnime = true;
                checkAllDataLoaded();


            });



        }

    }



    // Add or Remove Anime from Favorite
    public void onFavoriteClick(Media series) {
        if (binding.favoriteIcon.isActivated()) {
            Toast.makeText(this, "Added: " + series.getName(), Toast.LENGTH_SHORT).show();
            animeViewModel.addtvFavorite(series);
        } else {
            Toast.makeText(this, "Removed: " + series.getName(),
                    Toast.LENGTH_SHORT).show();
            animeViewModel.removeTvFromFavorite(series);
        }
    }



    // Load Seasons & episodes
    private void onLoadSeasons(List<Season> seasons, String animeId, String tvName, int premuim, String posterPath) {

        binding.mseason.setText(SEASONS+ seasons.size());
        for(Iterator<Season> iterator = seasons.iterator(); iterator.hasNext(); ) {
            if(iterator.next().getName().equals(SPECIALS))
                iterator.remove();
        }

        binding.planetsSpinner.setItem(seasons);
        binding.planetsSpinner.setSelection(0);
        binding.planetsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {


                Season season = (Season) adapterView.getItemAtPosition(position);
                String episodeId = String.valueOf(season.getId());
                String currentSeason = season.getName();
                String seasonNumber = season.getSeasonNumber();


                // Episodes RecycleView
                binding.recyclerViewEpisodes.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                binding.recyclerViewEpisodes.setHasFixedSize(true);
                mEPAdapter = new AnimesEpisodeAdapter(animeId,seasonNumber,episodeId,currentSeason,sharedPreferences,authManager,settingsManager,mediaRepository
                ,tvName,premuim,tokenManager,AnimeDetailsActivity.this,posterPath);
                mEPAdapter.addSeasons(season.getEpisodes());
                binding.recyclerViewEpisodes.setAdapter(mEPAdapter);




            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

                // do nothing if no season selected

            }
        });


        mEpisodes = true;
        checkAllDataLoaded();

    }



    // Appbar Animation
    private void onLoadAppBar(final String title) {

        binding.appBarLayout.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            if (appBarLayout.getTotalScrollRange() + verticalOffset == 0) {
                if (title != null){

                    binding.collapsingToolbar.setTitle(title);
                    binding.collapsingToolbar.setContentScrimColor(Color.parseColor("#E6070707"));

                }


                binding.collapsingToolbar.setTitle("");
                binding.toolbar.setVisibility(View.VISIBLE);
                binding.planetsSpinner.setVisibility(View.VISIBLE);



            } else {
                binding.collapsingToolbar.setTitle("");
                binding.toolbar.setVisibility(View.INVISIBLE);
                binding.planetsSpinner.setVisibility(View.GONE);
            }
        });
    }


    // Load the anime rating
    private void onLoadRating(float voteAverage) {

        binding.ratingBar.setRating(voteAverage / 2);
    }




    // Share button function
    private void onLoadShare(String title, int imdb) {

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.share_msg)+ " " + title + " For more information please check"+("https://www.imdb.com/title/tt" + imdb));
        sendIntent.setType("text/plain");
        startActivity(sendIntent);



    }





    // Handle Back Button
    private void onLoadBackButton() {

        binding.backbutton.setOnClickListener(v -> {
            onBackPressed();
            Animatoo.animateSplit(this);

        });
    }



    // Load Anime Trailer
    private void onLoadTrailer(String previewPath,String title,String backdrop) {

        Tools.startTrailer(this,previewPath,title,backdrop);
    }


    // Display Anime Poster
    private void onLoadImage(String imageURL){

        Tools.onLoadMediaCover(binding.MovieCover,imageURL);

    }


    // Display Anime Title
    private void onLoadTitle(String title){

        binding.serieTitle.setText(title);
    }


    // Display Anime Release Date
    private void onLoadDate(String date){

        Tools.dateFormat(date, binding.mrelease);
    }


    // Display Anime Synopsis or Overview
    private void onLoadSynopsis(String synopsis){
        binding.serieOverview.setText(synopsis);
    }


    private void onLoadReport(AnimeViewModel anime, String title, String posterpath) {


        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_report);
        dialog.setCancelable(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = MATCH_PARENT;
        lp.height = MATCH_PARENT;

        EditText editTextMessage = dialog.findViewById(R.id.et_post);
        TextView reportMovieName = dialog.findViewById(R.id.movietitle);
        ImageView imageView = dialog.findViewById(R.id.item_movie_image);


        reportMovieName.setText(title);


        Tools.onLoadMediaCover(imageView,posterpath);

        dialog.findViewById(R.id.bt_cancel).setOnClickListener(v -> dialog.dismiss());
        dialog.findViewById(R.id.bt_submit).setOnClickListener(v -> {


            String message = editTextMessage.getText().toString().trim();


            if (message !=null) {

                anime.sendReport(title,message);
                anime.reportMutableLiveData.observe(this, report -> {


                    if (report !=null) {


                        dialog.dismiss();


                        Toast.makeText(this, "Your report has been submitted successfully", Toast.LENGTH_SHORT).show();

                    }


                });

            }


        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);






    }

    // Anime Genres
    private void onLoadGenres(List<Genre> genresList) {
        String genres = "";
        if (genresList != null) {
            for (int i = 0; i < genresList.size(); i++) {
                if (genresList.get(i) == null) continue;
                if (i == genresList.size() - 1) {
                    genres = genres.concat(genresList.get(i).getName());
                } else {
                    genres = genres.concat(genresList.get(i).getName() + ", ");
                }
            }
        }
        binding.mgenres.setText(genres);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finishAfterTransition();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }






    private void checkAllDataLoaded() {
        if (mAnime && mEpisodes) {
            binding.progressBar.setVisibility(View.GONE);
            binding.scrollView.setVisibility(View.VISIBLE);

        }
    }



    @Override
    public void onBackPressed() {
        finishAfterTransition();
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            Tools.hideSystemPlayerUi(this,true,0);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Glide.get(this).clearMemory();
        binding = null;
    }
}