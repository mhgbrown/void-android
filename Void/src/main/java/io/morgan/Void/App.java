package io.morgan.Void;

import android.app.Application;
import android.content.Context;
import android.hardware.Camera;

/**
 * Created by mobrown on 7/11/13.
 */
public class App extends Application {
    public static final String SHARED_PREFERENCES_NAME = "Void";

    private static Context context;
    private static Camera camera;

    public void onCreate() {
        super.onCreate();
        App.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return App.context;
    }

    public static void releaseCameraInstance() {
        if( camera != null ) {
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }

    public static Camera getCameraInstance() {
        if(camera != null) {
            return camera;
        } else {
            try {
                camera = Camera.open(); // attempt to get a Camera instance
            }
            catch (Exception e){
                // Camera is not available (in use or does not exist)
            }
        }

        return camera; // returns null if camera is unavailable
    }
}
