package com.easyplex.ui.player.fsm.concrete;

import com.easyplex.ui.player.fsm.BaseState;
import com.easyplex.ui.player.fsm.Input;
import com.easyplex.ui.player.fsm.State;
import com.easyplex.ui.player.fsm.concrete.factory.StateFactory;

/**
 * Created by allensun on 7/31/17.
 */
public class VastAdInteractionSandBoxState extends BaseState {

    @Override
    public State transformToState(Input input, StateFactory factory) {

        if (input == Input.BACK_TO_PLAYER_FROM_VAST_AD) {
            return factory.createState(AdPlayingState.class);
        }
        return null;
    }
}
