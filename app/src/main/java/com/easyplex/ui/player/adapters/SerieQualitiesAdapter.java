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
public class SerieQualitiesAdapter extends RecyclerView.Adapter<SerieQualitiesAdapter.SerieQualitiesViewHolder> {

    private List<MediaStream> episodeStreams;
    private MediaModel mMediaModel;
    private EasyPlexMainPlayer player;

    ClickDetectListner clickDetectListner;



    @Inject
    public SerieQualitiesAdapter(EasyPlexMainPlayer player){

        this.player = player;

    }


    public void addQuality(List<MediaStream> castList, ClickDetectListner clickDetectListner) {
        this.episodeStreams = castList;
        notifyDataSetChanged();
        this.clickDetectListner = clickDetectListner;

    }

    @NonNull
    @Override
    public SerieQualitiesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        RowSubstitleBinding binding = RowSubstitleBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);

        return new SerieQualitiesAdapter.SerieQualitiesViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull SerieQualitiesViewHolder holder, int position) {
        holder.onBind(position);
    }



    @Override
    public int getItemCount() {
        if (episodeStreams != null) {
            return episodeStreams.size();
        } else {
            return 0;
        }
    }

    class SerieQualitiesViewHolder extends RecyclerView.ViewHolder {

        private final RowSubstitleBinding binding;

        SerieQualitiesViewHolder (@NonNull RowSubstitleBinding binding)
        {
            super(binding.getRoot());

            this.binding = binding;
        }
        void onBind(final int position) {

            final MediaStream qualitySelected = episodeStreams.get(position);


            binding.eptitle.setText(qualitySelected.getServer());


            binding.eptitle.setOnClickListener(v -> {

                String id = player.getPlayerController().getVideoID();
                String externalId = player.getPlayerController().getMediaSubstitleName();
                String type = player.getPlayerController().getMediaType();
                String currentQuality = player.getPlayerController().getVideoCurrentQuality();
                String artwork = (String.valueOf(player.getPlayerController().getMediaPoster())) ;
                String name = player.getPlayerController().getCurrentVideoName();
                mMediaModel = MediaModel.media(id,externalId,currentQuality,type,name, qualitySelected.getLink(), artwork, null,null,null
                        ,null,null,
                        null,null,
                        null,null,null, player.getPlayerController().getCurrentTvShowName());
                player.update(mMediaModel);
                player.getPlayerController().isSubtitleEnabled(true);
                clickDetectListner.onQualityClicked(true);


            });

        }
    }


}
