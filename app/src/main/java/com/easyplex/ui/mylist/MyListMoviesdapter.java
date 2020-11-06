package com.easyplex.ui.mylist;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.easyplex.data.local.entity.Media;
import com.easyplex.databinding.ItemFavBinding;
import com.easyplex.ui.animes.AnimeDetailsActivity;
import com.easyplex.ui.moviedetails.MovieDetailsActivity;
import com.easyplex.ui.seriedetails.SerieDetailsActivity;
import com.easyplex.util.Tools;
import java.util.List;

import static com.easyplex.util.Constants.ARG_MOVIE;

/**
 * Adapter for Movie Casts.
 *
 * @author Yobex.
 */
public class MyListMoviesdapter extends RecyclerView.Adapter<MyListMoviesdapter.MainViewHolder> {

    private List<Media> castList;

    public void addToContent(List<Media> castList) {
        this.castList = castList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyListMoviesdapter.MainViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        ItemFavBinding binding = ItemFavBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);

        return new MyListMoviesdapter.MainViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyListMoviesdapter.MainViewHolder holder, int position) {
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

        private final ItemFavBinding binding;


        MainViewHolder(@NonNull ItemFavBinding binding)
        {
            super(binding.getRoot());

            this.binding = binding;
        }

        void onBind(final int position) {

            final Media myListMedia = castList.get(position);
            Context context = binding.itemMovieImage.getContext();

            binding.rootLayout.setOnClickListener(view -> {

                if (myListMedia.getIsAnime() == 1) {

                    Intent intent = new Intent(context, AnimeDetailsActivity.class);
                    intent.putExtra(ARG_MOVIE, myListMedia);
                    context.startActivity(intent);


                }else if (myListMedia.getName() !=null) {


                    Intent intent = new Intent(context, SerieDetailsActivity.class);
                    intent.putExtra(ARG_MOVIE, myListMedia);
                    context.startActivity(intent);

                }else {


                    Intent intent = new Intent(context, MovieDetailsActivity.class);
                    intent.putExtra(ARG_MOVIE, myListMedia);
                    context.startActivity(intent);

                }







            });

            Tools.onLoadMediaCover(binding.itemMovieImage, myListMedia.getPosterPath());

        }
    }
}
