package io.morgan.Void;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.AttributeSet;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
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
    private ItemClickListener itemClickListener;
    private ItemLongClickListener itemLongClickListener;
    private CreateContextMenuListener createContextMenuListener;

    public final static int OPTION_LIKE = 1;
    public final static int OPTION_REMOVE = 2;

    public final static String OPTION_LIKE_TEXT = "Like";
    public final static String OPTION_UNLIKE_TEXT = "Unlike";
    public final static String OPTION_REMOVE_TEXT = "Remove";

    public boolean contextMenuDisplayed = false;

    public PostListView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setAdapter(new PostAdapter(context, R.layout.stream_post_view, new ArrayList<Post>()));
        getAdapter().listView = this;

        swipeDetector = new SwipeDetector();
        setOnTouchListener(swipeDetector);

        itemClickListener = new ItemClickListener();
        setOnItemClickListener(itemClickListener);

        itemLongClickListener = new ItemLongClickListener();
        setOnItemLongClickListener(itemLongClickListener);

        createContextMenuListener = new CreateContextMenuListener();
        setOnCreateContextMenuListener(createContextMenuListener);

        fillList();
    }

    public PostAdapter getAdapter() {
        return (PostAdapter) super.getAdapter();
    }

    public void onContextMenuClosed(Object something) {
        itemLongClickListener.listViewItem.setBackgroundResource(R.color.white);
    }

    private void removeDialog(int position, View view) {
        view.setBackgroundResource(R.color.black);
        AlertDialog.Builder adb = new AlertDialog.Builder(getContext());
        adb.setTitle("Remove?");
        adb.setMessage("Are you sure you want to remove this post? You will never see it again.");
        adb.setNegativeButton("Cancel", new CancelDeleteClickListener(view));
        adb.setPositiveButton("Ok", new DeletePostClickListener(position, view));
        adb.show();
    }

    private void remove(final int itemPosition, final View listViewItem) {
        final PostAdapter adapter = getAdapter();
        final Post post = adapter.getItem(itemPosition);

        // optimistically remove the post
        adapter.removeAt(itemPosition);
        adapter.notifyDataSetChanged();
        listViewItem.setBackgroundResource(R.color.white);

        post.destroy(new Post.Callback() {
            @Override
            public void onSuccess(Post post) {
                // great job
            }

            @Override
            public void onError(Exception e) {
                // hoping this works
                adapter.insert(post, itemPosition);
                adapter.notifyDataSetChanged();
                Toast.makeText(getContext(), "Failed to remove, please try again.", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void likeOrUnlike(final int itemPosition, final View listViewItem) {
        final PostAdapter adapter = getAdapter();
        final Post post = adapter.getItem(itemPosition);

        if(post.liked) {
            listViewItem.findViewById(R.id.post_location).setBackgroundResource(R.color.DarkRed);
        } else {
            listViewItem.findViewById(R.id.post_location).setBackgroundResource(R.color.Coral);
        }

        post.likeOrUnlike(new Post.Callback() {
            @Override
            public void onSuccess(Post returnedPost) {
                post.liked = returnedPost.liked;
            }

            @Override
            public void onError(Exception e) {
                // if the post was liked
                    // add back the like indicator
                    // give a message that the like failed
                // else
                    // just say that it failed to like
            }
        });
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
                removeDialog(position, view);
            }
        }
    }

    private class CancelDeleteClickListener implements AlertDialog.OnClickListener {

        private View listViewItem;

        public CancelDeleteClickListener(View listViewItem) {
            this.listViewItem = listViewItem;
        }

        @Override
        public void onClick(DialogInterface dialogInterface, int which) {
            listViewItem.setBackgroundResource(R.color.white);
        }
    }

    private class DeletePostClickListener implements AlertDialog.OnClickListener {

        private int itemPosition;
        private View listViewItem;

        public DeletePostClickListener(int itemPosition, View listViewItem) {
            this.itemPosition = itemPosition;
            this.listViewItem = listViewItem;
        }

        @Override
        public void onClick(DialogInterface dialogInterface, int which) {
            remove(itemPosition, listViewItem);
        }
    }

    private class ItemLongClickListener implements OnItemLongClickListener {

        public View listViewItem;
        public int itemPosition;

        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            listViewItem = view;
            itemPosition = position;
            ((Activity)getContext()).openContextMenu(parent);
            return true;
        }
    }

    private class CreateContextMenuListener implements View.OnCreateContextMenuListener {

        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            MenuItem menuItem;
            itemLongClickListener.listViewItem.setBackgroundResource(R.color.black);

            Post post = getAdapter().getItem(itemLongClickListener.itemPosition);
            if(post.liked) {
                menuItem = contextMenu.add(Menu.NONE, OPTION_LIKE, 0, OPTION_UNLIKE_TEXT);
            } else {
                menuItem = contextMenu.add(Menu.NONE, OPTION_LIKE, 0, OPTION_LIKE_TEXT);
            }

            menuItem.setOnMenuItemClickListener(new MenuItemClickListener());
            menuItem = contextMenu.add(Menu.NONE, OPTION_REMOVE, 0, OPTION_REMOVE_TEXT);
            menuItem.setOnMenuItemClickListener(new MenuItemClickListener());
            contextMenuDisplayed = true;
        }
    }

    private class MenuItemClickListener implements MenuItem.OnMenuItemClickListener {

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch(menuItem.getItemId()) {
                case OPTION_LIKE:
                    likeOrUnlike(itemLongClickListener.itemPosition, itemLongClickListener.listViewItem);
                    break;
                case OPTION_REMOVE:
                    removeDialog(itemLongClickListener.itemPosition, itemLongClickListener.listViewItem);
                    break;
                default:
                    break;
            }

            return false;
        }
    }
}
