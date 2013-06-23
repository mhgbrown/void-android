package io.morgan.Void;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.UUID;

/**
 * Created by mobrown on 6/23/13.
 */
public class Identity {
    private static final String VOID_ID = "VOID_ID";

    private static String id = null;

    public synchronized static String id(Context context) {
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

        return id;
    }
}
