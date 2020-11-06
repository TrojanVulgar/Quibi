package com.easyplex.ui.payment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.easyplex.R;
import com.easyplex.data.model.plans.Plan;
import com.easyplex.data.remote.ErrorHandling;
import com.easyplex.ui.viewmodels.LoginViewModel;
import com.easyplex.ui.manager.SettingsManager;
import com.easyplex.ui.splash.SplashActivity;
import com.easyplex.util.DialogHelper;
import com.easyplex.util.Tools;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentConfirmation;
import com.stripe.android.ApiResultCallback;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.PaymentIntentResult;
import com.stripe.android.Stripe;
import com.stripe.android.model.PaymentIntent;
import com.stripe.android.model.PaymentMethod;
import com.stripe.android.model.PaymentMethodCreateParams;
import com.stripe.android.model.StripeIntent.Status;
import com.stripe.android.view.CardInputWidget;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;

import java.lang.ref.WeakReference;
import java.math.BigDecimal;

import javax.inject.Inject;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import dagger.android.AndroidInjection;
import timber.log.Timber;

import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static com.easyplex.util.Constants.ARG_PAYMENT;
import static com.easyplex.util.Constants.PAYMENT;

/**
 * HoneyStream - Android Movie Portal App
 * @package     HoneyStream - Android Movie Portal App
 * @author      @Y0bEX
 * @copyright   Copyright (c) 2020 Y0bEX,
 * @license     http://codecanyon.net/wiki/support/legal-terms/licensing-terms/
 * @profile     https://codecanyon.net/user/yobex
 * @link        yobexd@gmail.com
 * @skype       yobexd@gmail.com
 **/


public class Payment extends AppCompatActivity {


    private Unbinder unbinder;


    private LoginViewModel loginViewModel;




    @BindView(R.id.cardInputWidget)
    CardInputWidget cardInputWidget;

    @BindView(R.id.paypal_method)
    TextView paypalMethod;


    @BindView(R.id.sumbit_subscribe)
    Button sumbitSubscribe;



    @BindView(R.id.form_container)
    LinearLayout formContainer;


    @BindView(R.id.loader)
    ProgressBar loader;


    private Stripe mStripe;


    @Inject
    SettingsManager settingsManager;
    private static int paypalCode = 7777;

    @Inject
    ViewModelProvider.Factory viewModelFactory;


    private String planId;
    private String planPrice;
    private String planName;
    private String planDuraction;
    private Plan plan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidInjection.inject(this);

        setContentView(R.layout.payment_activity);

        unbinder = ButterKnife.bind(this);


        loginViewModel = new ViewModelProvider(this, viewModelFactory).get(LoginViewModel.class);



        Intent intent = getIntent();
        Plan plan = intent.getParcelableExtra(ARG_PAYMENT);


        this.planId =  plan.getstripePlanId();
        this.planPrice = plan.getStripePlanPrice();
        this.planName = plan.getName();
        this.planDuraction = plan.getPackDuration();
        this.plan = plan;


        Tools.hideSystemPlayerUi(this, true, 0);

        Tools.setSystemBarTransparent(this);


        // Configure the SDK with your Stripe publishable key so that it can make requests to the Stripe API
        final Context applicationContext = getApplicationContext();



       PaymentConfiguration.init(applicationContext, settingsManager.getSettings().getStripePublishableKey());
       mStripe = new Stripe(applicationContext, settingsManager.getSettings().getStripePublishableKey());





        // Hook up the pay button to the card widget and stripe instance

        sumbitSubscribe.setOnClickListener(v -> {



            formContainer.setVisibility(View.GONE);
            loader.setVisibility(View.VISIBLE);

            CardInputWidget cardInputWidget = findViewById(R.id.cardInputWidget);
            PaymentMethodCreateParams params = cardInputWidget.getPaymentMethodCreateParams();

            if (params == null) {
                return;
            }
            mStripe.createPaymentMethod(params, new ApiResultCallback<PaymentMethod>() {
                @SuppressLint("SimpleDateFormat")
                @Override
                public void onSuccess(@NonNull PaymentMethod result) {


                onSuccessPayment(result.id, null);


                }

                @Override
                public void onError(@NonNull Exception e) {


                    //

                }
            });


        });

        paypalMethod.setOnClickListener(v -> {


            if (settingsManager.getSettings().getPaypalClientId() != null) {

                PayPalConfiguration config = new PayPalConfiguration().environment(PayPalConfiguration.ENVIRONMENT_PRODUCTION)
                        .clientId(settingsManager.getSettings().getPaypalClientId());
                PayPalPayment payPalPayment = new PayPalPayment(new BigDecimal(plan.getPrice()),"USD",
                        plan.getName(),PayPalPayment.PAYMENT_INTENT_SALE);
                Intent payment = new Intent(Payment.this, com.paypal.android.sdk.payments.PaymentActivity.class);
                payment.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION,config);
                payment.putExtra(com.paypal.android.sdk.payments.PaymentActivity.EXTRA_PAYMENT,payPalPayment);
                startActivityForResult(payment, paypalCode);


            } else {


                DialogHelper.showPaypalWarning(Payment.this);
            }

        });




    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // Handle the result of stripe.confirmPayment
        mStripe.onPaymentResult(requestCode, data, new PaymentResultCallback(this));


        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == paypalCode) {
            //If the result is OK i.e. user has not canceled the payment
            if (resultCode == Activity.RESULT_OK) {
                //Getting the payment confirmation
                PaymentConfirmation confirm = data.getParcelableExtra(com.paypal.android.sdk.payments.PaymentActivity.EXTRA_RESULT_CONFIRMATION);

                Timber.d(String.valueOf(confirm));
                //if confirmation is not null
                if (confirm != null) {
                    try {
                        //Getting the payment details
                        String paymentDetails = confirm.toJSONObject().toString(4);
                        Timber.d(paymentDetails);
                        Timber.i(paymentDetails);
                        Timber.d(String.valueOf(confirm.getPayment().toJSONObject()));
//                        Starting a new activity for the payment details and status to show

                        Intent intent = new Intent(Payment.this, PaymentDetails.class);
                        intent.putExtra("PaymentDetails", paymentDetails);
                        intent.putExtra(PAYMENT, plan);
                        startActivity(intent);


                    } catch (JSONException e) {
                        Timber.e(e, "an extremely unlikely failure occurred : ");
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Timber.i("The user canceled.");
            } else if (resultCode == com.paypal.android.sdk.payments.PaymentActivity.RESULT_EXTRAS_INVALID) {
                Timber.i("An invalid Payment or PayPalConfiguration was submitted. Please see the docs.");
            }
        }
    }




    private class PaymentResultCallback implements ApiResultCallback<PaymentIntentResult> {
        private final WeakReference<Payment> activityRef;

        PaymentResultCallback(@NonNull Payment activity) {
            activityRef = new WeakReference<>(activity);
        }

        @Override
        public void onSuccess(@NonNull PaymentIntentResult result) {
            final Payment activity = activityRef.get();
            if (activity == null) {
                return;
            }

            PaymentIntent paymentIntent = result.getIntent();
            Status status = paymentIntent.getStatus();
            if (status == Status.Succeeded) {
                // Payment completed successfully
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                String data = gson.toJson(paymentIntent);

                onSuccessPayment(data, status);



            } else if (status == Status.RequiresPaymentMethod) {
                Toast.makeText(activity, "Payment failed", Toast.LENGTH_SHORT).show();
            }
        }





        @Override
        public void onError(@NotNull Exception e) {


            //

        }
    }



    public void onSuccessPayment(@Nullable String paymentMethodId, Status status){


        loginViewModel.getSubscribePlan(paymentMethodId, planId, planPrice, planName, planDuraction).observe(this, login -> {

            if (login.status == ErrorHandling.Status.SUCCESS) {


                formContainer.setVisibility(View.GONE);
                loader.setVisibility(View.GONE);


                final Dialog dialog = new Dialog(this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
                dialog.setContentView(R.layout.dialog_success_payment);
                dialog.setCancelable(false);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(dialog.getWindow().getAttributes());
                lp.width = WRAP_CONTENT;
                lp.height = WRAP_CONTENT;

                dialog.findViewById(R.id.btn_start_watching).setOnClickListener(v -> {


                    Intent intent = new Intent(Payment.this, SplashActivity.class);
                    startActivity(intent);
                    finish();


                });


                dialog.show();
                dialog.getWindow().setAttributes(lp);


            } else {


                formContainer.setVisibility(View.VISIBLE);
                loader.setVisibility(View.GONE);

                DialogHelper.erroPayment(this);


            }

        });


    }





    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent = new Intent(this, SplashActivity.class);
        startActivity(intent);
        finish();

        unbinder.unbind();


    }
}