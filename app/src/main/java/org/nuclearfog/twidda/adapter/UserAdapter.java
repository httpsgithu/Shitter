package org.nuclearfog.twidda.adapter;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static androidx.recyclerview.widget.RecyclerView.NO_ID;
import static androidx.recyclerview.widget.RecyclerView.NO_POSITION;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.squareup.picasso.Picasso;

import org.nuclearfog.twidda.R;
import org.nuclearfog.twidda.adapter.holder.Footer;
import org.nuclearfog.twidda.adapter.holder.UserHolder;
import org.nuclearfog.twidda.backend.lists.Users;
import org.nuclearfog.twidda.backend.utils.PicassoBuilder;
import org.nuclearfog.twidda.database.GlobalSettings;
import org.nuclearfog.twidda.model.User;

import java.text.NumberFormat;

import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

/**
 * custom {@link androidx.recyclerview.widget.RecyclerView} adapter implementation to show users
 *
 * @author nuclearfog
 * @see org.nuclearfog.twidda.ui.fragments.UserFragment
 */
public class UserAdapter extends Adapter<ViewHolder> {

	/**
	 * index of {@link #loadingIndex} if no index is defined
	 */
	private static final int NO_LOADING = -1;

	/**
	 * View type for an user item
	 */
	private static final int ITEM_USER = 0;

	/**
	 * View type for a placeholder item
	 */
	private static final int ITEM_GAP = 1;

	/**
	 * locale specific number formatter
	 */
	private static final NumberFormat NUM_FORMAT = NumberFormat.getIntegerInstance();

	private UserClickListener listener;
	private GlobalSettings settings;
	private Picasso picasso;

	private Users data = new Users(0L, 0L);
	private int loadingIndex = NO_LOADING;
	private boolean userRemovable = false;

	/**
	 * @param listener click listener
	 */
	public UserAdapter(Context context, UserClickListener listener) {
		this.listener = listener;
		settings = GlobalSettings.getInstance(context);
		picasso = PicassoBuilder.get(context);
	}

	/**
	 * insert an user list depending on cursor to the top or bottom
	 *
	 * @param newData new userlist
	 */
	@MainThread
	public void setData(@NonNull Users newData) {
		disableLoading();
		// add empty list
		if (newData.isEmpty()) {
			// remove page footer if there isn't a next page
			if (!data.isEmpty() && data.peekLast() == null) {
				int end = data.size() - 1;
				data.remove(end);
				notifyItemRemoved(end);
			}
		}
		// add items to the top of the list
		else if (data.isEmpty() || !newData.hasPrevious()) {
			data.replace(newData);
			// add page footer if there is a next page
			if (newData.hasNext()) {
				data.add(null);
			}
			notifyDataSetChanged();
		}
		// add items to the end of the list
		else {
			int end = data.size() - 1;
			// remove page footer if there isn't a next page
			if (!newData.hasNext()) {
				data.remove(end);
				notifyItemRemoved(end);
			}
			data.addAt(newData, end);
			notifyItemRangeInserted(end, newData.size());
		}
	}

	/**
	 * update user information
	 *
	 * @param user User update
	 */
	@MainThread
	public void updateUser(User user) {
		int index = data.indexOf(user);
		if (index >= 0) {
			data.set(index, user);
			notifyItemChanged(index);
		}
	}

	/**
	 * remove user from adapter
	 *
	 * @param name screen name of the user to remove
	 */
	@MainThread
	public void removeUser(String name) {
		int pos = data.removeItem(name);
		if (pos >= 0) {
			notifyItemRemoved(pos);
		}
	}


	@Override
	public int getItemCount() {
		return data.size();
	}


	@Override
	public long getItemId(int index) {
		User user = data.get(index);
		if (user != null)
			return user.getId();
		return NO_ID;
	}


	@Override
	public int getItemViewType(int index) {
		if (data.get(index) == null)
			return ITEM_GAP;
		return ITEM_USER;
	}


	@NonNull
	@Override
	public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		if (viewType == ITEM_USER) {
			final UserHolder vh = new UserHolder(parent, settings);
			vh.itemView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					int position = vh.getLayoutPosition();
					if (position != NO_POSITION) {
						User user = data.get(position);
						if (user != null) {
							listener.onUserClick(user);
						}
					}
				}
			});
			if (userRemovable) {
				vh.delete.setVisibility(VISIBLE);
				vh.delete.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						int position = vh.getLayoutPosition();
						if (position != NO_POSITION) {
							User user = data.get(position);
							if (user != null) {
								listener.onDelete(user.getScreenname());
							}
						}
					}
				});
			} else {
				vh.delete.setVisibility(GONE);
			}
			return vh;
		} else {
			final Footer footer = new Footer(parent, settings, false);
			footer.loadBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					int position = footer.getLayoutPosition();
					if (position != NO_POSITION) {
						boolean actionPerformed = listener.onFooterClick(data.getNext());
						if (actionPerformed) {
							footer.setLoading(true);
							loadingIndex = position;
						}
					}
				}
			});
			return footer;
		}
	}


	@Override
	public void onBindViewHolder(@NonNull ViewHolder holder, int index) {
		if (holder instanceof UserHolder) {
			User user = data.get(index);
			if (user != null) {
				UserHolder userholder = (UserHolder) holder;
				userholder.username.setText(user.getUsername());
				userholder.screenname.setText(user.getScreenname());
				userholder.followingCount.setText(NUM_FORMAT.format(user.getFollowing()));
				userholder.followerCount.setText(NUM_FORMAT.format(user.getFollower()));
				if (user.isVerified()) {
					userholder.verifyIcon.setVisibility(VISIBLE);
				} else {
					userholder.verifyIcon.setVisibility(GONE);
				}
				if (user.isProtected()) {
					userholder.lockedIcon.setVisibility(VISIBLE);
				} else {
					userholder.lockedIcon.setVisibility(GONE);
				}
				if (settings.imagesEnabled() && !user.getImageUrl().isEmpty()) {
					String profileImageUrl = user.getImageUrl();
					if (!user.hasDefaultProfileImage())
						profileImageUrl += settings.getImageSuffix();
					picasso.load(profileImageUrl).transform(new RoundedCornersTransformation(2, 0))
							.error(R.drawable.no_image).into(userholder.profileImg);
				} else {
					userholder.profileImg.setImageResource(0);
				}
			}
		} else if (holder instanceof Footer) {
			Footer footer = (Footer) holder;
			footer.setLoading(loadingIndex != NO_LOADING);
		}
	}

	/**
	 * disable loading animation in footer
	 */
	public void disableLoading() {
		if (loadingIndex != NO_LOADING) {
			int oldIndex = loadingIndex;
			loadingIndex = NO_LOADING;
			notifyItemChanged(oldIndex);
		}
	}

	/**
	 * enables delete button for an user item
	 *
	 * @param enable true to enable delete button
	 */
	public void enableDeleteButton(boolean enable) {
		userRemovable = enable;
	}

	/**
	 * Listener for list click
	 */
	public interface UserClickListener {

		/**
		 * handle list item click
		 *
		 * @param user user item
		 */
		void onUserClick(User user);

		/**
		 * handle footer click
		 *
		 * @param cursor next cursor of the list
		 * @return true if click was handled
		 */
		boolean onFooterClick(long cursor);

		/**
		 * remove user from a list
		 *
		 * @param name screen name of the user
		 */
		void onDelete(String name);
	}
}