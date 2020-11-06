package com.easyplex.ui.player.fsm.concrete;

import android.view.View;
import android.webkit.WebView;

import androidx.annotation.NonNull;

import com.easyplex.util.Constants;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.easyplex.ui.player.controller.PlayerAdLogicController;
import com.easyplex.ui.player.controller.PlayerUIController;
import com.easyplex.ui.player.fsm.BaseState;
import com.easyplex.ui.player.fsm.Input;
import com.easyplex.ui.player.fsm.State;
import com.easyplex.ui.player.fsm.concrete.factory.StateFactory;
import com.easyplex.ui.player.fsm.state_machine.FsmPlayer;
import com.easyplex.data.model.ads.AdMediaModel;
import com.easyplex.data.model.media.MediaModel;
import com.easyplex.ui.player.views.TubiExoPlayerView;



/**
 * Created by allensun on 7/31/17.
 */
public class AdPlayingState extends BaseState {


    private boolean isPlayingAds = false;



    @Override
    public State transformToState(Input input, StateFactory factory) {

        switch (input) {

            case NEXT_AD:
                return factory.createState(AdPlayingState.class);

            case AD_CLICK:
                return factory.createState(VastAdInteractionSandBoxState.class);

            case AD_FINISH:
                return factory.createState(MoviePlayingState.class);

            case VPAID_MANIFEST:
                return factory.createState(VpaidState.class);


            default:

        }
        return null;
    }

    @Override
    public void performWorkAndUpdatePlayerUI(@NonNull FsmPlayer fsmPlayer) {
        super.performWorkAndUpdatePlayerUI(fsmPlayer);

        if (isNull(fsmPlayer)) {
            return;
        }

        //reset the ad player position everytime when a transition to AdPlaying occur
        controller.clearAdResumeInfo();

        playingAdAndPauseMovie(controller, adMedia, componentController, fsmPlayer);
    }


    private void playingAdAndPauseMovie(PlayerUIController controller, AdMediaModel adMediaModel,
                                        PlayerAdLogicController componentController, FsmPlayer fsmPlayer) {

        SimpleExoPlayer adPlayer = controller.getAdPlayer();
        SimpleExoPlayer moviePlayer = controller.getContentPlayer();

        // then setup the player for ad to playe
        MediaModel adMedia = adMediaModel.nextAD();

        // Handle situation when ad medaia is empty, or invalid urls.
        if (adMedia != null) {

            if (adMedia.isVpaid()) {
                fsmPlayer.transit(Input.VPAID_MANIFEST);
                return;
            }

            hideVpaidNShowPlayer(controller);

            moviePlayer.setPlayWhenReady(false);

            // We need save movie play position before play ads for single player instance case
            if (!isPlayingAds){

                long resumePosition = Math.max(0, moviePlayer.getCurrentPosition());
                controller.setMovieResumeInfo(moviePlayer.getCurrentWindowIndex(), resumePosition);

            }


            //prepare the moviePlayer with data source and set it play

            boolean haveResumePosition = controller.getAdResumePosition() != C.TIME_UNSET;

            //prepare the mediaSource to AdPlayer
            adPlayer.prepare(adMedia.getMediaSource(), !haveResumePosition, true);

            isPlayingAds = true;

            if (haveResumePosition) {
                adPlayer.seekTo(adPlayer.getCurrentWindowIndex(), controller.getAdResumePosition());
            }

            //update the ExoPlayerView with AdPlayer and AdMedia
            TubiExoPlayerView tubiExoPlayerView = (TubiExoPlayerView) controller.getExoPlayerView();
            tubiExoPlayerView.setPlayer(adPlayer, componentController.getTubiPlaybackInterface());
            tubiExoPlayerView.setMediaModel(adMedia);
            //update the numbers of ad left to give user indicator
            tubiExoPlayerView.setAvailableAdLeft(adMediaModel.nubmerOfAd());

            //Player the Ad.
            adPlayer.setPlayWhenReady(true);
            adPlayer.addAnalyticsListener(componentController.getAdPlayingMonitor());
            adPlayer.addMetadataOutput(componentController.getAdPlayingMonitor());

            //hide the subtitle view when ad is playing
            ((TubiExoPlayerView) controller.getExoPlayerView()).getSubtitleView().setVisibility(View.INVISIBLE);
        }
    }

    private void hideVpaidNShowPlayer(final PlayerUIController imcontroller) {

        imcontroller.getExoPlayerView().setVisibility(View.VISIBLE);

        WebView vpaidEWebView = imcontroller.getVpaidWebView();
        if (vpaidEWebView != null) {
            vpaidEWebView.setVisibility(View.GONE);
            vpaidEWebView.loadUrl(Constants.EMPTY_URL);
            vpaidEWebView.clearHistory();
        }
    }

}
