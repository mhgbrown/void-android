package io.morgan.Void;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import org.apache.http.HttpResponse;

import java.io.File;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by mobrown on 6/18/13.
 *
 * References:
 * http://developer.android.com/guide/topics/media/camera.html#saving-media
 */
public class Media {

    public static File createOutputFile() {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Void");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()){
            if (!mediaStorageDir.mkdirs()){
                Log.d("Void", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_"+ timeStamp + ".jpg");

        return mediaFile;
    }

    public static void getImage(String url, final ImageCallback callback) {
        AsyncTask<String, Void, Bitmap> imageFetcher = new AsyncTask<String, Void, Bitmap>() {

            @Override
            protected Bitmap doInBackground(String... urls) {
                String imageUrl = urls[0];
                Bitmap imageMap = null;

                try {
                    imageMap = BitmapFactory.decodeStream(new URL(imageUrl).openConnection().getInputStream());
                }
                catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }

                return imageMap;
            }

            @Override
            protected void onPostExecute(Bitmap image) {
                if(image == null) {
                    callback.onError(new Exception("image is null"));
                    return;
                }

                callback.onSuccess(image);
            }
        };

        imageFetcher.execute(url);
    }

    public static abstract class ImageCallback {

        public abstract void onSuccess(Bitmap image);

        public abstract void onError(Exception e);
    }
}
