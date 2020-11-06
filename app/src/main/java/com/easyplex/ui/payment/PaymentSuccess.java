package com.easyplex.ui.payment;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import com.easyplex.R;
import com.easyplex.databinding.PaymentSuccessBinding;
import com.easyplex.ui.splash.SplashActivity;


import dagger.android.AndroidInjection;

public class PaymentSuccess extends AppCompatActivity {



        PaymentSuccessBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        AndroidInjection.inject(this);

        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.payment_success);



        binding.closePaymentSuccess.setOnClickListener(v -> {


            Intent intent = new Intent(PaymentSuccess.this, SplashActivity.class);
            startActivity(intent);
            finish();

        });


    }
}
