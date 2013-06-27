package io.morgan.Void;

import android.app.Activity;
import android.content.Context;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;

import java.util.ArrayList;

/**
 * Created by mobrown on 6/22/13.
 */
public class PostAdapter extends ArrayAdapter<Post> {

    Context context;
    int layoutResourceId;
    ArrayList<Post> data;

    public PostAdapter(Context context, int layoutResourceId, ArrayList<Post> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        final PostHolder holder;

        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new PostHolder();
            holder.postImage = (ImageView)row.findViewById(R.id.post_image);

            WindowManager wm = (WindowManager) parent.getContext().getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();

            holder.postImage.getLayoutParams().height = display.getWidth();
            holder.postLocation = (TextView)row.findViewById(R.id.post_location);

            row.setTag(holder);
        }
        else
        {
            holder = (PostHolder)row.getTag();
        }

        Post post = data.get(position);
        holder.postLocation.setText(post.location);

        if(post.imageMap == null) {
            post.fetchImageMap(new Post.Callback() {
                @Override
                public void onBeforeSend(ArrayList<NameValuePair> nameValuePairs) {
                    // nothing
                }

                @Override
                public void onSuccess(Post post) {
                    holder.postImage.setImageBitmap(post.imageMap);
                }

                @Override
                public void onError(Exception e) {
                    Toast.makeText(context, "Sorry, failed to get one of your images", Toast.LENGTH_LONG).show();
                }
            });
        } else {
            holder.postImage.setImageBitmap(post.imageMap);
        }

        return row;
    }

    public static class PostHolder {
        ImageView postImage;
        TextView postLocation;
    }
}
