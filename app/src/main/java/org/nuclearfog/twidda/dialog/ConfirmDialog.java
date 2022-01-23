package org.nuclearfog.twidda.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;

import org.nuclearfog.twidda.R;
import org.nuclearfog.twidda.backend.utils.AppStyles;
import org.nuclearfog.twidda.database.GlobalSettings;

/**
 * this dialog is to confirm for user action
 *
 * @author nuclearfog
 */
public class ConfirmDialog extends Dialog implements OnClickListener {

    /**
     * types of dialogs, every dialog has its own message and title
     */
    public enum DialogType {
        WRONG_PROXY,
        DELETE_APP_DATA,
        APP_LOG_OUT,
        REMOVE_ACCOUNT,
        VIDEO_ERROR,
        TWEET_DELETE,
        TWEET_EDITOR_LEAVE,
        TWEET_EDITOR_ERROR,
        MESSAGE_DELETE,
        MESSAGE_EDITOR_LEAVE,
        MESSAGE_EDITOR_ERROR,
        PROFILE_EDITOR_LEAVE,
        PROFILE_EDITOR_ERROR,
        PROFILE_UNFOLLOW,
        PROFILE_BLOCK,
        PROFILE_MUTE,
        LIST_REMOVE_USER,
        LIST_UNFOLLOW,
        LIST_DELETE,
        LIST_EDITOR_LEAVE,
        LIST_EDITOR_ERROR
    }

    private TextView txtTitle, txtMessage;
    private Button confirm, cancel;

    @Nullable
    private OnConfirmListener listener;

    /**
     *
     */
    public ConfirmDialog(Context context) {
        super(context, R.style.ConfirmDialog);
        setContentView(R.layout.dialog_confirm);
        ViewGroup root = findViewById(R.id.confirm_rootview);
        confirm = findViewById(R.id.confirm_yes);
        cancel = findViewById(R.id.confirm_no);
        txtTitle = findViewById(R.id.confirm_title);
        txtMessage = findViewById(R.id.confirm_message);

        confirm.setCompoundDrawablesWithIntrinsicBounds(R.drawable.check, 0, 0, 0);
        cancel.setCompoundDrawablesWithIntrinsicBounds(R.drawable.cross, 0, 0, 0);
        GlobalSettings settings = GlobalSettings.getInstance(context);
        AppStyles.setTheme(root, settings.getBackgroundColor());

        confirm.setOnClickListener(this);
        cancel.setOnClickListener(this);
    }

    /**
     * creates an alert dialog
     *
     * @param type Type of dialog to show
     */
    public void show(DialogType type) {
        if (isShowing())
            return;
        confirm.setTag(type);
        // reset modifications
        if (cancel.getVisibility() != View.VISIBLE)
            cancel.setVisibility(View.VISIBLE);
        if (confirm.getCompoundDrawables()[0] == null)
            confirm.setCompoundDrawablesWithIntrinsicBounds(R.drawable.check, 0, 0, 0);
        // setup buttons and message
        switch (type) {
            case MESSAGE_DELETE:
                txtTitle.setVisibility(View.GONE);
                txtMessage.setText(R.string.confirm_delete_message);
                confirm.setText(android.R.string.ok);
                cancel.setText(android.R.string.cancel);
                break;

            case WRONG_PROXY:
                txtTitle.setText(R.string.info_error);
                txtMessage.setText(R.string.error_wrong_connection_settings);
                confirm.setText(android.R.string.ok);
                cancel.setText(android.R.string.cancel);
                break;

            case DELETE_APP_DATA:
                txtTitle.setVisibility(View.GONE);
                txtMessage.setText(R.string.confirm_delete_database);
                confirm.setText(android.R.string.ok);
                cancel.setText(android.R.string.cancel);
                break;

            case APP_LOG_OUT:
                txtTitle.setVisibility(View.GONE);
                txtMessage.setText(R.string.confirm_log_lout);
                confirm.setText(android.R.string.ok);
                cancel.setText(android.R.string.cancel);
                break;

            case VIDEO_ERROR:
                txtTitle.setText(R.string.error_cant_load_video);
                confirm.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                confirm.setText(R.string.confirm_open_link);
                cancel.setVisibility(View.GONE);
                break;

            case LIST_EDITOR_LEAVE:
            case PROFILE_EDITOR_LEAVE:
                txtTitle.setVisibility(View.GONE);
                txtMessage.setText(R.string.confirm_discard);
                confirm.setText(android.R.string.ok);
                cancel.setText(android.R.string.cancel);
                break;

            case TWEET_EDITOR_LEAVE:
                txtTitle.setVisibility(View.GONE);
                txtMessage.setText(R.string.confirm_cancel_tweet);
                confirm.setText(android.R.string.ok);
                cancel.setText(android.R.string.cancel);
                break;

            case MESSAGE_EDITOR_LEAVE:
                txtTitle.setVisibility(View.GONE);
                txtMessage.setText(R.string.confirm_cancel_message);
                confirm.setText(android.R.string.ok);
                cancel.setText(android.R.string.cancel);
                break;

            case LIST_EDITOR_ERROR:
            case MESSAGE_EDITOR_ERROR:
            case TWEET_EDITOR_ERROR:
            case PROFILE_EDITOR_ERROR:
                txtTitle.setText(R.string.info_error);
                confirm.setText(R.string.confirm_retry_button);
                cancel.setText(android.R.string.cancel);
                break;

            case TWEET_DELETE:
                txtTitle.setVisibility(View.GONE);
                txtMessage.setText(R.string.confirm_delete_tweet);
                confirm.setText(android.R.string.ok);
                cancel.setText(android.R.string.cancel);
                break;

            case PROFILE_UNFOLLOW:
                txtTitle.setVisibility(View.GONE);
                txtMessage.setText(R.string.confirm_unfollow);
                confirm.setText(android.R.string.ok);
                cancel.setText(android.R.string.cancel);
                break;

            case PROFILE_BLOCK:
                txtTitle.setVisibility(View.GONE);
                txtMessage.setText(R.string.confirm_block);
                confirm.setText(android.R.string.ok);
                cancel.setText(android.R.string.cancel);
                break;

            case PROFILE_MUTE:
                txtTitle.setVisibility(View.GONE);
                txtMessage.setText(R.string.confirm_mute);
                confirm.setText(android.R.string.ok);
                cancel.setText(android.R.string.cancel);
                break;

            case LIST_REMOVE_USER:
                txtTitle.setVisibility(View.GONE);
                txtMessage.setText(R.string.confirm_remove_user_from_list);
                confirm.setText(android.R.string.ok);
                cancel.setText(android.R.string.cancel);
                break;

            case LIST_UNFOLLOW:
                txtMessage.setText(R.string.confirm_unfollow_list);
                txtTitle.setVisibility(View.GONE);
                confirm.setText(android.R.string.ok);
                cancel.setText(android.R.string.cancel);
                break;

            case LIST_DELETE:
                txtMessage.setText(R.string.confirm_delete_list);
                txtTitle.setVisibility(View.GONE);
                confirm.setText(android.R.string.ok);
                cancel.setText(android.R.string.cancel);
                break;

            case REMOVE_ACCOUNT:
                txtMessage.setText(R.string.confirm_remove_account);
                txtTitle.setVisibility(View.GONE);
                confirm.setText(android.R.string.ok);
                cancel.setText(android.R.string.cancel);
                break;
        }
        super.show();
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.confirm_yes) {
            Object tag = v.getTag();
            if (listener != null && tag instanceof DialogType) {
                DialogType type = (DialogType) tag;
                listener.onConfirm(type);
            }
            dismiss();
        } else if (v.getId() == R.id.confirm_no) {
            dismiss();
        }
    }

    /**
     * add confirm listener
     */
    public void setConfirmListener(OnConfirmListener listener) {
        this.listener = listener;
    }

    /**
     * set message text
     *
     * @param message message text
     */
    public void setMessage(String message) {
        txtMessage.setText(message);
    }

    /**
     * Alert dialog listener
     */
    public interface OnConfirmListener {

        /**
         * called when the positive button was clicked
         *
         * @param type type of dialog
         */
        void onConfirm(DialogType type);
    }
}