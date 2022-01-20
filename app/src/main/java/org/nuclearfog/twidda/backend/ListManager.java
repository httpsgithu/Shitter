package org.nuclearfog.twidda.backend;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.Nullable;

import org.nuclearfog.twidda.backend.api.Twitter;
import org.nuclearfog.twidda.backend.api.TwitterException;
import org.nuclearfog.twidda.backend.utils.ErrorHandler;

import java.lang.ref.WeakReference;

/**
 * Backend async task to manage users on lists
 * Twitter users can be added and removed
 *
 * @author nuclearfog
 */
public class ListManager extends AsyncTask<String, Void, String> {

    /**
     * actions to be taken
     */
    public enum Action {
        /**
         * add user to list
         */
        ADD_USER,
        /**
         * remove user from list
         */
        DEL_USER
    }

    private TwitterException err;
    private Twitter twitter;
    private WeakReference<ListManagerCallback> callback;

    private long listId;
    private Action action;

    /**
     * @param listId   ID of the user list
     * @param action   what action should be performed
     * @param c        activity context
     * @param callback callback to update information
     */
    public ListManager(long listId, Action action, Context c, ListManagerCallback callback) {
        super();
        twitter = Twitter.get(c);
        this.listId = listId;
        this.action = action;
        this.callback = new WeakReference<>(callback);
    }


    @Override
    protected String doInBackground(String... strings) {
        try {
            switch (action) {
                case ADD_USER:
                    twitter.addUserToUserlist(listId, strings[0]);
                    break;

                case DEL_USER:
                    twitter.removeUserFromUserlist(listId, strings[0]);
                    break;
            }
            return strings[0];
        } catch (TwitterException err) {
            this.err = err;
        }
        return null;
    }


    @Override
    protected void onPostExecute(String names) {
        if (callback.get() != null) {
            if (names != null) {
                callback.get().onSuccess(names);
            } else {
                callback.get().onFailure(err);
            }
        }
    }

    /**
     * Callback interface for Activities or fragments
     */
    public interface ListManagerCallback {

        /**
         * Called when AsyncTask finished successfully
         *
         * @param names the names of the users added or removed from list
         */
        void onSuccess(String names);

        /**
         * called when an error occurs
         *
         * @param err Engine exception thrown by backend
         */
        void onFailure(@Nullable ErrorHandler.TwitterError err);
    }
}