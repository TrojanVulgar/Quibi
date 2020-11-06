package com.easyplex.ui.player.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.easyplex.data.model.media.MediaModel;
import com.easyplex.data.model.stream.MediaStream;
import com.easyplex.databinding.RowSubstitleBinding;
import com.easyplex.ui.player.activities.EasyPlexMainPlayer;

import java.util.List;
import javax.inject.Inject;

/**
 * Adapter for Movie Qualities.
 *
 * @author Yobex.
 */
public class MovieQualitiesAdapter extends RecyclerView.Adapter<MovieQualitiesAdapter.CastViewHolder> {

    private List<MediaStream> mediaStreams;
    private MediaModel mMediaModel;
    private EasyPlexMainPlayer player;

    ClickDetectListner clickDetectListner;



    @Inject
    public MovieQualitiesAdapter(EasyPlexMainPlayer player){

        this.player = player;
    }


    public void addSeasons(List<MediaStream> castList, ClickDetectListner clickDetectListner) {
        this.mediaStreams = castList;
        notifyDataSetChanged();
        this.clickDetectListner = clickDetectListner;

    }

    @NonNull
    @Override
    public MovieQualitiesAdapter.CastViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        RowSubstitleBinding binding = RowSubstitleBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);

        return new MovieQualitiesAdapter.CastViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieQualitiesAdapter.CastViewHolder holder, int position) {
        holder.onBind(position);
    }



    @Override
    public int getItemCount() {
        if (mediaStreams != null) {
            return mediaStreams.size();
        } else {
            return 0;
        }
    }

    class CastViewHolder extends RecyclerView.ViewHolder {

        private final RowSubstitleBinding binding;

        CastViewHolder (@NonNull RowSubstitleBinding binding)
        {
            super(binding.getRoot());

            this.binding = binding;
        }

        void onBind(final int position) {

            final MediaStream qualitySelected = mediaStreams.get(position);

            binding.eptitle.setText(qualitySelected.getLang() + " - "+qualitySelected.getServer());

            binding.eptitle.setOnClickListener(v -> {


                String id = player.getPlayerController().getVideoID();
                String externalId = player.getPlayerController().getMediaSubstitleName();
                String type = player.getPlayerController().getMediaType();
                String currentQuality = player.getPlayerController().getVideoCurrentQuality();
                String artwork = (String.valueOf(player.getPlayerController().getMediaPoster())) ;
                String name = player.getPlayerController().getCurrentVideoName();
                mMediaModel = MediaModel.media(id,externalId,currentQuality,type,name, qualitySelected.getLink(), artwork, null,null,null
                        ,null,null,null,
                        null,
                        null,null,null, player.getPlayerController().getCurrentTvShowName());
                player.update(mMediaModel);
                player.getPlayerController().isSubtitleEnabled(true);
                clickDetectListner.onQualityClicked(true);


            });

        }
    }


}
