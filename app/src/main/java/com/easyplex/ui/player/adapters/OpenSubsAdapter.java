package com.easyplex.ui.player.adapters;

import android.os.Environment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.easyplex.data.model.media.MediaModel;
import com.easyplex.data.model.substitles.Opensub;
import com.easyplex.databinding.RowSubstitleBinding;
import com.easyplex.ui.player.activities.EasyPlexMainPlayer;
import com.easyplex.util.DownloadFileAsync;
import com.google.android.exoplayer2.util.Log;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.model.FileHeader;
import java.io.File;
import java.io.IOException;
import java.util.List;
import static android.content.ContentValues.TAG;
import static com.easyplex.util.Constants.SUBSTITLE_LOCATION;
import static com.easyplex.util.Constants.SUBSTITLE_SUB_FILENAME_ZIP;
import static com.easyplex.util.Constants.ZIP_FILE_NAME;

/**
 * Adapter for Movie or Serie Substitles.
 *
 * @author Yobex.
 */
public class OpenSubsAdapter extends RecyclerView.Adapter<OpenSubsAdapter.SubstitlesViewHolder> {

    private List<Opensub> mediaSubstitles;
    private MediaModel mMediaModel;
    private EasyPlexMainPlayer doubleViewTubiPlayerActivity;

    ClickDetectListner clickDetectListner;



    public OpenSubsAdapter(EasyPlexMainPlayer doubleViewTubiPlayerActivity){

        this.doubleViewTubiPlayerActivity = doubleViewTubiPlayerActivity;

    }


    public void addSubtitle(List<Opensub> castList, ClickDetectListner clickDetectListner) {
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

            final Opensub mediaSubstitle = mediaSubstitles.get(position);


            binding.eptitle.setText(mediaSubstitle.getLanguageName());


            binding.eptitle.setOnClickListener(v -> {


                DownloadFileAsync download = new DownloadFileAsync(
                        doubleViewTubiPlayerActivity.getApplicationContext()
                                .getExternalFilesDir(Environment.getDataDirectory().getAbsolutePath())
                                +SUBSTITLE_SUB_FILENAME_ZIP, new DownloadFileAsync.PostDownload(){
                    @Override
                    public void downloadDone(File file) throws IOException {
                        Log.i(TAG, "file download completed");
                        // check unzip file now
                        ZipFile zipFile;
                        zipFile = new ZipFile("subs.zip");
                        FileHeader fileHeader;
                        fileHeader = zipFile.getFileHeader(
                                doubleViewTubiPlayerActivity.getApplicationContext()
                                        .getExternalFilesDir(Environment.getDataDirectory()
                                                .getAbsolutePath())+SUBSTITLE_SUB_FILENAME_ZIP);
                        if (fileHeader != null) {
                            zipFile.removeFile(fileHeader);
                        }else {
                            new ZipFile(file, null).extractFile(mediaSubstitle.getSubFileName(),
                                    String.valueOf(doubleViewTubiPlayerActivity.getApplicationContext().getExternalFilesDir(Environment.getDataDirectory().getAbsolutePath()))
                                    , ZIP_FILE_NAME);
                            Log.i(TAG, "file unzip completed");

                        }



                    }
                });


                Toast.makeText(doubleViewTubiPlayerActivity.getApplicationContext(), "The "+mediaSubstitle.getLanguageName()+" will be ready in 5 sec", Toast.LENGTH_LONG).show();

                new Handler().postDelayed(() -> {
                    String subs = SUBSTITLE_LOCATION + doubleViewTubiPlayerActivity.getApplicationContext().getPackageName()+"/files/data/"+ZIP_FILE_NAME;
                    String substitleLanguage = mediaSubstitle.getLanguageName();
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


                }, 5000);


            });

        }
    }


}
