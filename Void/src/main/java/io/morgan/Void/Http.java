package io.morgan.Void;

import android.os.AsyncTask;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

/**
 * Created by mobrown on 6/17/13.
 */
public class Http {

    public static void get(String url, final Callback callback) {

        AsyncTask<String, Void, HttpResponse> get = new AsyncTask<String, Void, HttpResponse>() {
            Exception exception;
            HttpClient httpClient = new DefaultHttpClient();

            @Override
            protected HttpResponse doInBackground(String... urls) {

                try {
                    String url = urls[0];
                    URI website;

                    website = new URI(url);
                    HttpGet request = new HttpGet();
                    request.setURI(website);
                    HttpResponse response = httpClient.execute(request);
                    return response;
                } catch (URISyntaxException e) {
                    exception = e;
                    e.printStackTrace();
                    return null;
                } catch (ClientProtocolException e) {
                    exception = e;
                    e.printStackTrace();
                    return null;
                } catch (IOException e) {
                    exception = e;
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(HttpResponse response) {
                if(response == null) {
                    callback.onError(exception);
                    return;
                }

                if(response.getStatusLine().getStatusCode() < 200 || response.getStatusLine().getStatusCode() >= 300) {
                    callback.onError(new Exception("Non-200 status"));
                } else {
                    callback.onSuccess(response);
                }

                httpClient.getConnectionManager().shutdown();
            }
        };

        get.execute(url);
    }

    public static void post(final String url, final List<NameValuePair> nameValuePairs, final Callback callback) {

        AsyncTask<Void, Void, HttpResponse> post = new AsyncTask<Void, Void, HttpResponse>() {
            Exception exception;
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
                    exception = e;
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(HttpResponse response) {
                if(response == null) {
                    callback.onError(exception);
                    return;
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

    public static String getContentString(HttpResponse response) throws IOException {
        HttpEntity entity;
        InputStream inputStream;
        String result;

        entity = response.getEntity();
        inputStream = entity.getContent();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
        StringBuilder sb = new StringBuilder();

        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line + "\n");
        }
        result = sb.toString();

        return result;
    }

    public static abstract class Callback {

        public abstract void onSuccess(HttpResponse httpResponse);

        public abstract void onError(Exception e);
    }
}
