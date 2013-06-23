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

/**
 * Created by mobrown on 6/22/13.
 */
public class PostAdapter extends ArrayAdapter<Post> {

    Context context;
    int layoutResourceId;
    Post data[];

    public PostAdapter(Context context, int layoutResourceId, Post[] data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        PostHolder holder = null;

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

        Post post = data[position];
        holder.postLocation.setText(post.location);
        holder.postImage.setImageBitmap(post.rotatedImageMap(90));

        return row;
    }

    public static class PostHolder {
        ImageView postImage;
        TextView postLocation;
    }
}
