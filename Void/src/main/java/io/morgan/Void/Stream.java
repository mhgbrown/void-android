package io.morgan.Void;

import android.hardware.Camera;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class Stream extends Activity {

    RelativeLayout actionBar;
    LinearLayout stream;
    SurfaceView preview;
    SurfaceHolder previewHolder;
    Camera camera;
    boolean inPreview = false;
    boolean cameraConfigured = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stream);

        stream = (LinearLayout) findViewById(R.id.stream);
        actionBar = (RelativeLayout) findViewById(R.id.action_bar);
        preview = (SurfaceView) findViewById(R.id.preview);

        previewHolder = preview.getHolder();
        previewHolder.addCallback(surfaceCallback);
        previewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        // attach main action handlers
        Button b = (Button) findViewById(R.id.enter_the_void);
        b.setOnClickListener(new View.OnClickListener() {
            boolean open = false;
            int originalHeight;

            @Override
            public void onClick(View v) {
                if(open) {
                    preview.setVisibility(View.INVISIBLE);
                    if (inPreview) {
                        camera.stopPreview();
                    }
                    camera.release();
                    camera = null;
                    inPreview = false;
                    Animator.collapse(actionBar, originalHeight);
                    open = false;
                } else {
                    originalHeight = actionBar.getHeight();
                    Animator.expand(actionBar, stream.getHeight());
                    preview.setVisibility(View.VISIBLE);
                    preview.getLayoutParams().height = preview.getWidth();
                    camera = Camera.open();
                    startPreview();
                    open = true;
                }
            }
        });
    }

    private Camera.Size getBestPreviewSize(int width, int height,
                                           Camera.Parameters parameters) {
        Camera.Size result = null;

        for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
            if (size.width <= width && size.height <= height) {
                if (result == null) {
                    result = size;
                }
                else {
                    int resultArea = result.width * result.height;
                    int newArea = size.width * size.height;

                    if (newArea > resultArea) {
                        result = size;
                    }
                }
            }
        }

        return(result);
    }

    private void initPreview(int width, int height) {
        if (camera != null && previewHolder.getSurface() != null) {
            try {
                camera.setPreviewDisplay(previewHolder);
            }
            catch (Throwable t) {
                Log.e("PreviewDemo-surfaceCallback", "Exception in setPreviewDisplay()", t);
                Toast.makeText(Stream.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }

            if (!cameraConfigured) {
                Camera.Parameters parameters = camera.getParameters();
                Camera.Size size = getBestPreviewSize(width, height, parameters);

                if (size != null) {
                    parameters.setPreviewSize(size.width, size.height);
                    camera.setParameters(parameters);
                    cameraConfigured = true;
                }
            }
        }
    }

    private void startPreview() {
        if (cameraConfigured && camera != null) {
            camera.startPreview();
            inPreview = true;
        }
    }

    SurfaceHolder.Callback surfaceCallback = new SurfaceHolder.Callback() {

        public void surfaceCreated(SurfaceHolder holder) {
            // no-op -- wait until surfaceChanged()
        }

        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            initPreview(width, height);
            startPreview();
        }

        public void surfaceDestroyed(SurfaceHolder holder) {
            // no-op
        }
    };
}
