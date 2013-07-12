package io.morgan.Void;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.UUID;

/**
 * Created by mobrown on 6/23/13.
 */
public class User {
    private static final String VOID_ID = "VOID_ID";

    private static User current = null;

    public String id = null;

    public synchronized static User current() {
        if(current == null) {
            current = new User();
        }

        return current;
    }

    private User() {
        setId();
    }

    private void setId() {
        Context context = App.getAppContext();

        if (id == null) {
            SharedPreferences sharedPrefs = context.getSharedPreferences(VOID_ID, Context.MODE_PRIVATE);
            id = sharedPrefs.getString(VOID_ID, null);
            if (id == null) {
                id = UUID.randomUUID().toString();
                SharedPreferences.Editor editor = sharedPrefs.edit();
                editor.putString(VOID_ID, id);
                editor.commit();
            }
        }
    }
}
