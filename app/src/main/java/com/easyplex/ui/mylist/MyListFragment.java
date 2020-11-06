package com.easyplex.ui.mylist;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import com.easyplex.R;
import com.easyplex.databinding.FragmentFavouriteMoviesBinding;
import com.easyplex.di.Injectable;
import com.easyplex.ui.viewmodels.MoviesListViewModel;
import com.easyplex.util.SpacingItemDecoration;
import com.easyplex.util.Tools;

import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;


public class MyListFragment extends Fragment implements Injectable {


    @Inject
    ViewModelProvider.Factory viewModelFactory;


    FragmentFavouriteMoviesBinding binding;




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    @Nullable
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_favourite_movies, container, false);


        // ViewModel to cache, retrieve data for MyListFragment
        MoviesListViewModel moviesListViewModel = new ViewModelProvider(this, viewModelFactory).get(MoviesListViewModel.class);


        MyListMoviesdapter myListMoviesdapter = new MyListMoviesdapter();

        moviesListViewModel.getMoviesFavorites().observe(getViewLifecycleOwner(), favoriteMovies -> {
            if (favoriteMovies != null && !favoriteMovies.isEmpty()) {

                binding.noResults.setVisibility(View.GONE);

                myListMoviesdapter.addToContent(favoriteMovies);
                binding.rvMylist.setAdapter(myListMoviesdapter);
                binding.rvMylist.setLayoutManager(new GridLayoutManager(getActivity(), 3));
                binding.rvMylist.addItemDecoration(new SpacingItemDecoration(3, Tools.dpToPx(getActivity(), 0), true));
                binding.rvMylist.setHasFixedSize(true);

            } else {
                binding.noResults.setVisibility(View.VISIBLE);
            }
        });

        return  binding.getRoot();

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding.rvMylist.setAdapter(null);
        binding =null;


    }


}
