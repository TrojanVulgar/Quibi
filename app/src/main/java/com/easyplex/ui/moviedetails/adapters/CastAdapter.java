package com.easyplex.ui.moviedetails.adapters;


import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.easyplex.data.model.credits.Cast;
import com.easyplex.databinding.ListItemCastBinding;
import com.easyplex.ui.manager.SettingsManager;
import com.easyplex.util.Tools;
import java.util.List;
import javax.inject.Inject;


/**
 * Adapter for Movie Casts.
 *
 * @author Yobex.
 */
public class CastAdapter extends RecyclerView.Adapter<CastAdapter.CastViewHolder> {

    private List<Cast> castList;

    @Inject
    SettingsManager settingsManager;


    public CastAdapter(SettingsManager settingsManager) {

        this.settingsManager = settingsManager;
    }



    public void addCasts(List<Cast> castList) {
        this.castList = castList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CastAdapter.CastViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        ListItemCastBinding binding = ListItemCastBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);

        return new CastAdapter.CastViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CastAdapter.CastViewHolder holder, int position) {
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

    class CastViewHolder extends RecyclerView.ViewHolder {

        private final ListItemCastBinding binding;


        CastViewHolder(@NonNull ListItemCastBinding binding)
        {
            super(binding.getRoot());

            this.binding = binding;
        }

        void onBind(final int position) {

            final Cast cast = castList.get(position);

            Tools.onLoadMediaCover(binding.imageCast,settingsManager.getSettings().getImdbCoverPath() + cast.getProfilePath());

            binding.castName.setText(cast.getName());

        }
    }
}
