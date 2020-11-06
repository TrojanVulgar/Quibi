package com.easyplex.ui.player.fsm.listener;

import androidx.annotation.NonNull;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.analytics.AnalyticsListener;
import com.easyplex.ui.player.fsm.state_machine.FsmAdController;
import com.easyplex.ui.player.fsm.state_machine.FsmPlayer;
import com.easyplex.ui.player.utilities.EventLogger;


/**
 * Created by allensun on 8/9/17.
 * on Tubitv.com, allengotstuff@gmail.com
 */
public class AdPlayingMonitor extends EventLogger {

    private FsmAdController fsmPlayer;



    public AdPlayingMonitor(@NonNull FsmPlayer fsmPlayer) {
        super(null);
        this.fsmPlayer = fsmPlayer;
    }

    @Override
    public void onPlayerStateChanged(AnalyticsListener.EventTime eventTime, boolean playWhenReady, int playbackState) {
        super.onPlayerStateChanged(eventTime, playWhenReady, playbackState);

        //the last ad has finish playing.
        if (playbackState == Player.STATE_ENDED && playWhenReady) {
            fsmPlayer.removePlayedAdAndTransitToNextState();
        }
    }

    @Override
    public void onPlayerError(AnalyticsListener.EventTime eventTime, ExoPlaybackException error) {
        super.onPlayerError(eventTime, error);
        fsmPlayer.removePlayedAdAndTransitToNextState();
    }

    @Override
    public void onDroppedVideoFrames(final AnalyticsListener.EventTime eventTime, final int droppedFrames, final long elapsedM) {
        super.onDroppedVideoFrames(eventTime, droppedFrames, elapsedM);
        seekOrSkip();
    }

    // this is a hack to handle when played corrupted video file, it stuck in the buffering state forever.
    private void seekOrSkip() {
        if (fsmPlayer == null) {
            return;
        }

        if (fsmPlayer instanceof FsmPlayer && ((FsmPlayer) fsmPlayer).getController() != null) {

            SimpleExoPlayer adPlayer = ((FsmPlayer) fsmPlayer).getController().getAdPlayer();

            if (adPlayer != null && adPlayer.getPlaybackState() == Player.STATE_BUFFERING) {

                long position = Math.min(adPlayer.getCurrentPosition() + 1000, adPlayer.getDuration());
                adPlayer.seekTo(position);
                adPlayer.setPlayWhenReady(true);

            }

        }
    }
}
