package com.easyplex.ui.animes;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.easyplex.data.local.entity.Media;
import com.easyplex.data.model.genres.Genre;
import com.easyplex.databinding.ItemAnimeBinding;
import com.easyplex.util.Tools;
import java.util.List;
import static com.easyplex.util.Constants.ARG_MOVIE;

/**
 * Adapter for Animes.
 *
 * @author Yobex.
 */
public class AnimesAdapter extends RecyclerView.Adapter<AnimesAdapter.AnimeViewHolder> {

    private List<Media> animeList;
    private Context context;


    public void addToContent(List<Media> castList,Context context) {
        this.animeList = castList;
        this.context = context;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AnimeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        ItemAnimeBinding binding = ItemAnimeBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);

        return new AnimesAdapter.AnimeViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull AnimeViewHolder holder, int position) {
        holder.onBind(position);
    }

    @Override
    public int getItemCount() {
        if (animeList != null) {
            return animeList.size();
        } else {
            return 0;
        }
    }

    class AnimeViewHolder extends RecyclerView.ViewHolder {


        private final ItemAnimeBinding binding;


        AnimeViewHolder (@NonNull ItemAnimeBinding binding)
        {
            super(binding.getRoot());

            this.binding = binding;
        }

        void onBind(final int position) {


            final Media anime = animeList.get(position);

            binding.movietitle.setText(anime.getName());


            for (Genre genre : anime.getGenres()) {

                binding.mgenres.setText(genre.getName());


            }

            binding.rootLayout.setOnClickListener(view -> {


                Intent intent = new Intent(context, AnimeDetailsActivity.class);
                intent.putExtra(ARG_MOVIE, anime);
                context.startActivity(intent);


            });

            Tools.onLoadMediaCover(binding.itemMovieImage,anime.getPosterPath());


        }
    }
}
