package com.easyplex.ui.player.fsm.concrete;

import androidx.annotation.NonNull;

import com.easyplex.ui.player.fsm.BaseState;
import com.easyplex.ui.player.fsm.Input;
import com.easyplex.ui.player.fsm.State;
import com.easyplex.ui.player.fsm.callback.AdInterface;
import com.easyplex.ui.player.fsm.callback.RetrieveAdCallback;
import com.easyplex.ui.player.fsm.concrete.factory.StateFactory;
import com.easyplex.ui.player.fsm.state_machine.FsmPlayer;
import com.easyplex.data.model.ads.AdRetriever;
import com.easyplex.ui.player.utilities.ExoPlayerLogger;


/**
 * Created by allensun on 7/31/17.
 */
public class MakingAdCallState extends BaseState {

    @Override
    public State transformToState(Input input, StateFactory factory) {
        switch (input) {
            case AD_RECEIVED:
                return factory.createState(ReceiveAdState.class);

            case EMPTY_AD:
                return null;

            case MAKE_AD_CALL:
                return factory.createState(MakingAdCallState.class);

            case PRE_ROLL_AD_RECEIVED:
                return factory.createState(AdPlayingState.class);

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

        fetchAd(fsmPlayer.getAdServerInterface(), fsmPlayer.getAdRetriever(), fsmPlayer);

        //in the MadingAdCallState, no UI need to be updated.

    }

    private void fetchAd(AdInterface adInterface, AdRetriever retriever, RetrieveAdCallback callback) {
        if (adInterface != null && retriever != null && callback != null) {
            adInterface.fetchAd(retriever, callback);
        } else {
            ExoPlayerLogger.e("TAG", "fetchAd fail, adInterface or AdRetriever is empty");
        }
    }
}
