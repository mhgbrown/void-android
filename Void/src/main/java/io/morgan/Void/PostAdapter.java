package io.morgan.Void;

import android.app.Activity;
import android.content.Context;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by mobrown on 6/22/13.
 */
public class PostAdapter extends ArrayAdapter<Post> {

    public Context context;
    public int layoutResourceId;
    public ArrayList<Post> data;
    public PostListView listView;

    private LikeButtonListener likeButtonListener;

    public PostAdapter(Context context, int layoutResourceId, ArrayList<Post> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
        likeButtonListener = new LikeButtonListener();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final PostHolder holder;
        Post post = data.get(position);

        if(convertView == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            convertView = inflater.inflate(layoutResourceId, parent, false);

            holder = new PostHolder();
            holder.postImage = (ImageView)convertView.findViewById(R.id.post_image);

            WindowManager wm = (WindowManager) parent.getContext().getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();

            holder.postImage.getLayoutParams().height = display.getWidth();
            holder.postLocation = (TextView)convertView.findViewById(R.id.post_location);

            holder.postLikeButton = (ImageButton)convertView.findViewById(R.id.like_button);
            holder.postLikeButton.setOnClickListener(likeButtonListener);

            convertView.setTag(holder);
        }
        else
        {
            holder = (PostHolder) convertView.getTag();
        }

        holder.postLocation.setText(post.location);

        holder.postLikeButton.setTag(position);
        if(post.liked) {
            holder.postLikeButton.setImageResource(R.drawable.heart);
            holder.postLikeButton.setAlpha(255);
        } else {
            holder.postLikeButton.setImageResource(R.drawable.heart_white);
            holder.postLikeButton.setAlpha(96);
        }

        if(post.imageMap == null) {
//            holder.postImage.post(new Runnable() {
//                @Override
//                public void run() {
//                    holder.postImage.setImageDrawable(context.getResources().getDrawable(R.drawable.loading));
//                    AnimationDrawable rocketAnimation = (AnimationDrawable) holder.postImage.getDrawable();
//                    holder.postImage.setVisibility(View.INVISIBLE);
//                    holder.postImage.setVisibility(View.VISIBLE);
//                    rocketAnimation.start();
//                    listView.scrollBy(0, 0);
//                }
//            });

            post.fetchImageMap(holder.postImage.getLayoutParams().height, holder.postImage.getLayoutParams().height, new Post.Callback() {

                @Override
                public void onSuccess(Post post) {
                    holder.postImage.setImageBitmap(post.imageMap);
                    PostAdapter.this.notifyDataSetChanged();
                }

                @Override
                public void onError(Exception e) {
                    Toast.makeText(context, "Sorry, failed to get one of your images", Toast.LENGTH_LONG).show();
                    // set post image to question mark or something
                    // set location text to something like, tap to reload
                    // implement the tap to reload
                }
            });
        } else {
            holder.postImage.setImageBitmap(post.imageMap);
        }

        return convertView;
    }

    /**
     * Remove the post at the given index
     *
     * @param index The index of the post to be removed
     */
    public void removeAt(int index) {
        data.remove(index);
    }

    private class LikeButtonListener implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            ImageButton likeButton = (ImageButton) view;
            final Post post = getItem((Integer)likeButton.getTag());

            if(post.liked) {
                likeButton.setImageResource(R.drawable.heart_white);
                likeButton.setAlpha(96);
            } else {
                likeButton.setImageResource(R.drawable.heart);
                likeButton.setAlpha(255);
            }

            post.likeOrUnlike(new Post.Callback() {
                @Override
                public void onSuccess(Post returnedPost) {
                    notifyDataSetChanged();
                }

                @Override
                public void onError(Exception e) {}
            });
        }
    }

    static class PostHolder {
        ImageButton postLikeButton;
        ImageView postImage;
        TextView postLocation;
    }
}
