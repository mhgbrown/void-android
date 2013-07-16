package io.morgan.Void;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by mobrown on 7/15/13.
 */
public class PostListView extends ListView {

    private SwipeDetector swipeDetector;

    public PostListView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setAdapter(new PostAdapter(context, R.layout.stream_post_view, new ArrayList<Post>()));

        swipeDetector = new SwipeDetector();
        setOnTouchListener(swipeDetector);
        setOnItemClickListener(new ItemClickListener());

        fillList();
    }

    public PostAdapter getAdapter() {
        return (PostAdapter) super.getAdapter();
    }

    private void fillList() {
        if(User.current().id == null) {
            return;
        }

        String url = Post.ENDPOINT.replace("USER_ID", User.current().id);
        Http.get(url, new Http.Callback() {

            @Override
            public void onSuccess(HttpResponse httpResponse) {
                try {
                    String json = Http.getContentString(httpResponse);
                    JSONArray postsJson = new JSONArray(json);

                    if(postsJson.length() == 0) {
                        return;
                    }

                    for (int i = 0; i < postsJson.length(); i++) {
                        JSONObject aPost = postsJson.getJSONObject(i);
                        Post tmp = Post.fromJSON(aPost.toString());
                        getAdapter().add(tmp);
                    }

                    getAdapter().notifyDataSetChanged();

                } catch (IOException e) {
                    onError(e);
                } catch (JSONException e) {
                    onError(e);
                }
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(getContext(), "Sorry, couldn't get your stream", Toast.LENGTH_LONG).show();
            }
        });
    }

    private class ItemClickListener implements OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (swipeDetector.getAction() == SwipeDetector.Action.LR || swipeDetector.getAction() == SwipeDetector.Action.RL) {
                AlertDialog.Builder adb = new AlertDialog.Builder(getContext());
                adb.setTitle("Delete?");
                adb.setMessage("Are you sure you want to delete this post? You will not see it again.");
                adb.setNegativeButton("Cancel", null);
                adb.setPositiveButton("Ok", new DeletePostClickListener(position));
                adb.show();
            }
        }
    }

    private class DeletePostClickListener implements AlertDialog.OnClickListener {

        private int itemPosition;

        public DeletePostClickListener(int itemPosition) {
            this.itemPosition = itemPosition;
        }

        @Override
        public void onClick(DialogInterface dialogInterface, int which) {
            final PostAdapter adapter = getAdapter();
            Post post = adapter.getItem(itemPosition);
            post.destroy(new Post.Callback() {
                @Override
                public void onSuccess(Post post) {
                    adapter.removeAt(itemPosition);
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onError(Exception e) {
                    Toast.makeText(getContext(), "Failed to delete, please try again.", Toast.LENGTH_LONG).show();
                }
            });

        }
    }

    // postsList.setOnItemLongClickListener(new OnItemLongClickListener() { ...
}
