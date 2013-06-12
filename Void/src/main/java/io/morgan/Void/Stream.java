package io.morgan.Void;

import android.hardware.Camera;
import android.hardware.Camera.ShutterCallback;
import android.hardware.Camera.PictureCallback;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class Stream extends Activity {

    final int K_STATE_STREAM = 0;
    final int K_STATE_PREVIEW = 1;
    final int K_STATE_FROZEN = 2;

    int state = K_STATE_STREAM;
    int originalHeight;

    RelativeLayout actionBar;
    LinearLayout stream;

    SurfaceView preview;
    SurfaceHolder previewHolder;
    Camera camera;

    Button enterTheVoid;
    Button takePicture;
    Button post;
    Button stopAction;

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

        enterTheVoid = (Button) findViewById(R.id.enter_the_void);
        takePicture = (Button) findViewById(R.id.take_picture);
        post = (Button) findViewById(R.id.post);
        stopAction = (Button) findViewById(R.id.stop_action);

        // attach main action handlers
        enterTheVoid.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                originalHeight = actionBar.getHeight();
                Animation anim = Animator.expand(actionBar, stream.getHeight());
                anim.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation arg0) {}
                    @Override
                    public void onAnimationRepeat(Animation arg0) {}

                    @Override
                    public void onAnimationEnd(Animation arg0) {
                        Log.d("ITS DONE", "ITS DONE");
                        stopAction.setVisibility(View.VISIBLE);
                        preview.setVisibility(View.VISIBLE);
                        enterTheVoid.setVisibility(View.INVISIBLE);
                        takePicture.setVisibility(View.VISIBLE);
                        preview.getLayoutParams().height = preview.getWidth();
                        camera = Camera.open();
                        // ONLY do this after the first
                        initPreview(preview.getWidth(), preview.getLayoutParams().height);
                        startPreview();
                    }
                });
            }
        });

        takePicture.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                camera.takePicture(shutterCallback, rawCallback, jpegCallback);
                // take the picture
                // freeze the preview
                // set the location?
                // display the location under the photo
                takePicture.setVisibility(View.INVISIBLE);
                post.setVisibility(View.VISIBLE);
                state = K_STATE_FROZEN;
            }
        });

        post.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                // disable this button
                // start the upload
                // show and update the progress bar accordingly
                // teardown preview and camera
                // enable the void button
                // hide and enable this button
                // collapse

//                preview.setVisibility(View.INVISIBLE);
//                if (inPreview) {
//                    camera.stopPreview();
//                }
//                camera.release();
//                camera = null;
//                inPreview = false;
//                Animator.collapse(actionBar, originalHeight);
//                voidOpen = false;
            }
        });

        stopAction.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                switch(state) {
                    case K_STATE_FROZEN:
                        startPreview();
                        takePicture.setVisibility(View.VISIBLE);
                        post.setVisibility(View.INVISIBLE);
                        state = K_STATE_PREVIEW;
                        break;
                    case K_STATE_PREVIEW:
                        stopAction.setVisibility(View.INVISIBLE);
                        takePicture.setVisibility(View.INVISIBLE);
                        preview.setVisibility(View.INVISIBLE);
                        enterTheVoid.setVisibility(View.VISIBLE);
                        camera.stopPreview();
                        camera.release();
                        camera = null;
                        cameraConfigured = false;
                        Animator.collapse(actionBar, originalHeight);
                        state = K_STATE_STREAM;
                        break;
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
                Log.d("SIZE", "=> " + width + ", " + height);
                Camera.Parameters parameters = camera.getParameters();
                Camera.Size size = getBestPreviewSize(width, height, parameters);

                // set a camera format
                // parameters.setPictureFormat(PixelFormat.JPEG);

                if (size != null) {
                    Log.d("THIS", "GOT CALLED");
                    parameters.setPreviewSize(size.width, size.height);
                    camera.setParameters(parameters);
                    cameraConfigured = true;
                }
            }
        }
    }

    private void startPreview() {
        if (cameraConfigured && camera != null) {
            Log.d("PREVIEW", "START");
            camera.startPreview();
            state = K_STATE_PREVIEW;
        }
    }

    SurfaceHolder.Callback surfaceCallback = new SurfaceHolder.Callback() {

        public void surfaceCreated(SurfaceHolder holder) {
            // no-op -- wait until surfaceChanged()
        }

        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
//            initPreview(width, height);
//            startPreview();
        }

        public void surfaceDestroyed(SurfaceHolder holder) {
            Log.d("DESTROYED", "DESTROYED");
            // no-op
        }
    };

    ShutterCallback shutterCallback = new ShutterCallback() {
        public void onShutter() {
            // TODO Do something when the shutter closes.
        }
    };

    PictureCallback rawCallback = new PictureCallback() {
        public void onPictureTaken(byte[] _data, Camera _camera) {
            // TODO Do something with the image RAW data.
        }
    };

    PictureCallback jpegCallback = new PictureCallback() {
        public void onPictureTaken(byte[] _data, Camera _camera) {
            // TODO Do something with the image JPEG data.
        }
    };
}
