package io.morgan.Void;

import android.graphics.Bitmap;
import android.graphics.Matrix;
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
    public static final String ENDPOINT = "http://void-server.herokuapp.com/posts";
    public static final String LOCATION_NAME = "post[location]";
    public static final String IMAGE_NAME = "post[image]";

    public int id = -1;
    public String location = "Somewhere";
    public String imageUrl = null;
    public byte[] image = new byte[1];
    public Bitmap imageMap = null;

    public Post() {
        this.location = "Somewhere";
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

    public boolean save(final Callback callback) {
        ArrayList nameValuePairs = assembleParameters();
        callback.onBeforeSend(nameValuePairs);

        Http.post(ENDPOINT, nameValuePairs, new Http.Callback() {
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

        return true;
    }

    public Bitmap rotatedImageMap(int rotation) {
        Matrix matrix = new Matrix();
        matrix.postRotate(rotation);
        Bitmap rotatedImageMap = Bitmap.createBitmap(imageMap, 0, 0, imageMap.getWidth(), imageMap.getHeight(), matrix, true);

        return rotatedImageMap;
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

        return nameValuePairs;
    }

    public void fetchImageMap(final Callback callback) {
        if( imageUrl != null ) {
            Media.getImage(imageUrl, new Media.ImageCallback() {
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

        public abstract void onBeforeSend(ArrayList<NameValuePair> nameValuePairs);

        public abstract void onSuccess(Post post);

        public abstract void onError(Exception e);
    }
}
