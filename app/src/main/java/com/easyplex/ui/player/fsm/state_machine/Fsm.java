package com.easyplex.ui.player.fsm.state_machine;

import androidx.annotation.NonNull;

import com.easyplex.ui.player.fsm.Input;
import com.easyplex.ui.player.fsm.State;


/**
 * Created by allensun on 7/27/17.
 */
public interface Fsm {

    State getCurrentState();

    void transit(Input input);

    void updateSelf();

    /**
     * this is the beginning state of the fsm
     *
     * @return
     */
    @NonNull


    Class<?> initializeState();

    void restart();

    void update();

    void backfromApp();

    void updateSelfUi();

}
