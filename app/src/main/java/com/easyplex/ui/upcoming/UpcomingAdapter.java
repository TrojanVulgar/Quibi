package com.easyplex.ui.upcoming;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.easyplex.data.model.upcoming.Upcoming;
import com.easyplex.databinding.ItemUpcomingBinding;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import timber.log.Timber;

import static com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions.withCrossFade;
import static com.easyplex.util.Constants.ARG_MOVIE;

/**
 * Adapter for  Upcoming Movies
 *
 * @author Yobex.
 */
public class UpcomingAdapter extends RecyclerView.Adapter<UpcomingAdapter.UpcomingViewHolder> {

    private List<Upcoming> upcomingList;

    public void addCasts(List<Upcoming> castList) {
        this.upcomingList = castList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public UpcomingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        ItemUpcomingBinding binding = ItemUpcomingBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);

        return new UpcomingAdapter.UpcomingViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull UpcomingViewHolder holder, int position) {
        holder.onBind(position);

    }

    @Override
    public int getItemCount() {
        if (upcomingList != null) {
            return upcomingList.size();
        } else {
            return 0;
        }
    }

    class UpcomingViewHolder extends RecyclerView.ViewHolder {

        private final ItemUpcomingBinding binding;


        UpcomingViewHolder (@NonNull ItemUpcomingBinding binding)
        {
            super(binding.getRoot());

            this.binding = binding;
        }

        void onBind(final int position) {

            final Upcoming upcoming = upcomingList.get(position);


            Context context = binding.imageViewShowCard.getContext();

            Glide.with(context).asBitmap().load(upcoming.getBackdropPath())
                    .fitCenter()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .transition(withCrossFade())
                    .into(binding.imageViewShowCard);

            binding.textViewTitleShowCard.setText(upcoming.getTitle());


            if (upcoming.getReleaseDate() != null && !upcoming.getReleaseDate().trim().isEmpty()) {
                SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy.MM.dd");
                try {
                    Date releaseDate = sdf1.parse(upcoming.getReleaseDate());
                    binding.releaseShowCard.setText(sdf2.format(releaseDate));
                } catch (ParseException e) {

                    Timber.d("%s", Arrays.toString(e.getStackTrace()));

                }
            } else {
                binding.releaseShowCard.setText("");
            }

            binding.textViewGenreShowCard.setText(upcoming.getGenre());


            binding.upcomingRelative.setOnClickListener(v -> {

                Intent intent = new Intent(context, UpcomingTitlesActivity.class);
                intent.putExtra(ARG_MOVIE, upcoming);
                context.startActivity(intent);
                Animatoo.animateFade(context);


            });


        }
    }


}
