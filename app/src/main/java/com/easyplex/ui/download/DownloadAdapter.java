package com.easyplex.ui.download;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.easyplex.R;
import com.easyplex.data.local.entity.Download;
import com.easyplex.data.model.media.MediaModel;
import com.easyplex.data.repository.MediaRepository;
import com.easyplex.databinding.ListItemDownloadBinding;
import com.easyplex.ui.player.activities.EasyPlexMainPlayer;
import com.easyplex.ui.player.activities.EasyPlexPlayerActivity;
import java.io.File;
import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

import static com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions.withCrossFade;


/**
 * Adapter for  Upcoming Movies
 *
 * @author Yobex.
 */
public class DownloadAdapter extends RecyclerView.Adapter<DownloadAdapter.UpcomingViewHolder> {

    private List<Download> upcomingList;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private MediaRepository mediaRepository;

    public void addCasts(List<Download> castList, MediaRepository mediaRepository) {
        this.upcomingList = castList;
        this.mediaRepository = mediaRepository;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public UpcomingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        ListItemDownloadBinding binding = ListItemDownloadBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);

        return new DownloadAdapter.UpcomingViewHolder(binding);
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

        private final ListItemDownloadBinding binding;


        UpcomingViewHolder (@NonNull ListItemDownloadBinding binding)
        {
            super(binding.getRoot());

            this.binding = binding;
        }

        void onBind(final int position) {

            final Download download = upcomingList.get(position);


            Context context = binding.imageViewShowCard.getContext();

            Glide.with(context).asBitmap().load(download.getBackdropPath())
                    .fitCenter()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .transition(withCrossFade())
                    .into(binding.imageViewShowCard);

            binding.textViewTitleShowCard.setText(download.getTitle());


            binding.textViewDelete.setOnClickListener(v -> {


                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Do you Realy want to delete this movie from your List ?");
                builder.setPositiveButton(R.string.AGREE, (dialogInterface, i) -> {

                    compositeDisposable.add(Completable.fromAction(() -> mediaRepository.removeDownload(download))
                            .subscribeOn(Schedulers.io())
                            .subscribe());


                    File f = new File(download.getLink());
                    f.delete();



                });
                builder.setNegativeButton(R.string.DISAGREE, null);
                builder.show();

            });

            binding.upcomingRelative.setOnClickListener(v -> {

                Intent intent = new Intent(context, EasyPlexMainPlayer.class);
                intent.putExtra(EasyPlexPlayerActivity.EASYPLEX_MEDIA_KEY, MediaModel.media(download.getTmdbId(),null,null,null, download.getTitle(),
                        download.getLink(), download.getBackdropPath(), null, null
                        , null,null,null,null,null,null,null,null,null));
                context.startActivity(intent);



            });


        }
    }


}
