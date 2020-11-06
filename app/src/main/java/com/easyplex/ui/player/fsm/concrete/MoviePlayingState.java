package com.easyplex.ui.player.fsm.concrete;

import android.view.View;
import android.webkit.WebView;

import androidx.annotation.NonNull;

import com.easyplex.util.Constants;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.easyplex.ui.player.bindings.PlayerController;
import com.easyplex.ui.player.controller.PlayerAdLogicController;
import com.easyplex.ui.player.controller.PlayerUIController;
import com.easyplex.ui.player.fsm.BaseState;
import com.easyplex.ui.player.fsm.Input;
import com.easyplex.ui.player.fsm.State;
import com.easyplex.ui.player.fsm.concrete.factory.StateFactory;
import com.easyplex.ui.player.fsm.state_machine.FsmPlayer;
import com.easyplex.data.model.media.MediaModel;
import com.easyplex.ui.player.utilities.PlayerDeviceUtils;
import com.easyplex.ui.player.views.TubiExoPlayerView;


/**
 * Created by allensun on 7/27/17.
 */
public class MoviePlayingState extends BaseState {


    private boolean isPlayingAds = false;

    @Override
    public State transformToState(Input input, StateFactory factory) {

        if (input == Input.MAKE_AD_CALL) {
            return factory.createState(MakingAdCallState.class);
        } else if (input == Input.MOVIE_FINISH) {
            return factory.createState(FinishState.class);
        }

        return null;
    }

    @Override
    public void performWorkAndUpdatePlayerUI(@NonNull FsmPlayer fsmPlayer) {
        super.performWorkAndUpdatePlayerUI(fsmPlayer);

        if (isNull(fsmPlayer)) {
            return;
        }

        stopAdandPlayerMovie(controller, componentController, movieMedia);
    }


    private void stopAdandPlayerMovie(PlayerUIController controller, PlayerAdLogicController componentController,
                                      MediaModel movieMedia) {

        SimpleExoPlayer adPlayer = controller.getAdPlayer();
        SimpleExoPlayer moviePlayer = controller.getContentPlayer();

        boolean shouldReprepareForSinglePlayer = PlayerDeviceUtils.useSinglePlayer() && isPlayingAds;

        //first remove the AdPlayer's listener and pause the player
        adPlayer.removeAnalyticsListener(componentController.getAdPlayingMonitor());

        if (shouldReprepareForSinglePlayer) {
            adPlayer.setPlayWhenReady(false);
        }

        //then update the playerView with SimpleExoPlayer and Movie MediaModel
        TubiExoPlayerView tubiExoPlayerView = (TubiExoPlayerView) controller.getExoPlayerView();
        tubiExoPlayerView.setPlayer(moviePlayer, componentController.getTubiPlaybackInterface());
        tubiExoPlayerView.setMediaModel(movieMedia);

        //prepare the moviePlayer with data source and set it play

        boolean haveResumePosition = controller.getMovieResumePosition() != C.TIME_UNSET;

        boolean isPlayerIdle = moviePlayer.getPlaybackState() == Player.STATE_IDLE;

        if (shouldReprepareForSinglePlayer || isPlayerIdle) {
            moviePlayer.prepare(movieMedia.getMediaSource(), !haveResumePosition, false);
            updatePlayerPosition(moviePlayer, controller);
        }

        moviePlayer.setPlayWhenReady(true);


        isPlayingAds = false;

        hideVpaidNShowPlayer(controller);

        //when return to the movie playing state, show the subtitle if necessary
        if (shouldShowSubtitle()) {
            ((TubiExoPlayerView) controller.getExoPlayerView()).getSubtitleView().setVisibility(View.VISIBLE);
        }
    }

    private void updatePlayerPosition(SimpleExoPlayer moviePlayer, PlayerUIController controller) {

        // if want to play movie from certain position when first open the movie
        if (controller.hasHistory()) {
            moviePlayer.seekTo(moviePlayer.getCurrentWindowIndex(), controller.getHistoryPosition());
            controller.clearHistoryRecord();
            return;
        }

        boolean haveResumePosition = controller.getMovieResumePosition() != C.TIME_UNSET;
        if (haveResumePosition) {
            moviePlayer.seekTo(moviePlayer.getCurrentWindowIndex(), controller.getMovieResumePosition());
        }
    }

    private void hideVpaidNShowPlayer(final PlayerUIController controller) {

        controller.getExoPlayerView().setVisibility(View.VISIBLE);

        WebView vpaidEWebView = controller.getVpaidWebView();
        if (vpaidEWebView != null) {
            vpaidEWebView.setVisibility(View.GONE);
            vpaidEWebView.loadUrl(Constants.EMPTY_URL);
            vpaidEWebView.clearHistory();
        }
    }

    private boolean shouldShowSubtitle() {

        TubiExoPlayerView view = (TubiExoPlayerView) controller.getExoPlayerView();
        PlayerController controller = (PlayerController) view.getPlayerController();


        return controller.mediaHasSubstitle.get() && controller.isMediaSubstitleEnabled.get();
    }

}
