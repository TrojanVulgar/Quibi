package com.easyplex.ui.player.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.easyplex.data.model.media.MediaModel;
import com.easyplex.data.model.substitles.MediaSubstitle;
import com.easyplex.databinding.RowSubstitleBinding;
import com.easyplex.ui.player.activities.EasyPlexMainPlayer;

import java.util.List;

/**
 * Adapter for Movie or Serie Substitles.
 *
 * @author Yobex.
 */
public class SubstitlesAdapter extends RecyclerView.Adapter<SubstitlesAdapter.SubstitlesViewHolder> {

    private List<MediaSubstitle> mediaSubstitles;
    private MediaModel mMediaModel;
    private EasyPlexMainPlayer doubleViewTubiPlayerActivity;


    ClickDetectListner clickDetectListner;



    public SubstitlesAdapter(EasyPlexMainPlayer doubleViewTubiPlayerActivity){

        this.doubleViewTubiPlayerActivity = doubleViewTubiPlayerActivity;

    }


    public void addSubtitle(List<MediaSubstitle> castList, ClickDetectListner clickDetectListner) {
        this.mediaSubstitles = castList;
        notifyDataSetChanged();
        this.clickDetectListner = clickDetectListner;

    }

    @NonNull
    @Override
    public SubstitlesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        RowSubstitleBinding binding = RowSubstitleBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);

        return new SubstitlesViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull SubstitlesViewHolder holder, int position) {
        holder.onBind(position);
    }



    @Override
    public int getItemCount() {
        if (mediaSubstitles != null) {
            return mediaSubstitles.size();
        } else {
            return 0;
        }
    }

    class SubstitlesViewHolder extends RecyclerView.ViewHolder {

        private final RowSubstitleBinding binding;

        SubstitlesViewHolder (@NonNull RowSubstitleBinding binding)
        {
            super(binding.getRoot());

            this.binding = binding;
        }

        void onBind(final int position) {

            final MediaSubstitle mediaSubstitle = mediaSubstitles.get(position);



            binding.eptitle.setText(mediaSubstitle.getLang());

            binding.eptitle.setOnClickListener(v -> {


                String subs = mediaSubstitle.getLink();

                String substitleLanguage = mediaSubstitle.getLang();

                String id = doubleViewTubiPlayerActivity.getPlayerController().getVideoID();
                String type = doubleViewTubiPlayerActivity.getPlayerController().getMediaType();
                String currentQuality = doubleViewTubiPlayerActivity.getPlayerController().getVideoCurrentQuality();
                String artwork = (String.valueOf(doubleViewTubiPlayerActivity.getPlayerController().getMediaPoster())) ;
                String name = doubleViewTubiPlayerActivity.getPlayerController().getCurrentVideoName();
                String videoUrl = (String.valueOf(doubleViewTubiPlayerActivity.getPlayerController().getVideoUrl())) ;
                mMediaModel = MediaModel.media(id,substitleLanguage,currentQuality,type,name, videoUrl, artwork, subs,null,null
                        ,null,null,null,null,null,
                        null,null,doubleViewTubiPlayerActivity.getPlayerController().getCurrentTvShowName());
                doubleViewTubiPlayerActivity.update(mMediaModel);
                doubleViewTubiPlayerActivity.getPlayerController().isSubtitleEnabled(true);
                clickDetectListner.onSubstitleClicked(true);


            });

        }
    }


}
