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


public class MoviesFragment extends Fragment implements Injectable {

    LayoutGenresBinding binding;

    @Inject
    ViewModelProvider.Factory viewModelFactory;
    private GenresViewModel genresViewModel;

    private boolean sectionLoaded;
    private GenresAdapter mMoviesByGenresAdapter;
    private List<String> provinceList;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.layout_genres, container, false);
        sectionLoaded = false;
        binding.progressBar.setVisibility(View.VISIBLE);
        genresViewModel = new ViewModelProvider(this, viewModelFactory).get(GenresViewModel.class);
        genresViewModel.getMoviesGenresList();


        onLoadAllGenres();

        onLoadGenres();


        return binding.getRoot();


    }



    // Load Genres
    private void onLoadGenres() {


        genresViewModel.movieDetailMutableLiveData.observe(getViewLifecycleOwner(), movieDetail -> {

            binding.filterBtn.setOnClickListener(v -> binding.planetsSpinner.performClick());

            if (!movieDetail.getGenres().isEmpty()) {

                binding.noMoviesFound.setVisibility(View.GONE);


                binding.planetsSpinner.setItem(movieDetail.getGenres());
                binding.planetsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {

                        binding.progressBar.setVisibility(View.VISIBLE);
                        binding.planetsSpinner.setVisibility(View.GONE);
                        binding.filterBtn.setVisibility(View.VISIBLE);


                        Genre genre = (Genre) adapterView.getItemAtPosition(position);
                        int genreId = genre.getId();
                        String genreName = genre.getName();

                        binding.selectedGenre.setText(genreName);

                        genresViewModel.getMoviesByGenres(genreId);
                        genresViewModel.movieGenresDataMutableLiveData.observe(getViewLifecycleOwner(), movieDetail -> {

                            mMoviesByGenresAdapter = new GenresAdapter();
                            binding.recyclerView.setAdapter(mMoviesByGenresAdapter);
                            binding.recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
                            binding.recyclerView.addItemDecoration(new SpacingItemDecoration(3, Tools.dpToPx(getActivity(), 0), true));
                            binding.recyclerView.setHasFixedSize(true);
                            mMoviesByGenresAdapter.addToContent(movieDetail.getGenres());
                            sectionLoaded = true;
                            checkAllDataLoaded();

                            if (mMoviesByGenresAdapter.getItemCount() == 0) {

                                binding.noMoviesFound.setVisibility(View.VISIBLE);
                                binding.noResults.setText("No Results found for "+genreName);

                            } else {

                                binding.noMoviesFound.setVisibility(View.GONE);

                            }


                        });

                    }


                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                        // Nothting to refresh when no Item Selected

                    }
                });


            }else {


                binding.noMoviesFound.setVisibility(View.VISIBLE);
                binding.progressBar.setVisibility(View.GONE);

            }


        });



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
                                genresViewModel.getMoviesAllMovies();
                                genresViewModel.movieGenresDataMutableLiveData.observe(getViewLifecycleOwner(), movieDetail -> {

                                    String genreName = provinceList.get(0);
                                    binding.selectedGenreLeft.setText(genreName);

                                    mMoviesByGenresAdapter = new GenresAdapter();
                                    binding.recyclerView.setAdapter(mMoviesByGenresAdapter);
                                    binding.recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
                                    binding.recyclerView.addItemDecoration(new SpacingItemDecoration(3, Tools.dpToPx(getActivity(), 0), true));
                                    binding.recyclerView.setHasFixedSize(true);
                                    mMoviesByGenresAdapter.addToContent(movieDetail.getGenres());


                                    sectionLoaded = true;
                                    checkAllDataLoaded();

                                     });

                                break;
                            case 1:
                                genresViewModel.getMoviesLatestMovies();
                                genresViewModel.movieGenresDataMutableLiveData.observe(getViewLifecycleOwner(), movieDetail -> {


                                    String genreName = provinceList.get(1);
                                    binding.selectedGenreLeft.setText(genreName);

                                    mMoviesByGenresAdapter = new GenresAdapter();
                                    binding.recyclerView.setAdapter(mMoviesByGenresAdapter);
                                    binding.recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
                                    binding.recyclerView.addItemDecoration(new SpacingItemDecoration(3, Tools.dpToPx(getActivity(), 0), true));
                                    binding.recyclerView.setHasFixedSize(true);
                                    mMoviesByGenresAdapter.addToContent(movieDetail.getGenres());

                                    sectionLoaded = true;
                                    checkAllDataLoaded();

                                });



                                break;
                            case 2:

                                genresViewModel.getByRating();
                                genresViewModel.movieGenresDataMutableLiveData.observe(getViewLifecycleOwner(), movieDetail -> {

                                    String genreName = provinceList.get(2);
                                    binding.selectedGenreLeft.setText(genreName);

                                    mMoviesByGenresAdapter = new GenresAdapter();
                                    binding.recyclerView.setAdapter(mMoviesByGenresAdapter);
                                    binding.recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
                                    binding.recyclerView.addItemDecoration(new SpacingItemDecoration(3, Tools.dpToPx(getActivity(), 0), true));
                                    binding.recyclerView.setHasFixedSize(true);
                                    mMoviesByGenresAdapter.addToContent(movieDetail.getGenres());


                                    sectionLoaded = true;
                                    checkAllDataLoaded();

                                });
                                break;
                            case 3:

                                genresViewModel.getByYear();
                                genresViewModel.movieGenresDataMutableLiveData.observe(getViewLifecycleOwner(), movieDetail -> {


                                    String genreName = provinceList.get(3);
                                    binding.selectedGenreLeft.setText(genreName);

                                    mMoviesByGenresAdapter = new GenresAdapter();
                                    binding.recyclerView.setAdapter(mMoviesByGenresAdapter);
                                    binding.recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
                                    binding.recyclerView.addItemDecoration(new SpacingItemDecoration(3, Tools.dpToPx(getActivity(), 0), true));
                                    binding.recyclerView.setHasFixedSize(true);
                                    mMoviesByGenresAdapter.addToContent(movieDetail.getGenres());


                                    sectionLoaded = true;
                                    checkAllDataLoaded();

                                    });

                                break;
                            case 4:
                                genresViewModel.getByViews();
                                genresViewModel.movieGenresDataMutableLiveData.observe(getViewLifecycleOwner(), movieDetail -> {


                                    String genreName = provinceList.get(4);
                                    binding.selectedGenreLeft.setText(genreName);

                                    mMoviesByGenresAdapter = new GenresAdapter();
                                    binding.recyclerView.setAdapter(mMoviesByGenresAdapter);
                                    binding.recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
                                    binding.recyclerView.addItemDecoration(new SpacingItemDecoration(3, Tools.dpToPx(getActivity(), 0), true));
                                    binding.recyclerView.setHasFixedSize(true);
                                    mMoviesByGenresAdapter.addToContent(movieDetail.getGenres());


                                    sectionLoaded = true;
                                    checkAllDataLoaded();

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


    private void checkAllDataLoaded() {
        if (sectionLoaded) {
            binding.progressBar.setVisibility(View.GONE);
            binding.scrollView.setVisibility(View.VISIBLE);

        }
    }



    // On Fragment Detach clear binding views & adapters to avoid memory leak
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding.recyclerView.setAdapter(null);
        binding.constraintLayout.removeAllViews();
        binding.scrollView.removeAllViews();
        binding = null;


    }

}
