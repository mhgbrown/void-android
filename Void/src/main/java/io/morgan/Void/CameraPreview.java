package io.morgan.Void;

import android.content.Context;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by mobrown on 7/29/13.
 *
 * http://developer.android.com/guide/topics/media/camera.html
 */
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    private SurfaceHolder holder;
    private Camera camera;

    public CameraPreview(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);

        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        holder = getHolder();
        holder.addCallback(this);
        // deprecated setting, but required on Android versions prior to 3.0
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public void surfaceCreated(SurfaceHolder holder) {
        // No-op
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        // empty. Take care of releasing the Camera preview in your activity.
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.
        camera = App.getCameraInstance();

        if (holder.getSurface() == null){
            // preview surface does not exist
            return;
        }

        // stop preview before making changes
        try {
            camera.stopPreview();
        } catch (Exception e) {
            e.printStackTrace();
            // ignore: tried to stop a non-existent preview
        }

        configurePreview();

        // start preview with new settings
        try {
            camera.setPreviewDisplay(holder);
            camera.startPreview();
        } catch (Exception e){
            Log.d("Void", "Error starting camera preview: " + e.getMessage());
        }
    }

    private void configurePreview() {
        Camera.Parameters parameters = camera.getParameters();

        // set a camera format
        parameters.setPictureFormat(ImageFormat.JPEG);
        parameters.set("orientation", "portrait");
        parameters.setRotation(90);
        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        Camera.Size size = getBestPreviewSize(parameters);

        if (size != null) {
            parameters.setPreviewSize(size.width, size.height);
        }

        camera.setParameters(parameters);
        camera.setDisplayOrientation(90);
    }

    private Camera.Size getBestPreviewSize(Camera.Parameters parameters) {
        Camera.Size result = null;

        for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
            if (size.width <= getWidth() && size.height <= getWidth()) {
                if (result == null) {
                    result = size;
                } else {
                    int resultArea = result.width * result.height;
                    int newArea = size.width * size.height;

                    if (newArea > resultArea) {
                        result = size;
                    }
                }
            }
        }

        return result;
    }
}
