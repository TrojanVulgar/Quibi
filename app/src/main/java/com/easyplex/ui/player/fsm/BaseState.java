package com.easyplex.ui.player.fsm;

import androidx.annotation.NonNull;
import com.easyplex.ui.player.controller.PlayerAdLogicController;
import com.easyplex.ui.player.controller.PlayerUIController;
import com.easyplex.ui.player.fsm.state_machine.FsmPlayer;
import com.easyplex.data.model.ads.AdMediaModel;
import com.easyplex.data.model.media.MediaModel;
import com.easyplex.ui.player.utilities.ExoPlayerLogger;
import com.easyplex.util.Constants;


/**
 * Created by allensun on 7/31/17.
 * Base class for {@link State}, preparation method to get the state ready for UI and business rule manipulation.
 */
public abstract class BaseState implements State {

    protected PlayerUIController controller;

    protected PlayerAdLogicController componentController;

    protected MediaModel movieMedia;

    protected AdMediaModel adMedia;

    /**
     * for testing purpose,
     *
     * @param fsmPlayer
     * @return
     */
    protected boolean isNull(FsmPlayer fsmPlayer) {
        if (fsmPlayer == null) {
            throw new IllegalStateException("FsmPlayer can not be null");
        }

        if (controller == null || componentController == null || movieMedia == null) {
            ExoPlayerLogger.e(Constants.FSMPLAYER_TESTING, "components are null");
            return true;
        }

        return false;
    }

    @Override
    public void performWorkAndUpdatePlayerUI(@NonNull FsmPlayer fsmPlayer) {
        /**
         * need to get the reference of the UI and Business logic components first.
         */
        controller = fsmPlayer.getController();
        componentController = fsmPlayer.getPlayerComponentController();
        movieMedia = fsmPlayer.getMovieMedia();
        adMedia = fsmPlayer.getAdMedia();
    }
}
