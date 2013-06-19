package io.morgan.Void;

import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by mobrown on 6/17/13.
 */
public class Post {
//    public final String ENDPOINT = "http://void.67.218.102.218.xip.io/posts";
    public final String ENDPOINT = "http://void.192.168.1.2.xip.io/posts";
    public final String LOCATION_NAME = "post[location]";
    public final String IMAGE_NAME = "post[image]";

    private String location = "Somewhere";
    private byte[] image = new byte[1];

    public Post() {
        this.location = "Somewhere";
        this.image = new byte[1];
    }

    public Post(String location, byte[] image) {
        this.location = location;
        this.image = image;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setImage(byte[] image) {
        this.image = image;
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
        String imagePath = saveImage();
        BasicNameValuePair locationData = new BasicNameValuePair(LOCATION_NAME, location);
        BasicNameValuePair imageData = new BasicNameValuePair(IMAGE_NAME, imagePath);
        ArrayList nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(locationData);
        nameValuePairs.add(imageData);
        Http.post(ENDPOINT, nameValuePairs, new Http.Callback() {
            @Override
            public void onSuccess(HttpResponse httpResponse) {
                callback.onSuccess();
            }

            @Override
            public void onError(Exception e) {
                callback.onError();
            }
        });
        return true;
    }

    public static abstract class Callback {

        public abstract void onSuccess();

        public abstract void onError();
    }
}
