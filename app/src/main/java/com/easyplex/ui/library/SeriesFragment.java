package com.easyplex.ui.library;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import com.easyplex.R;
import com.easyplex.data.model.genres.Genre;
import com.easyplex.databinding.LayoutGenresBinding;
import com.easyplex.di.Injectable;
import com.easyplex.ui.viewmodels.GenresViewModel;
import com.easyplex.util.SpacingItemDecoration;
import com.easyplex.util.Tools;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;


public class SeriesFragment extends Fragment implements Injectable {

    LayoutGenresBinding binding;

    @Inject
    ViewModelProvider.Factory viewModelFactory;
    private GenresViewModel genresViewModel;
    private GenresAdapter mMoviesByGenresAdapter;
    private List<String> provinceList;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.layout_genres, container, false);

        genresViewModel = new ViewModelProvider(this, viewModelFactory).get(GenresViewModel.class);


        binding.filterBtnAllgenres.setOnClickListener(v -> onLoadAllGenres());

        genresViewModel.getMoviesGenresList();
        onLoadAllGenres();
        onLoadGenres();

        return binding.getRoot();

    }



    private void onLoadAllGenres() {

        provinceList = new ArrayList<>();
        provinceList.add("All Genres");
        provinceList.add("Latest Added");
        provinceList.add("By Rating");
        provinceList.add("By Year");
        provinceList.add("By Views");


        binding.filterBtnAllgenres.setOnClickListener(v -> binding.planetsSpinnerSort.performClick());

        binding.noMoviesFound.setVisibility(View.GONE);

        binding.planetsSpinnerSort.setItem(provinceList);
        binding.planetsSpinnerSort.setSelection(0);
        binding.planetsSpinnerSort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {



                switch (position) {

                    case 0:
                        genresViewModel.getMoviesAllSeries();
                        genresViewModel.movieGenresDataMutableLiveData.observe(getViewLifecycleOwner(), movieDetail -> {

                            String genreName = provinceList.get(0);
                            binding.selectedGenreLeft.setText(genreName);

                            mMoviesByGenresAdapter = new GenresAdapter();
                            binding.recyclerView.setAdapter(mMoviesByGenresAdapter);
                            binding.recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
                            binding.recyclerView.addItemDecoration(new SpacingItemDecoration(3, Tools.dpToPx(getActivity(), 0), true));
                            binding.recyclerView.setHasFixedSize(true);
                            mMoviesByGenresAdapter.addToContent(movieDetail.getGenres());



                        });

                        break;
                    case 1:
                        genresViewModel.getMoviesLatestSeries();
                        genresViewModel.movieGenresDataMutableLiveData.observe(getViewLifecycleOwner(), movieDetail -> {


                            String genreName = provinceList.get(1);
                            binding.selectedGenreLeft.setText(genreName);

                            mMoviesByGenresAdapter = new GenresAdapter();
                            binding.recyclerView.setAdapter(mMoviesByGenresAdapter);
                            binding.recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
                            binding.recyclerView.addItemDecoration(new SpacingItemDecoration(3, Tools.dpToPx(getActivity(), 0), true));
                            binding.recyclerView.setHasFixedSize(true);
                            mMoviesByGenresAdapter.addToContent(movieDetail.getGenres());


                        });



                        break;
                    case 2:

                        genresViewModel.getByRatingtv();
                        genresViewModel.movieGenresDataMutableLiveData.observe(getViewLifecycleOwner(), movieDetail -> {

                            String genreName = provinceList.get(2);
                            binding.selectedGenreLeft.setText(genreName);

                            mMoviesByGenresAdapter = new GenresAdapter();
                            binding.recyclerView.setAdapter(mMoviesByGenresAdapter);
                            binding.recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
                            binding.recyclerView.addItemDecoration(new SpacingItemDecoration(3, Tools.dpToPx(getActivity(), 0), true));
                            binding.recyclerView.setHasFixedSize(true);
                            mMoviesByGenresAdapter.addToContent(movieDetail.getGenres());

                        });
                        break;
                    case 3:

                        genresViewModel.getByYeartv();
                        genresViewModel.movieGenresDataMutableLiveData.observe(getViewLifecycleOwner(), movieDetail -> {


                            String genreName = provinceList.get(3);
                            binding.selectedGenreLeft.setText(genreName);

                            mMoviesByGenresAdapter = new GenresAdapter();
                            binding.recyclerView.setAdapter(mMoviesByGenresAdapter);
                            binding.recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
                            binding.recyclerView.addItemDecoration(new SpacingItemDecoration(3, Tools.dpToPx(getActivity(), 0), true));
                            binding.recyclerView.setHasFixedSize(true);
                            mMoviesByGenresAdapter.addToContent(movieDetail.getGenres());


                        });

                        break;
                    case 4:
                        genresViewModel.getByViewstv();
                        genresViewModel.movieGenresDataMutableLiveData.observe(getViewLifecycleOwner(), movieDetail -> {

                            String genreName = provinceList.get(4);
                            binding.selectedGenreLeft.setText(genreName);

                            mMoviesByGenresAdapter = new GenresAdapter();
                            binding.recyclerView.setAdapter(mMoviesByGenresAdapter);
                            binding.recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
                            binding.recyclerView.addItemDecoration(new SpacingItemDecoration(3, Tools.dpToPx(getActivity(), 0), true));
                            binding.recyclerView.setHasFixedSize(true);
                            mMoviesByGenresAdapter.addToContent(movieDetail.getGenres());


                        });

                        break;
                    default:
                        break;
                }


            }


            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

                // Nothting to refresh when no Item Selected

            }
        });

    }

    private void onLoadGenres() {




        binding.filterBtn.setOnClickListener(v -> binding.planetsSpinner.performClick());



        genresViewModel.movieDetailMutableLiveData.observe(getViewLifecycleOwner(), movieDetail -> {



            if (!movieDetail.getGenres().isEmpty()) {

                binding.noMoviesFound.setVisibility(View.GONE);
                binding.planetsSpinner.setItem(movieDetail.getGenres());
                binding.planetsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {

                        binding.planetsSpinner.setVisibility(View.GONE);
                        binding.filterBtn.setVisibility(View.VISIBLE);
                        Genre genre = (Genre) adapterView.getItemAtPosition(position);
                        int genreId = genre.getId();
                        String genreName = genre.getName();

                        binding.selectedGenre.setText(genreName);

                        genresViewModel.getSeriesByGenres(genreId);
                        genresViewModel.movieGenresDataMutableLiveData.observe(getViewLifecycleOwner(), movieDetail -> {


                            mMoviesByGenresAdapter = new GenresAdapter();
                            binding.recyclerView.setAdapter(mMoviesByGenresAdapter);
                            binding.recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
                            binding.recyclerView.addItemDecoration(new SpacingItemDecoration(3, Tools.dpToPx(getActivity(), 0), true));
                            binding.recyclerView.setHasFixedSize(true);
                            mMoviesByGenresAdapter.addToContent(movieDetail.getGenres());

                            if (mMoviesByGenresAdapter.getItemCount() == 0) {

                                binding.noMoviesFound.setVisibility(View.VISIBLE);
                                binding.noResults.setText(String.format("No Results found for %s", genreName));

                            }else {

                                binding.noMoviesFound.setVisibility(View.GONE);

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


    // On Fragment Detach clear binding views &  adapters to avoid memory leak
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding.recyclerView.setAdapter(null);
        binding.constraintLayout.removeAllViews();
        binding.scrollView.removeAllViews();
        binding.planetsSpinner.clearSelection();
        binding = null;


    }

}
