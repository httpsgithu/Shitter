package org.nuclearfog.twidda.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.nuclearfog.twidda.R;
import org.nuclearfog.twidda.backend.UserListUpdater;
import org.nuclearfog.twidda.backend.engine.EngineException;
import org.nuclearfog.twidda.backend.holder.ListHolder;
import org.nuclearfog.twidda.backend.utils.DialogBuilder;
import org.nuclearfog.twidda.backend.utils.DialogBuilder.OnDialogClick;
import org.nuclearfog.twidda.backend.utils.ErrorHandler;
import org.nuclearfog.twidda.backend.utils.FontTool;
import org.nuclearfog.twidda.database.GlobalSettings;

import static android.os.AsyncTask.Status.RUNNING;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static org.nuclearfog.twidda.activity.ListDetail.RET_LIST_CHANGED;
import static org.nuclearfog.twidda.activity.TwitterList.RET_LIST_CREATED;
import static org.nuclearfog.twidda.backend.utils.DialogBuilder.DialogType.LISTPOPUP_LEAVE;

/**
 * Popup activity for the list editor
 */
public class ListPopup extends AppCompatActivity implements OnClickListener, OnDialogClick {

    /**
     * Key for the list ID of the list if an existing list should be updated
     */
    public static final String KEY_LIST_ID = "list_id";

    /**
     * Key for the title of the list
     */
    public static final String KEY_LIST_TITLE = "list_title";

    /**
     * Key for the list description
     */
    public static final String KEY_LIST_DESCR = "list_description";

    /**
     * Key for the visibility of the list
     */
    public static final String KEY_LIST_VISIB = "list_visibility";

    private UserListUpdater updaterAsync;
    private EditText titleInput, subTitleInput;
    private CompoundButton visibility;
    private View progressCircle;
    private Dialog leaveDialog;

    private long listId = -1;
    private String title = "";
    private String description = "";
    private boolean isPublic = false;


    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.popup_userlist);
        View root = findViewById(R.id.list_popup_root);
        Button updateButton = findViewById(R.id.userlist_create_list);
        TextView popupTitle = findViewById(R.id.popup_list_title);
        titleInput = findViewById(R.id.list_edit_title);
        subTitleInput = findViewById(R.id.list_edit_descr);
        visibility = findViewById(R.id.list_edit_public_sw);
        progressCircle = findViewById(R.id.list_popup_loading);

        GlobalSettings settings = GlobalSettings.getInstance(this);
        root.setBackgroundColor(settings.getPopupColor());
        FontTool.setViewFont(settings, root);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            listId = extras.getLong(KEY_LIST_ID, -1);
            title = extras.getString(KEY_LIST_TITLE, "");
            description = extras.getString(KEY_LIST_DESCR, "");
            isPublic = extras.getBoolean(KEY_LIST_VISIB, false);
        }
        titleInput.setText(title);
        subTitleInput.setText(description);
        visibility.setChecked(isPublic);
        if (listId > 0) {
            popupTitle.setText(R.string.menu_edit_list);
            updateButton.setText(R.string.update_list);
        }
        leaveDialog = DialogBuilder.create(this, LISTPOPUP_LEAVE, this);
        updateButton.setOnClickListener(this);
    }


    @Override
    public void onBackPressed() {
        if (visibility.isChecked() == isPublic && titleInput.getText().toString().equals(title)
                && subTitleInput.getText().toString().equals(description)) {
            super.onBackPressed();
        } else {
            if (!leaveDialog.isShowing()) {
                leaveDialog.show();
            }
        }
    }


    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.userlist_create_list) {
            String titleStr = titleInput.getText().toString();
            String descrStr = subTitleInput.getText().toString();
            boolean isPublic = visibility.isChecked();
            if (titleStr.trim().isEmpty()) {
                Toast.makeText(this, R.string.error_empty_list_information, Toast.LENGTH_SHORT).show();
            } else if (updaterAsync == null || updaterAsync.getStatus() != RUNNING) {
                ListHolder mHolder;
                if (listId > 0) {
                    // update existing list
                    mHolder = new ListHolder(titleStr, descrStr, isPublic, listId);
                } else {
                    // create new one
                    mHolder = new ListHolder(titleStr, descrStr, isPublic);
                }
                updaterAsync = new UserListUpdater(this);
                updaterAsync.execute(mHolder);
                progressCircle.setVisibility(VISIBLE);
            }
        }
    }


    @Override
    public void onConfirm(DialogBuilder.DialogType type) {
        finish();
    }

    /**
     * called when a list was updated successfully
     */
    public void onSuccess() {
        if (listId > 0) {
            Toast.makeText(this, R.string.info_list_updated, Toast.LENGTH_SHORT).show();
            setResult(RET_LIST_CHANGED);
        } else {
            // it's a new list, if no list ID is defined
            Toast.makeText(this, R.string.info_list_created, Toast.LENGTH_SHORT).show();
            setResult(RET_LIST_CREATED);
        }
        finish();
    }

    /**
     * called when an error occurs while updating a list
     *
     * @param err twitter exception
     */
    public void onError(@Nullable EngineException err) {
        if (err != null)
            ErrorHandler.handleFailure(this, err);
        progressCircle.setVisibility(INVISIBLE);
    }
}