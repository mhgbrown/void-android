package io.morgan.Void;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;

public class Stream extends Activity {

    public final int K_STATE_STREAM = 0;
    public final int K_STATE_PREVIEW = 1;
    public final int K_STATE_FROZEN = 2;

    public int state = K_STATE_STREAM;
    int originalHeight;

    RelativeLayout actionBar;
    LinearLayout stream;

    CameraPreview cameraPreview;

    Camera camera;
    TextView locationDisplay;
    TextView emptyIndicator;
    PostListView postsList;

    ImageButton enterTheVoid;
    ImageButton takePicture;
    ImageButton postButton;
    ImageButton stopAction;

    ImageView loadingIndicator;

    Locator locator;
    Post post = null;

    SharedPreferences preferences;
    final String isNewUserPref = "NEW_USER";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stream);

        stream = (LinearLayout) findViewById(R.id.stream);
        actionBar = (RelativeLayout) findViewById(R.id.action_bar);
        cameraPreview = (CameraPreview) findViewById(R.id.camera_preview);
        locationDisplay = (TextView) findViewById(R.id.location_display);
        emptyIndicator = (TextView) findViewById(R.id.empty_indicator);
        postsList = (PostListView) findViewById(R.id.posts);

        enterTheVoid = (ImageButton) findViewById(R.id.enter_the_void);
        takePicture = (ImageButton) findViewById(R.id.take_picture);
        postButton = (ImageButton) findViewById(R.id.post);
        stopAction = (ImageButton) findViewById(R.id.stop_action);

        loadingIndicator = (ImageView) findViewById(R.id.loading_indicator);

        locator = new Locator();

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
                        post = new Post();

                        cameraPreview.getLayoutParams().height = cameraPreview.getWidth();

                        // move the location display down
                        RelativeLayout.LayoutParams locationDisplayLayout = (RelativeLayout.LayoutParams)locationDisplay.getLayoutParams();
                        locationDisplayLayout.setMargins(Animator.dpsToPixels(actionBar, 5), cameraPreview.getLayoutParams().height + ((ViewGroup.MarginLayoutParams) cameraPreview.getLayoutParams()).topMargin + Animator.dpsToPixels(actionBar, 5), 0, 0);
                        locationDisplay.setLayoutParams(locationDisplayLayout);

                        stopAction.setVisibility(View.VISIBLE);
                        cameraPreview.setVisibility(View.VISIBLE);
                        enterTheVoid.setVisibility(View.INVISIBLE);
                        takePicture.setVisibility(View.VISIBLE);
                        locationDisplay.setVisibility(View.VISIBLE);

                        state = K_STATE_PREVIEW;
                        camera = App.getCameraInstance();

                        locator.getLocation(Stream.this, locatorListener);
                    }
                });
            }
        });

        takePicture.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                takePicture.setVisibility(View.INVISIBLE);
                camera.cancelAutoFocus();
                camera.autoFocus(autoFocusCallback);
            }
        });

        postButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // disable every button
                stopAction.setVisibility(View.INVISIBLE);
                takePicture.setVisibility(View.INVISIBLE);
                postButton.setVisibility(View.INVISIBLE);
                startLoadingAnimation();
                post.save(new Post.Callback(){

                    @Override
                    public void onSuccess(final Post p) {
                        stopLoadingAnimation();
                        stopAction.setVisibility(View.INVISIBLE);
                        takePicture.setVisibility(View.INVISIBLE);
                        cameraPreview.setVisibility(View.INVISIBLE);
                        enterTheVoid.setVisibility(View.VISIBLE);
                        locationDisplay.setVisibility(View.INVISIBLE);
                        App.releaseCameraInstance();
                        camera = null;
                        Animator.collapse(actionBar, originalHeight);
                        state = K_STATE_STREAM;
                        post = null;
                        locationDisplay.setText(Post.DEFAULT_LOCATION);

                        if(p == null || p.imageUrl == null || p.imageUrl.contains("missing")) {
                            Toast.makeText(Stream.this, "No new images for you yet.  Please try again.", Toast.LENGTH_LONG).show();
                            return;
                        }

                        postsList.getAdapter().insert(p, 0);
                        postsList.getAdapter().notifyDataSetChanged();
                    }

                    @Override
                    public void onError(Exception e) {
                        stopLoadingAnimation();
                        stopAction.setVisibility(View.VISIBLE);
                        takePicture.setVisibility(View.INVISIBLE);
                        postButton.setVisibility(View.VISIBLE);
                        state = K_STATE_FROZEN;
                        Toast.makeText(Stream.this, "Sorry, please try again", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        stopAction.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                switch(state) {
                    case K_STATE_FROZEN:
                        camera.startPreview();
                        takePicture.setVisibility(View.VISIBLE);
                        postButton.setVisibility(View.INVISIBLE);
                        state = K_STATE_PREVIEW;
                        break;
                    case K_STATE_PREVIEW:
                        stopAction.setVisibility(View.INVISIBLE);
                        takePicture.setVisibility(View.INVISIBLE);
                        cameraPreview.setVisibility(View.INVISIBLE);
                        enterTheVoid.setVisibility(View.VISIBLE);
                        locationDisplay.setVisibility(View.INVISIBLE);
                        App.releaseCameraInstance();
                        camera = null;
                        Animator.collapse(actionBar, originalHeight);
                        state = K_STATE_STREAM;
                        post = null;
                        locationDisplay.setText(Post.DEFAULT_LOCATION);
                        break;
                }
            }
        });

        // determine if we should display the welcome dialog
        preferences = App.getAppContext().getSharedPreferences(App.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
        Boolean isNewUser = preferences.getBoolean(isNewUserPref, false);
        if (!isNewUser) {
            AlertDialog.Builder adb = new AlertDialog.Builder(this);
            adb.setTitle("Welcome");
            adb.setMessage("This is Void.\n\nYou've received a photo from a random user. Take and share photos using the purple triangle to receive more.\n\nSwipe left or right on a photo to remove it.\n\nIf you like a photo, touch the heart to increase the chance it will be redistributed.\n\nEnjoy.");
            adb.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            adb.show();
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean(isNewUserPref, true);
            editor.commit();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        App.releaseCameraInstance();
        camera = null;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(state == K_STATE_FROZEN) {
            camera = App.getCameraInstance();
            takePicture.setVisibility(View.VISIBLE);
            postButton.setVisibility(View.INVISIBLE);
            state = K_STATE_PREVIEW;
        }
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        FlurryAgent.onStartSession(this, getString(R.string.flurry_api_key));
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        FlurryAgent.onEndSession(this);
    }

    Camera.AutoFocusCallback autoFocusCallback = new Camera.AutoFocusCallback() {
        @Override
        public void onAutoFocus(boolean success, Camera camera) {
            camera.takePicture(shutterCallback, null, jpegCallback);
        }
    };

    ShutterCallback shutterCallback = new ShutterCallback() {
        public void onShutter() {
            takePicture.setVisibility(View.INVISIBLE);
            postButton.setVisibility(View.VISIBLE);
            state = K_STATE_FROZEN;
        }
    };

    PictureCallback jpegCallback = new PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {
            post.image = data;
        }
    };

    Locator.LocatorListener locatorListener = new Locator.LocatorListener() {
        @Override
        public void onLocationFound(Location location) {
            String cityAndCountry = Locator.getLocalityAndCountry(Stream.this, location);

            if(cityAndCountry != null) {
                locationDisplay.setText(cityAndCountry);
                post.location = cityAndCountry;
                post.latitude = location.getLatitude();
                post.longitude = location.getLongitude();
            }
        }
    };

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if(hasFocus && postsList.contextMenuDisplayed) {
            postsList.contextMenuDisplayed = false;
            postsList.onContextMenuClosed(null);
        }
    }

    private void stopLoadingAnimation() {
        loadingIndicator.setVisibility(View.INVISIBLE);
        loadingIndicator.post(new Runnable() {
            @Override
            public void run() {
                AnimationDrawable loadingAnimation = (AnimationDrawable) loadingIndicator.getBackground();
                loadingAnimation.stop();
            }
        });
    }

    private void startLoadingAnimation() {
        loadingIndicator.setVisibility(View.VISIBLE);
        loadingIndicator.post(new Runnable() {
            @Override
            public void run() {
                AnimationDrawable loadingAnimation = (AnimationDrawable) loadingIndicator.getBackground();
                loadingAnimation.start();
            }
        });
    }
}

// TODO
// Fix camera preview being squashed
// Loading animations
// Farid's liking idea
// Don't restart the preview after leaving the app
// list view caching/pagination?

// so for likes
// explore or exploit
// take the ratio of likes / times a photo has been delivered
// pick some threshold ratio over which photos will be eligibile to send out
// 90% (or some other percent of the time) serve a random photo above that like threshold
// 10% (or some other percent of the time) serve a random new or unsent out photo
