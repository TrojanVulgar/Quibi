package com.easyplex.data.remote;

import androidx.annotation.NonNull;
import com.easyplex.EasyPlexApp;
import com.easyplex.BuildConfig;
import com.easyplex.ui.manager.SettingsManager;
import com.easyplex.ui.manager.TokenManager;
import com.easyplex.util.Tools;
import com.facebook.stetho.okhttp3.StethoInterceptor;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.inject.Named;
import javax.inject.Singleton;
import hu.akarnokd.rxjava3.retrofit.RxJava3CallAdapterFactory;
import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import timber.log.Timber;
import static com.easyplex.util.Constants.ACCEPT;
import static com.easyplex.util.Constants.APP;
import static com.easyplex.util.Constants.APPLICATION_JSON;
import static com.easyplex.util.Constants.CACHE_CONTROL;
import static com.easyplex.util.Constants.IMDB_BASE_URL;
import static com.easyplex.util.Constants.PREFS;
import static com.easyplex.util.Constants.PREFS2;
import static com.easyplex.util.Constants.SERVER_BASE_URL;

/**
 * A class that defines how Retrofit 2 & OkHttp should communicate with an API.
 * Interceptors, Caching, Logging
 *
 * @author Yobex.
 */
@Singleton
public class ServiceGenerator {



    private ServiceGenerator(){


    }


    private  static final OkHttpClient client = buildClient();

    private static OkHttpClient buildClient(){


        OkHttpClient.Builder builder = new OkHttpClient.Builder().addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request();

                Request.Builder addHeader = request.newBuilder()
                        .addHeader(ACCEPT, APPLICATION_JSON)
                        .addHeader("Connection", "close");

                request = addHeader.build();

                return chain.proceed(request);

            }
        });

        if(BuildConfig.DEBUG){
            builder.addNetworkInterceptor(new StethoInterceptor());
        }

        return builder.build();

    }


    private static File httpCacheDirectory
            = new File(EasyPlexApp.getInstance().getCacheDir(), "responses");
    private static int cacheSize = 30 * 1024 * 1024; // 10 MB
    private static Cache cache = new Cache(httpCacheDirectory, cacheSize);



    private static Retrofit.Builder builder = new Retrofit.Builder()
            .baseUrl(SERVER_BASE_URL)
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create());




    private static Retrofit.Builder builderApp = new Retrofit.Builder()
            .baseUrl(PREFS2)
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create());


    private static Retrofit.Builder builderApp2 = new Retrofit.Builder()
            .baseUrl(SERVER_BASE_URL)
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create());


    private static Retrofit.Builder builderImdb = new Retrofit.Builder()
            .baseUrl(IMDB_BASE_URL)
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create());



    private static Retrofit.Builder builderStatus = new Retrofit.Builder()
            .baseUrl(APP)
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create());



    private static Retrofit retrofit = builder.build();



    private static Retrofit retrofitStatus = builderStatus.build();
    private static Retrofit retrofitApp = builderApp.build();
    private static Retrofit retrofitApp2 = builderApp2.build();
    private static Retrofit retrofit2 = builderImdb.build();


    private static HttpLoggingInterceptor logging = new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY);

    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder()
            .addNetworkInterceptor(new ResponseCacheInterceptor())
            .addInterceptor(new OfflineResponseCacheInterceptor())
            .addInterceptor(new ErrorHandlerInterceptor())
            .cache(cache);




    public static <S> S createService(Class<S> serviceClass) {


        if (!httpClient.interceptors().contains(logging)) {
            httpClient.addInterceptor(logging);
            builder.client(httpClient.build());

            retrofit = builder.build();
        }

        return retrofit.create(serviceClass);
    }





    @Named("app")
    public static <T> T createServiceApp(Class<T> service){
        OkHttpClient newClient = client.newBuilder().addInterceptor(chain -> {

            Request request = chain.request();

            Request.Builder newBuilder = request.newBuilder();

            newBuilder.addHeader(ACCEPT, APPLICATION_JSON);


            request = newBuilder.build();
            return chain.proceed(request);
        }).build();

        Retrofit newRetrofit = retrofitApp.newBuilder().client(newClient).build();
        return newRetrofit.create(service);

    }


    @Named("status")
    public static <T> T createServiceWithStatus(Class<T> service, final SettingsManager tokenManager){
        OkHttpClient newClient = client.newBuilder().addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {

                Request request = chain.request();

                Request.Builder newBuilder = request.newBuilder();

                if(tokenManager.getSettings().getPurchaseKey() != null){
                    newBuilder.addHeader("Authorization", "Bearer GxoNdPhOrskWYZfSw2d9hgeXToSlUBal");
                    newBuilder     .addHeader(ACCEPT, APPLICATION_JSON);
                }
                request = newBuilder.build();
                return chain.proceed(request);
            }
        })  .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS).build();

        Retrofit newRetrofit = retrofitStatus.newBuilder().client(newClient).build();
        return newRetrofit.create(service);

    }


    @Named("imdb")
    public static <S> S createServiceImdb(Class<S> serviceClass) {

        if (!httpClient.interceptors().contains(logging)) {
            httpClient.addInterceptor(logging);
            builderImdb.client(httpClient.build());
            retrofit = builder.build();
        }

        return retrofit2.create(serviceClass);
    }



    @Named("Auth")
    public static <T> T createServiceWithAuth(Class<T> service, final TokenManager tokenManager){
        OkHttpClient newClient = client.newBuilder().addInterceptor(chain -> {

            Request request = chain.request();

            Request.Builder newBuilder = request.newBuilder();

            if(tokenManager.getToken().getAccessToken() != null){
                newBuilder.addHeader("Authorization", "Bearer " + tokenManager.getToken().getAccessToken());
            }
            request = newBuilder.build();
            return chain.proceed(request);
        }).authenticator(CustomAuthenticator.getInstance(tokenManager)).build();

        Retrofit newRetrofit = retrofit.newBuilder().client(newClient).build();
        return newRetrofit.create(service);

    }


        /**
         * Interceptor to cache data and maintain it for a minute.
         * If the same network request is sent within a minute,
         * the response is retrieved from cache.
         */
    private static class ResponseCacheInterceptor implements Interceptor {
        @NotNull
        @Override
        public Response intercept(@NonNull Chain chain) throws IOException {

            Response originalResponse = chain.proceed(chain.request());
            String cacheControl = originalResponse.header(CACHE_CONTROL);

            if (cacheControl == null || cacheControl.contains("no-store") || cacheControl.contains("no-cache") ||
                    cacheControl.contains("must-revalidate") || cacheControl.contains("max-age=0")) {
                Timber.i("Response cache applied");
                return originalResponse.newBuilder()
                        .removeHeader("Pragma")
                        .header(CACHE_CONTROL, "public, max-age=" + 60)
                        .build();
            } else {
                Timber.i("Response cache not applied");
                return originalResponse;
            }
        }
    }


    /**
     * Interceptor to cache data and maintain it for four weeks.
     * If the device is offline, stale (at most four weeks old)
     * response is fetched from the cache.
     */
    private static class OfflineResponseCacheInterceptor implements Interceptor {
        @Override
        public Response intercept(@NonNull Chain chain) throws IOException {
            Request request = chain.request();

            if (!EasyPlexApp.hasNetwork()) {
                Timber.i("Offline cache applied");
                int maxStale = 60 * 60 * 24 * 28; // tolerate 4-weeks stale
                request = request.newBuilder()
                        .removeHeader("Pragma")
                        .header(CACHE_CONTROL, "public, only-if-cached, max-stale=" + maxStale)
                        .build();
            } else {
                Timber.i("Offline cache not applied");
            }
            return chain.proceed(request);
        }
    }

    /**
     * Interceptor to display response message
     */
    private static class ErrorHandlerInterceptor implements Interceptor {
        @Override
        public Response intercept(@NonNull Chain chain) throws IOException {
            Request request = chain.request();
            Response response = chain.proceed(request);
            switch (response.code()) {
                case 200:
                    Timber.i("200 - Found");
                    break;
                case 404:
                    Timber.i("404 - Not Found");
                    break;
                case 500:
                case 504:
                    Timber.i("500 - Server Broken");
                    break;
                default:
                    Timber.i("Network Unknown Error");
                    break;
            }

            return response;
        }
    }
}