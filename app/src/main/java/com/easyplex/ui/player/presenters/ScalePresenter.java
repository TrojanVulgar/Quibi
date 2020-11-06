package com.easyplex.ui.player.presenters;

import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.view.Display;
import android.view.WindowManager;

import com.easyplex.ui.player.bindings.PlayerController;
import com.easyplex.ui.player.enums.ScaleMode;


public class ScalePresenter {

    private Context mContext;
    private PlayerController mUserController;
    private ScaleMode mCurrentScaleMode = ScaleMode.MODE_DEFAULT;

    public ScalePresenter(Context context, PlayerController userController) {
        mContext = context;
        mUserController = userController;
        doScale(mCurrentScaleMode);
    }

    public void doScale() {
        ScaleMode nextScaleMode = mCurrentScaleMode.nextMode();
        doScale(nextScaleMode);
        mCurrentScaleMode = nextScaleMode;
    }

    public void doScale(ScaleMode mode) {
        switch (mode) {
            case MODE_DEFAULT:
                float initVideoAspectRatio = mUserController.getInitVideoAspectRatio();
                if (initVideoAspectRatio > 0) {
                    mUserController.setVideoAspectRatio(initVideoAspectRatio);
                }
                break;
            case MODE_4_3:
                mUserController.setVideoAspectRatio((float) 4 / 3);
                break;
            case MODE_FULL_SCREEN:
                mUserController.setVideoAspectRatio(getScreenWidthHeightRatio());
                break;

            default:
                mUserController.setVideoAspectRatio((float) 16 / 9);
                break;


        }
        mUserController.setResizeMode(mode.getMode());
    }

    public ScaleMode getCurrentScaleMode() {
        return mCurrentScaleMode;
    }

    private float getScreenWidthHeightRatio() {

        WindowManager windowManager =
                (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        final Display display = windowManager.getDefaultDisplay();
        Point outPoint = new Point();
        if (19 <= Build.VERSION.SDK_INT) {
            // include navigation bar
            display.getRealSize(outPoint);
        } else {
            // exclude navigation bar
            display.getSize(outPoint);


        }

        return (float) outPoint.x / outPoint.y;
    }
}
