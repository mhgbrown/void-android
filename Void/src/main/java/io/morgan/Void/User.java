package io.morgan.Void;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;

/**
 * Created by mobrown on 6/23/13.
 */
public class User {
    public final String VOID_ID_NAME = "user[void_id]";

    public String voidId = null;

    public User(String voidId) {
        this.voidId = voidId;
    }

    public ArrayList<NameValuePair> assembleParameters() {
        ArrayList nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair(VOID_ID_NAME, voidId));

        return nameValuePairs;
    }
}
