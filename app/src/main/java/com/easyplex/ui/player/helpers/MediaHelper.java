package com.easyplex.ui.player.helpers;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.RenderersFactory;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.exoplayer2.util.Assertions;
import com.google.android.exoplayer2.util.Util;
import com.easyplex.data.model.media.MediaModel;

import java.util.Arrays;
import java.util.LinkedList;

/**
 * Created by stoyan on 6/21/17.
 */

public class MediaHelper {



    private LinkedList<MediaModel> linkedList;
        private static MediaHelper instance;

        public static synchronized  MediaHelper create(@NonNull MediaModel... models) {
            instance = new MediaHelper(models);
           return instance;
        }

        private MediaHelper(MediaModel[] models) {
           linkedList = new LinkedList<>();
            linkedList.addAll(Arrays.asList(models));
       }

        public static synchronized  MediaHelper getInstance() {
            Assertions.checkNotNull(instance);
            return instance;
        }

        public MediaSource getConcatenatedMedia() {
            return new ConcatenatingMediaSource(concatenateMedia());
        }

        private MediaSource concatenateMedia() {
            MediaSource[] mediaSources = new MediaSource[linkedList.size()];
            for (int i = 0; i < linkedList.size(); i++) {
               mediaSources[i] = linkedList.get(i).getMediaSource();
            }
           return new ConcatenatingMediaSource(mediaSources);
       }


    public static
    @NonNull
    DataSource.Factory buildDataSourceFactory(@NonNull Context context, DefaultBandwidthMeter.Builder bandwidthMeter) {

        return new DefaultDataSourceFactory(context, bandwidthMeter.build(), buildHttpDataSourceFactory(context, bandwidthMeter.build()));}


    public static RenderersFactory buildRenderersFactory(Context context) {
            return new DefaultRenderersFactory(context.getApplicationContext()).setExtensionRendererMode(DefaultRenderersFactory.EXTENSION_RENDERER_MODE_PREFER);
    }


    // put user agent in meta or attrs
    public static
    @NonNull
    HttpDataSource.Factory buildHttpDataSourceFactory(@NonNull Context context,
                                                      @NonNull DefaultBandwidthMeter bandwidthMeter) {
        return new DefaultHttpDataSourceFactory(Util.getUserAgent(context, "EasyPlexPlayer"), bandwidthMeter);
    }

}
