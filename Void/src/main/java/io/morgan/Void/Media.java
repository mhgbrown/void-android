package io.morgan.Void;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by mobrown on 6/18/13.
 *
 * References:
 * http://developer.android.com/guide/topics/media/camera.html#saving-media
 * http://stackoverflow.com/questions/924990/how-to-cache-inputstream-for-multiple-use
 * http://stackoverflow.com/questions/477572/strange-out-of-memory-issue-while-loading-an-image-to-a-bitmap-object
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

    public static void getImage(String url, final int targetWidth, final int targetHeight, final ImageCallback callback) {
        AsyncTask<String, Void, Bitmap> imageFetcher = new AsyncTask<String, Void, Bitmap>() {
            Exception exception;

            private Bitmap decodeStream(InputStream inputStream) throws IOException {
                Bitmap b;

                //Decode image size
                BitmapFactory.Options o = new BitmapFactory.Options();
                o.inJustDecodeBounds = true;
                BitmapFactory.decodeStream(inputStream, null, o);
                inputStream.reset();

                int scale = 1;
                if (o.outHeight > targetHeight || o.outWidth > targetWidth) {
                    scale = (int)Math.pow(2, (int) Math.round(Math.log(Math.max(targetHeight, targetWidth) /
                            (double) Math.max(o.outHeight, o.outWidth)) / Math.log(0.5)));
                }

                //Decode with inSampleSize
                BitmapFactory.Options o2 = new BitmapFactory.Options();
                o2.inSampleSize = scale;
                b = BitmapFactory.decodeStream(inputStream, null, o2);
                inputStream.close();

                return b;
            }

            @Override
            protected Bitmap doInBackground(String... urls) {
                String imageUrl = urls[0];
                Bitmap imageMap;
                HttpGet httpRequest = new HttpGet(imageUrl);
                HttpClient httpclient = new DefaultHttpClient();

                try {
                    HttpResponse response =  httpclient.execute(httpRequest);
                    HttpEntity entity = response.getEntity();
                    BufferedHttpEntity bufferedHttpEntity = new BufferedHttpEntity(entity);
                    InputStream is = bufferedHttpEntity.getContent();
                    imageMap = decodeStream(is);
                }
                catch (Exception e) {
                    exception = e;
                    e.printStackTrace();
                    return null;
                }

                return imageMap;
            }

            @Override
            protected void onPostExecute(Bitmap image) {
                if(image == null) {
                    callback.onError(exception);
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
