package com.easyplex.ui.upcoming;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.easyplex.R;
import com.easyplex.databinding.FragmentUpcomingBinding;
import com.easyplex.di.Injectable;
import com.easyplex.ui.viewmodels.UpcomingViewModel;
import com.easyplex.util.SpacingItemDecoration;
import com.easyplex.util.Tools;
import javax.inject.Inject;

public class UpComingFragment extends Fragment implements Injectable{



    FragmentUpcomingBinding binding;

    private boolean mUpcomingSectionLoaded;

    @Inject
    ViewModelProvider.Factory viewModelFactory;
    private UpcomingViewModel upcomingViewModel;




    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_upcoming, container, false);

        onLoadToolbar();

        onLoadNestedToolbar();

        setHasOptionsMenu(true);

        binding.scrollView.setVisibility(View.GONE);
        binding.progressBar.setVisibility(View.VISIBLE);





        return binding.getRoot();

    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        upcomingViewModel = new ViewModelProvider(this, viewModelFactory).get(UpcomingViewModel.class);

        binding.progressBar.setVisibility(View.VISIBLE);

       onLoadUpcoming();

    }



    @RequiresApi(api = Build.VERSION_CODES.M)
    private void onLoadNestedToolbar() {

        Tools.loadAppBar(binding.scrollView,binding.toolbar);


    }


    private void onLoadToolbar() {

        Tools.loadToolbar(((AppCompatActivity)getActivity()),binding.toolbar,null);
        Tools.loadMiniLogo(binding.logoImageTop);

    }

    private void onLoadUpcoming() {

        UpcomingAdapter mUpcomingAdapter = new UpcomingAdapter();
        binding.recyclerViewUpcoming.setAdapter(mUpcomingAdapter);
        binding.recyclerViewUpcoming.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.recyclerViewUpcoming.addItemDecoration(new SpacingItemDecoration(1, Tools.dpToPx(getActivity(), 0), true));
        binding.recyclerViewUpcoming.setHasFixedSize(true);

        upcomingViewModel.getUpcomingMovie();
        upcomingViewModel.upcomingResponseMutableLive.observe(getViewLifecycleOwner(), upcoming -> {

                mUpcomingAdapter.addCasts(upcoming.getUpcoming());

                if (mUpcomingAdapter.getItemCount() == 0) {

                    binding.noResults.setVisibility(View.VISIBLE);

                }else {


                    binding.noResults.setVisibility(View.GONE);

                }

                mUpcomingSectionLoaded = true;
                checkAllDataLoaded();

        });


    }


    private void checkAllDataLoaded() {
        if (mUpcomingSectionLoaded ) {
            binding.scrollView.setVisibility(View.VISIBLE);
            binding.progressBar.setVisibility(View.GONE);
        }
    }



    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.clear();
    }




    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding.recyclerViewUpcoming.setAdapter(null);
        Glide.get(getContext()).clearMemory();
        binding.constraintLayout.removeAllViews();
        binding.scrollView.removeAllViews();
        binding =null;

    }


}
