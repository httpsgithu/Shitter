package org.nuclearfog.twidda.backend;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

public class SendStatus extends AsyncTask<Object, Void, Boolean> {

    private Context context;
    private String path;

    /**
     * @param context Context of #TweetPopup
     * @param path Internal Path of the Image
     */
    public SendStatus(Context context, String path) {
        this.context = context;
        this.path = path;
    }

    /**
     * @param args Argument + Text
     *             args[0] = TWEET TEXT
     *             args[1] = REPLY TWEET ID
     */
    @Override
    protected Boolean doInBackground(Object... args) {
        try {
            Long id = -1L;
            String tweet = (String) args[0];
            if(args.length > 1)
                id = (Long) args[1];
            TwitterEngine.getInstance(context).sendStatus(tweet,id,path);
            return true;
        } catch(Exception err) {
            err.printStackTrace();
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean success) {
        if(success) {
            Toast.makeText(context, "Tweet wurde gesendet!", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(context, "Fehler beim Senden des Tweets!", Toast.LENGTH_LONG).show();
        }
    }
}