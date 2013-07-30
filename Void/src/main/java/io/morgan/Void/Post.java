package io.morgan.Void;

import android.graphics.Bitmap;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by mobrown on 6/17/13.
 */
public class Post {
    public static final String ENDPOINT = "http://void-server.herokuapp.com/users/USER_ID/posts";
    public static final String LOCATION_NAME = "post[location]";
    public static final String IMAGE_NAME = "post[image]";
    public static final String LATITUDE_NAME = "post[latitude]";
    public static final String LONGITUDE_NAME = "post[longitude]";
    public static final String DEFAULT_LOCATION = "Somewhere";

    public String location;
    public int id = -1;
    public String imageUrl = null;
    public byte[] image = new byte[1];
    public Bitmap imageMap = null;
    public double latitude = 0;
    public double longitude = 0;

    public Post() {
        this.location = DEFAULT_LOCATION;
        this.image = new byte[1];
    }

    private String saveImage() {
        File outputFile = Media.createOutputFile();
        FileOutputStream output;

        try {
            output = new FileOutputStream(outputFile);
            output.write(image);
            output.close();
        } catch (FileNotFoundException e) {
            Log.e("Void", "The specified image file could not be found");
            e.printStackTrace();
        } catch (IOException e) {
            Log.e("Void", "The image file could not be saved");
            e.printStackTrace();
        }

        return outputFile.getAbsolutePath();
    }

    /**
     * Save this post, persisting it's information from the server.
     *
     * @param callback
     */
    public void save(final Callback callback) {
        ArrayList nameValuePairs = assembleParameters();
        String url = ENDPOINT.replace("USER_ID", User.current().id);

        Http.post(url, nameValuePairs, new Http.Callback() {
            @Override
            public void onSuccess(HttpResponse response) {
                Post p = null;

                try {
                    p = Post.fromJSON(Http.getContentString(response));
                } catch (IOException e) {
                    e.printStackTrace();
                    callback.onError(e);
                }

                callback.onSuccess(p);
            }

            @Override
            public void onError(Exception e) {
                callback.onError(e);
            }
        });
    }

    /**
     * Destroy this post for the current user.
     *
     * @param callback
     */
    public void destroy(final Callback callback) {
        ArrayList nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("_method", "delete"));
        String url = ENDPOINT.replace("USER_ID", User.current().id) + "/" + id;
        Http.post(url, nameValuePairs, new Http.Callback() {

            @Override
            public void onSuccess(HttpResponse httpResponse) {
                callback.onSuccess(Post.this);
            }

            @Override
            public void onError(Exception e) {
                callback.onError(e);
            }
        });
    }

    public static Post fromJSON(String json) {
        Post post = new Post();

        try {
            JSONObject postJson = new JSONObject(json);
            post.id = postJson.getInt("id");
            post.imageUrl = postJson.getString("image_url");
            post.location = postJson.getString("location");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return post;
    }

    public ArrayList<NameValuePair> assembleParameters() {
        String imagePath = saveImage();
        ArrayList nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair(LOCATION_NAME, location));
        nameValuePairs.add(new BasicNameValuePair(IMAGE_NAME, imagePath));
        nameValuePairs.add(new BasicNameValuePair(LONGITUDE_NAME, String.valueOf(longitude)));
        nameValuePairs.add(new BasicNameValuePair(LATITUDE_NAME, String.valueOf(latitude)));

        return nameValuePairs;
    }

    public void fetchImageMap(int targetWidth, int targetHeight, final Callback callback) {
        if( imageUrl != null ) {
            Media.getImage(imageUrl, targetWidth, targetHeight, new Media.ImageCallback() {
                @Override
                public void onSuccess(Bitmap image) {
                    imageMap = image;
                    callback.onSuccess(Post.this);
                }

                @Override
                public void onError(Exception e) {
                    // I dunno
                    callback.onError(e);
                }
            });
        }
    }

    public static abstract class Callback {

        public abstract void onSuccess(Post post);

        public abstract void onError(Exception e);
    }
}
