package com.easyplex.ui.notifications;


import android.app.NotificationChannel;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.easyplex.R;
import com.easyplex.data.local.entity.Media;
import com.easyplex.data.model.media.MediaModel;
import com.easyplex.data.repository.AnimeRepository;
import com.easyplex.data.repository.MediaRepository;
import com.easyplex.ui.animes.AnimeDetailsActivity;
import com.easyplex.ui.moviedetails.MovieDetailsActivity;
import com.easyplex.ui.player.activities.EasyPlexMainPlayer;
import com.easyplex.ui.player.activities.EasyPlexPlayerActivity;
import com.easyplex.ui.seriedetails.SerieDetailsActivity;
import com.easyplex.ui.splash.SplashActivity;
import com.easyplex.util.Constants;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

import javax.inject.Inject;
import dagger.android.AndroidInjection;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import timber.log.Timber;

import static com.easyplex.util.Constants.ARG_MOVIE;


/**
 * EasyPlex - Android Movie Portal App
 * @package     EasyPlex - Android Movie Portal App
 * @author      @Y0bEX
 * @copyright   Copyright (c) 2020 Y0bEX,
 * @license     http://codecanyon.net/wiki/support/legal-terms/licensing-terms/
 * @profile     https://codecanyon.net/user/yobex
 * @link        yobexd@gmail.com
 * @skype       yobexd@gmail.com
 **/


public class NotificationManager extends FirebaseMessagingService {


    private static final String CHANNEL_ID = "CHANNEL_ID";

    @Inject
    MediaRepository repository;

    @Inject
    AnimeRepository animeRepository;


    @Override
    public void onMessageReceived(@NotNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        AndroidInjection.inject(this);


        if (remoteMessage.getData().size() > 0) {

            createNotification(remoteMessage);

        }

    }


    private void createNotification(RemoteMessage remoteMessage) {

        Map<String, String> remoteData = remoteMessage.getData();


        String imdb = remoteData.get("tmdb");
        String type = remoteData.get("type");
        String title = remoteData.get("title");
        String message = remoteData.get("message");

        Timber.i("Type : + %s", type);
        Timber.i("Title : + %s", title);
        Timber.i("Message : + %s", message);

        switch (type) {
            case "0": {

                repository.getMovie(imdb)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<Media>() {
                            @Override
                            public void onSubscribe(Disposable d) {

                                //

                            }

                            @Override
                            public void onNext(Media movieDetail) {

                                Intent intent = new Intent(NotificationManager.this, MovieDetailsActivity.class);
                                intent.putExtra(ARG_MOVIE, movieDetail);
                                TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());
                                stackBuilder.addNextIntentWithParentStack(intent);


                                PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

                                Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                                NotificationCompat.Builder notificationBuilder =
                                        new NotificationCompat.Builder(NotificationManager.this, CHANNEL_ID)
                                                .setSmallIcon(R.drawable.notification_smal_size)
                                                .setContentTitle(title)
                                                .setContentText(message)
                                                .setAutoCancel(true)
                                                .setSound(defaultSoundUri)
                                                .setContentIntent(resultPendingIntent);


                                android.app.NotificationManager notificationManager = (android.app.NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                                // Since android Oreo notification channel is needed.
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                                            "EasyPlex",
                                            android.app.NotificationManager.IMPORTANCE_DEFAULT);
                                    notificationManager.createNotificationChannel(channel);
                                }

                                notificationManager.notify(0, notificationBuilder.build());

                            }


                            @Override
                            public void onError(Throwable e) {


                            }

                            @Override
                            public void onComplete() {

                                //

                            }
                        });



                break;
            }
            case "1": {
                repository.getSerie(imdb)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<Media>() {
                            @Override
                            public void onSubscribe(Disposable d) {

                                //

                            }

                            @Override
                            public void onNext(Media movieDetail) {

                                Intent intent = new Intent(NotificationManager.this, SerieDetailsActivity.class);
                                intent.putExtra(ARG_MOVIE, movieDetail);
                                TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());
                                stackBuilder.addNextIntentWithParentStack(intent);


                                PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

                                Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                                NotificationCompat.Builder notificationBuilder =
                                        new NotificationCompat.Builder(NotificationManager.this, CHANNEL_ID)
                                                .setSmallIcon(R.drawable.notification_smal_size)
                                                .setContentTitle(title)
                                                .setContentText(message)
                                                .setAutoCancel(true)
                                                .setSound(defaultSoundUri)
                                                .setContentIntent(resultPendingIntent);


                                android.app.NotificationManager notificationManager = (android.app.NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                                // Since android Oreo notification channel is needed.
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                                            "EasyPlex",
                                            android.app.NotificationManager.IMPORTANCE_DEFAULT);
                                    notificationManager.createNotificationChannel(channel);
                                }

                                notificationManager.notify(0, notificationBuilder.build());

                            }


                            @Override
                            public void onError(Throwable e) {


                            }

                            @Override
                            public void onComplete() {

                                //

                            }
                        });

                break;
            }
            case "2":

                animeRepository.getAnimeDetails(imdb)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<Media>() {
                            @Override
                            public void onSubscribe(Disposable d) {

                                //

                            }

                            @Override
                            public void onNext(Media movieDetail) {



                                Intent intent = new Intent(NotificationManager.this, AnimeDetailsActivity.class);
                                intent.putExtra(ARG_MOVIE, movieDetail);
                                TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());
                                stackBuilder.addNextIntentWithParentStack(intent);


                                PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

                                Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                                NotificationCompat.Builder notificationBuilder =
                                        new NotificationCompat.Builder(NotificationManager.this, CHANNEL_ID)
                                                .setSmallIcon(R.drawable.notification_smal_size)
                                                .setContentTitle(title)
                                                .setContentText(message)
                                                .setAutoCancel(true)
                                                .setSound(defaultSoundUri)
                                                .setContentIntent(resultPendingIntent);


                                android.app.NotificationManager notificationManager = (android.app.NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                                // Since android Oreo notification channel is needed.
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                    NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                                            "EasyPlex",
                                            android.app.NotificationManager.IMPORTANCE_DEFAULT);
                                    notificationManager.createNotificationChannel(channel);
                                }

                                notificationManager.notify(0, notificationBuilder.build());

                            }


                            @Override
                            public void onError(Throwable e) {


                            }

                            @Override
                            public void onComplete() {

                                //

                            }
                        });


                break;

                case "3":

                    repository.getLiveTvById(imdb)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Observer<Media>() {
                                @Override
                                public void onSubscribe(Disposable d) {

                                    //

                                }

                                @Override
                                public void onNext(Media movieDetail) {

                                    String artwork = movieDetail.getPosterPath();
                                    String name = movieDetail.getName();
                                    String videourl = movieDetail.getLink();
                                    String type = "streaming";
                                    Intent intent = new Intent(NotificationManager.this, EasyPlexMainPlayer.class);
                                    intent.putExtra(EasyPlexPlayerActivity.EASYPLEX_MEDIA_KEY, MediaModel.media(movieDetail.getId(),null,null,type, name, videourl, artwork, null
                                            , null, null,null,
                                            null,null,
                                            null,
                                            null,null,null,null));
                                    intent.putExtra(ARG_MOVIE, movieDetail);
                                    startActivity(intent);


                                    TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());
                                    stackBuilder.addNextIntentWithParentStack(intent);


                                    PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

                                    Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                                    NotificationCompat.Builder notificationBuilder =
                                            new NotificationCompat.Builder(NotificationManager.this, CHANNEL_ID)
                                                    .setSmallIcon(R.drawable.notification_smal_size)
                                                    .setContentTitle(title)
                                                    .setContentText(message)
                                                    .setAutoCancel(true)
                                                    .setSound(defaultSoundUri)
                                                    .setContentIntent(resultPendingIntent);


                                    android.app.NotificationManager notificationManager = (android.app.NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                                    // Since android Oreo notification channel is needed.
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                        NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                                                "EasyPlex",
                                                android.app.NotificationManager.IMPORTANCE_DEFAULT);
                                        notificationManager.createNotificationChannel(channel);
                                    }

                                    notificationManager.notify(0, notificationBuilder.build());

                                }


                                @Override
                                public void onError(Throwable e) {


                                }

                                @Override
                                public void onComplete() {

                                    //

                                }
                            });

                break;
            default: {
                Intent intent = new Intent(this, SplashActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                break;
            }
        }

    }


}
