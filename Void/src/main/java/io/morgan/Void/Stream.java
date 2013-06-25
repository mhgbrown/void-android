package io.morgan.Void;

import android.app.Activity;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class Stream extends Activity {

    final int K_STATE_STREAM = 0;
    final int K_STATE_PREVIEW = 1;
    final int K_STATE_FROZEN = 2;

    final String DEFAULT_LOCATION = "Somewhere";

    int state = K_STATE_STREAM;
    int originalHeight;
    ArrayList<Post> posts = new ArrayList<Post>();
    boolean cameraConfigured = false;

    RelativeLayout actionBar;
    LinearLayout stream;

    SurfaceView preview;
    SurfaceHolder previewHolder;
    Camera camera;
    TextView locationDisplay;
    TextView emptyIndicator;
    ListView postsList;

    Button enterTheVoid;
    Button takePicture;
    Button postButton;
    Button stopAction;

    Locator locator;
    Post post = null;
    User user = null;
    PostAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stream);

        stream = (LinearLayout) findViewById(R.id.stream);
        actionBar = (RelativeLayout) findViewById(R.id.action_bar);
        preview = (SurfaceView) findViewById(R.id.preview);
        locationDisplay = (TextView) findViewById(R.id.location_display);
        emptyIndicator = (TextView) findViewById(R.id.empty_indicator);
        postsList = (ListView) findViewById(R.id.posts);

        previewHolder = preview.getHolder();
        previewHolder.addCallback(surfaceCallback);
        previewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        enterTheVoid = (Button) findViewById(R.id.enter_the_void);
        takePicture = (Button) findViewById(R.id.take_picture);
        postButton = (Button) findViewById(R.id.post);
        stopAction = (Button) findViewById(R.id.stop_action);

        locator = new Locator();
        user = new User(Identity.id(this));

        adapter = new PostAdapter(Stream.this, R.layout.stream_post_view, new ArrayList<Post>());
        postsList.setAdapter(adapter);

        // attach main action handlers
        enterTheVoid.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                hideEmptyIndicator();
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
                        stopAction.setVisibility(View.VISIBLE);
                        preview.setVisibility(View.VISIBLE);
                        enterTheVoid.setVisibility(View.INVISIBLE);
                        takePicture.setVisibility(View.VISIBLE);
                        locationDisplay.setVisibility(View.VISIBLE);
                        preview.getLayoutParams().height = preview.getWidth();
                        // move the location display down
                        RelativeLayout.LayoutParams locationDisplayLayout = (RelativeLayout.LayoutParams)locationDisplay.getLayoutParams();
                        locationDisplayLayout.setMargins(Animator.dpsToPixels(actionBar, 5), preview.getLayoutParams().height + ((ViewGroup.MarginLayoutParams) preview.getLayoutParams()).topMargin + Animator.dpsToPixels(actionBar, 5), 0, 0);
                        locationDisplay.setLayoutParams(locationDisplayLayout);
                        camera = Camera.open();
                        // this is weird and im not sure about it
                        initPreview(preview.getWidth(), preview.getLayoutParams().height);
                        startPreview();

                        locator.getLocation(Stream.this, locatorListener);
                    }
                });
            }
        });

        takePicture.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                camera.takePicture(shutterCallback, rawCallback, jpegCallback);
                // change visibility in shutter callback
            }
        });

        postButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // disable every button
                stopAction.setVisibility(View.INVISIBLE);
                takePicture.setVisibility(View.INVISIBLE);
                postButton.setVisibility(View.INVISIBLE);
                post.save(new Post.Callback(){

                    @Override
                    public void onBeforeSend(ArrayList<NameValuePair> nameValuePairs) {
                        nameValuePairs.addAll(user.assembleParameters());
                    }

                    @Override
                    public void onSuccess(final Post p) {
                        stopAction.setVisibility(View.INVISIBLE);
                        takePicture.setVisibility(View.INVISIBLE);
                        preview.setVisibility(View.INVISIBLE);
                        enterTheVoid.setVisibility(View.VISIBLE);
                        locationDisplay.setVisibility(View.INVISIBLE);
                        camera.stopPreview();
                        camera.release();
                        camera = null;
                        cameraConfigured = false;
                        Animator.collapse(actionBar, originalHeight);
                        state = K_STATE_STREAM;
                        post = null;
                        locationDisplay.setText(DEFAULT_LOCATION);

                        if(p == null || p.imageUrl == null || p.imageUrl.contains("missing")) {
                            Toast.makeText(Stream.this, "No new images for you yet.  Please try again.", Toast.LENGTH_LONG).show();
                            return;
                        }

                        adapter.insert(p, 0);
                        adapter.notifyDataSetChanged();
                        hideEmptyIndicator();
                    }

                    @Override
                    public void onError(Exception e) {
                        Toast.makeText(Stream.this, "Sorry, please try again", Toast.LENGTH_LONG).show();
                        startPreview();
                        stopAction.setVisibility(View.VISIBLE);
                        takePicture.setVisibility(View.VISIBLE);
                        postButton.setVisibility(View.INVISIBLE);
                        state = K_STATE_PREVIEW;
                    }
                });
            }
        });

        stopAction.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                switch(state) {
                    case K_STATE_FROZEN:
                        startPreview();
                        takePicture.setVisibility(View.VISIBLE);
                        postButton.setVisibility(View.INVISIBLE);
                        state = K_STATE_PREVIEW;
                        break;
                    case K_STATE_PREVIEW:
                        stopAction.setVisibility(View.INVISIBLE);
                        takePicture.setVisibility(View.INVISIBLE);
                        preview.setVisibility(View.INVISIBLE);
                        enterTheVoid.setVisibility(View.VISIBLE);
                        locationDisplay.setVisibility(View.INVISIBLE);
                        camera.stopPreview();
                        camera.release();
                        camera = null;
                        cameraConfigured = false;
                        Animator.collapse(actionBar, originalHeight);
                        state = K_STATE_STREAM;
                        post = null;
                        locationDisplay.setText(DEFAULT_LOCATION);
                        break;
                }
            }
        });

        initStream();
    }

    private void initStream() {
        // this is probably not a good idea, but its important to live dangerously
        // especially within an app that has no concern for your content anyways
        String url = Post.ENDPOINT + "?" + User.VOID_ID_NAME + "=" + user.voidId;
        Http.get(url, new Http.Callback() {

            @Override
            public void onSuccess(HttpResponse httpResponse) {
                try {
                    String json = Http.getContentString(httpResponse);
                    JSONArray postsJson = new JSONArray(json);

                    if(postsJson.length() == 0) {
                        showEmptyIndicator();
                        return;
                    }

                    for (int i = 0; i < postsJson.length(); i++) {
                        JSONObject aPost = postsJson.getJSONObject(i);
                        Post tmp = Post.fromJSON(aPost.toString());
                        adapter.add(tmp);
                    }

                    adapter.notifyDataSetChanged();

                } catch (IOException e) {
                    e.printStackTrace();
                    onError(e);
                } catch (JSONException e) {
                    e.printStackTrace();
                    onError(e);
                }
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(Stream.this, "Sorry, couldn't get your stream", Toast.LENGTH_LONG).show();
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

                // set a camera format
                parameters.setPictureFormat(ImageFormat.JPEG);
                parameters.set("orientation", "portrait");
                parameters.setRotation(90);

                if (size != null) {
                    parameters.setPreviewSize(size.width, size.height);
                    camera.setParameters(parameters);
                    camera.setDisplayOrientation(90);
                    cameraConfigured = true;
                }
            }
        }
    }

    private void startPreview() {
        if (cameraConfigured && camera != null) {
            camera.startPreview();
            state = K_STATE_PREVIEW;
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

    ShutterCallback shutterCallback = new ShutterCallback() {
        public void onShutter() {
            takePicture.setVisibility(View.INVISIBLE);
            postButton.setVisibility(View.VISIBLE);
            state = K_STATE_FROZEN;
        }
    };

    PictureCallback rawCallback = new PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {
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
            }
        }
    };

    public void showEmptyIndicator() {
        emptyIndicator.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        emptyIndicator.setVisibility(View.VISIBLE);
    }

    public void hideEmptyIndicator() {
        emptyIndicator.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, 0));
        emptyIndicator.setVisibility(View.INVISIBLE);
    }
}
