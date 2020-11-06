package com.easyplex.ui.home.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.easyplex.data.local.entity.Media;
import com.easyplex.databinding.RowItemSeriesBinding;
import com.easyplex.ui.seriedetails.SerieDetailsActivity;
import com.easyplex.util.Tools;
import java.util.List;

import static com.easyplex.util.Constants.ARG_MOVIE;

/**
 * Adapter for Latest Series.
 *
 * @author Yobex.
 */
public class LatestseriesAdapter extends RecyclerView.Adapter<LatestseriesAdapter.MainViewHolder> {

    private List<Media> castList;

    public void addLatest(List<Media> castList) {
        this.castList = castList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public LatestseriesAdapter.MainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        RowItemSeriesBinding binding = RowItemSeriesBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);

        return new LatestseriesAdapter.MainViewHolder(binding);

    }

    @Override
    public void onBindViewHolder(@NonNull LatestseriesAdapter.MainViewHolder holder, int position) {
        holder.onBind(position);
    }

    @Override
    public int getItemCount() {
        if (castList != null) {
            return castList.size();
        } else {
            return 0;
        }
    }

    class MainViewHolder extends RecyclerView.ViewHolder {

        private final RowItemSeriesBinding binding;

        MainViewHolder(@NonNull RowItemSeriesBinding binding)
        {
            super(binding.getRoot());

            this.binding = binding;
        }

        void onBind(final int position) {




            final Media serie = castList.get(position);

            Context context = binding.itemMovieImage.getContext();


            binding.movietitle.setText(serie.getName());

            binding.rootLayout.setOnClickListener(view -> {

                Intent intent = new Intent(context, SerieDetailsActivity.class);
                intent.putExtra(ARG_MOVIE, serie);
                context.startActivity(intent);


            });


            if (serie.getPremuim() == 1) {

                binding.moviePremuim.setVisibility(View.VISIBLE);


            }else {

                binding.moviePremuim.setVisibility(View.GONE);
            }


            Tools.onLoadMediaCover(binding.itemMovieImage,serie.getPosterPath());

        }
    }
}
