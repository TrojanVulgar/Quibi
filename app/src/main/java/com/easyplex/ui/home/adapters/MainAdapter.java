package com.easyplex.ui.home.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.easyplex.data.local.entity.Media;
import com.easyplex.data.model.genres.Genre;
import com.easyplex.databinding.ItemMovieBinding;
import com.easyplex.ui.moviedetails.MovieDetailsActivity;
import com.easyplex.util.Tools;
import java.util.List;
import static com.easyplex.util.Constants.ARG_MOVIE;

/**
 * Adapter for Movie.
 *
 * @author Yobex.
 */
public class MainAdapter extends RecyclerView.Adapter<MainAdapter.MainViewHolder> {

    private List<Media> castList;



    public void addMain(List<Media> mediaList) {
        this.castList = mediaList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MainAdapter.MainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        ItemMovieBinding binding = ItemMovieBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);

        return new MainAdapter.MainViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MainAdapter.MainViewHolder holder, int position) {
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

        private final ItemMovieBinding binding;

        MainViewHolder(@NonNull ItemMovieBinding binding)
        {
            super(binding.getRoot());

            this.binding = binding;
        }



        void onBind(final int position) {


            final Media media = castList.get(position);

            Context context = binding.itemMovieImage.getContext();


            binding.movietitle.setText(media.getTitle());


            for (Genre genre : media.getGenres()) {

                binding.mgenres.setText(genre.getName());


            }

            if (media.getPremuim() == 1) binding.moviePremuim.setVisibility(View.VISIBLE);


            binding.rootLayout.setOnClickListener(view -> {


                    Intent intent = new Intent(context, MovieDetailsActivity.class);
                    intent.putExtra(ARG_MOVIE, media);
                    context.startActivity(intent);


            });

            Tools.onLoadMediaCoverAdapters(binding.itemMovieImage, media.getPosterPath());

        }
    }
}
