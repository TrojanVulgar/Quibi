package com.easyplex.ui.manager;

import android.content.SharedPreferences;
import com.easyplex.data.model.status.Status;
import static com.easyplex.util.Constants.CODE;


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



public class StatusManager {



    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    public StatusManager(SharedPreferences prefs){
        this.prefs = prefs;
        this.editor = prefs.edit();
    }

    public void saveSettings(Status status){
        editor.putString(CODE, status.getCode()).commit();
        editor.apply();
    }


    public Status getAds(){
        Status status = new Status();
        status.setCode(prefs.getString(CODE, null));
        return status;
    }




}
