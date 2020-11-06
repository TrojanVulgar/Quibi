package com.easyplex.ui.plans;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.easyplex.data.model.plans.Plan;
import com.easyplex.databinding.ItemPlansBinding;
import com.easyplex.ui.manager.SettingsManager;
import com.easyplex.ui.payment.Payment;
import com.easyplex.util.DialogHelper;

import java.util.List;

import static com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions.withCrossFade;
import static com.easyplex.util.Constants.PAYMENT;

/**
 * Adapter for  Plans
 *
 * @author Yobex.
 */
public class PlansAdapter extends RecyclerView.Adapter<PlansAdapter.UpcomingViewHolder> {

    private List<Plan> planList;
    private SettingsManager settingsManager;


    public void addCasts(List<Plan> castList,SettingsManager settingsManager) {
        this.planList = castList;
        this.settingsManager = settingsManager;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public UpcomingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        ItemPlansBinding binding = ItemPlansBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);

        return new PlansAdapter.UpcomingViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull UpcomingViewHolder holder, int position) {
        holder.onBind(position);

    }

    @Override
    public int getItemCount() {
        if (planList != null) {
            return planList.size();
        } else {
            return 0;
        }
    }

    class UpcomingViewHolder extends RecyclerView.ViewHolder {

        private final ItemPlansBinding binding;


        UpcomingViewHolder (@NonNull ItemPlansBinding binding)
        {
            super(binding.getRoot());

            this.binding = binding;
        }

        void onBind(final int position) {

            final Plan plan = planList.get(position);


            Context context = binding.cardviewPayment.getContext();

            binding.planName.setText(plan.getName());

            binding.planPrice.setText(plan.getPrice());

            binding.planDescription.setText(plan.getDescription());


            binding.cardviewPayment.setOnClickListener(v -> {


                if (settingsManager.getSettings().getStripePublishableKey() !=null && settingsManager.getSettings().getStripeSecretKey() !=null) {

                    Intent intent = new Intent(context, Payment.class);
                    intent.putExtra(PAYMENT, plan);
                    context.startActivity(intent);

                } else {

                    DialogHelper.showStripeWarning(context);

                }



            });

            binding.subscribeButon.setOnClickListener(v -> {


                if (settingsManager.getSettings().getStripePublishableKey() !=null && settingsManager.getSettings().getStripeSecretKey() !=null) {

                    Intent intent = new Intent(context, Payment.class);
                    intent.putExtra(PAYMENT, plan);
                    context.startActivity(intent);

                } else {

                    DialogHelper.showStripeWarning(context);

                }


            });


        }
    }


}
