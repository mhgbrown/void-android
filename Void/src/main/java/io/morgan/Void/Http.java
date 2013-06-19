package io.morgan.Void;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created by mobrown on 6/17/13.
 */
public class Http {

    public static void post(final String url, final List<NameValuePair> nameValuePairs, final Callback callback) {

        AsyncTask<Void, Void, HttpResponse> post = new AsyncTask<Void, Void, HttpResponse>() {
            HttpClient httpClient = new DefaultHttpClient();
            HttpContext localContext = new BasicHttpContext();
            HttpPost httpPost = new HttpPost(url);

            @Override
            protected HttpResponse doInBackground(Void... nothing) {

                try {
                    MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);

                    for(int index = 0; index < nameValuePairs.size(); index++) {
                        if(nameValuePairs.get(index).getName().contains("image")) {
                            // If the key equals to "image", we use FileBody to transfer the data
                            entity.addPart(nameValuePairs.get(index).getName(), new FileBody(new File(nameValuePairs.get(index).getValue())));
                        } else {
                            // Normal string data
                            entity.addPart(nameValuePairs.get(index).getName(), new StringBody(nameValuePairs.get(index).getValue()));
                        }
                    }

                    httpPost.setEntity(entity);

                    HttpResponse response = httpClient.execute(httpPost, localContext);
                    return response;
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(HttpResponse response) {
                if(response == null) {
                    callback.onError(new Exception("Response is null"));
                }

                if(response.getStatusLine().getStatusCode() < 200 || response.getStatusLine().getStatusCode() >= 300) {
                    callback.onError(new Exception("Non-200 status"));
                } else {
                    callback.onSuccess(response);
                }

                httpClient.getConnectionManager().shutdown();
            }
        };

        post.execute();
    }

    public static abstract class Callback {

        public abstract void onSuccess(HttpResponse httpResponse);

        public abstract void onError(Exception e);
    }
}
