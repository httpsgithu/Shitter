package org.nuclearfog.twidda.ui.activities;

import static android.os.AsyncTask.Status.RUNNING;
import static android.view.View.GONE;
import static android.view.View.OnClickListener;
import static android.view.View.OnLongClickListener;
import static android.view.View.VISIBLE;
import static android.widget.Toast.LENGTH_SHORT;
import static org.nuclearfog.twidda.ui.activities.SearchActivity.KEY_SEARCH_QUERY;
import static org.nuclearfog.twidda.ui.activities.TweetEditor.KEY_TWEETPOPUP_REPLYID;
import static org.nuclearfog.twidda.ui.activities.TweetEditor.KEY_TWEETPOPUP_TEXT;
import static org.nuclearfog.twidda.ui.activities.UsersActivity.KEY_USERDETAIL_ID;
import static org.nuclearfog.twidda.ui.activities.UsersActivity.KEY_USERDETAIL_MODE;
import static org.nuclearfog.twidda.ui.activities.UsersActivity.USERLIST_FAVORIT;
import static org.nuclearfog.twidda.ui.activities.UsersActivity.USERLIST_RETWEETS;
import static org.nuclearfog.twidda.ui.fragments.TweetFragment.KEY_FRAG_TWEET_ID;
import static org.nuclearfog.twidda.ui.fragments.TweetFragment.KEY_FRAG_TWEET_MODE;
import static org.nuclearfog.twidda.ui.fragments.TweetFragment.KEY_FRAG_TWEET_SEARCH;
import static org.nuclearfog.twidda.ui.fragments.TweetFragment.TWEET_FRAG_ANSWER;

import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Spannable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;

import com.squareup.picasso.Picasso;

import org.nuclearfog.tag.Tagger;
import org.nuclearfog.tag.Tagger.OnTagClickListener;
import org.nuclearfog.textviewtool.LinkAndScrollMovement;
import org.nuclearfog.twidda.R;
import org.nuclearfog.twidda.backend.api.TwitterException;
import org.nuclearfog.twidda.backend.async.TweetAction;
import org.nuclearfog.twidda.backend.utils.AppStyles;
import org.nuclearfog.twidda.backend.utils.ErrorHandler;
import org.nuclearfog.twidda.backend.utils.PicassoBuilder;
import org.nuclearfog.twidda.database.GlobalSettings;
import org.nuclearfog.twidda.model.Tweet;
import org.nuclearfog.twidda.model.User;
import org.nuclearfog.twidda.ui.dialogs.ConfirmDialog;
import org.nuclearfog.twidda.ui.dialogs.ConfirmDialog.OnConfirmListener;
import org.nuclearfog.twidda.ui.dialogs.LinkDialog;
import org.nuclearfog.twidda.ui.fragments.TweetFragment;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

/**
 * Tweet Activity for tweet and user information
 *
 * @author nuclearfog
 */
public class TweetActivity extends AppCompatActivity implements OnClickListener,
		OnLongClickListener, OnTagClickListener, OnConfirmListener {

	/**
	 * Activity result code to update existing tweet information
	 */
	public static final int RETURN_TWEET_UPDATE = 0x789CD38B;

	/**
	 * Activity result code if a tweet was not found or removed
	 */
	public static final int RETURN_TWEET_REMOVED = 0x8B03DB84;

	/**
	 * key used for tweet information
	 * value type is {@link Tweet}
	 * If no tweet object exists, {@link #KEY_TWEET_ID} and {@link #KEY_TWEET_NAME} will be used instead
	 */
	public static final String KEY_TWEET_DATA = "tweet_data";

	/**
	 * key for the Tweet ID value, alternative to {@link #KEY_TWEET_DATA}
	 * value type is Long
	 */
	public static final String KEY_TWEET_ID = "tweet_tweet_id";

	/**
	 * key for the tweet author's name. alternative to {@link #KEY_TWEET_DATA}
	 * value type is String
	 */
	public static final String KEY_TWEET_NAME = "tweet_author";

	/**
	 * key to return updated tweet information
	 * value type is {@link Tweet}
	 */
	public static final String INTENT_TWEET_UPDATE_DATA = "tweet_update_data";

	/**
	 * key to return a tweet ID if this tweet was deleted
	 * value type is Long
	 */
	public static final String INTENT_TWEET_REMOVED_ID = "tweet_removed_id";

	/**
	 * regex pattern of a tweet URL
	 */
	public static final Pattern LINK_PATTERN = Pattern.compile("https://twitter.com/\\w+/status/\\d+");

	private static final int MENU_GROUP_COPY = 0x157426;

	@Nullable
	private ClipboardManager clip;
	private GlobalSettings settings;
	private TweetAction statusAsync;
	private Picasso picasso;

	private LinkDialog linkPreview;
	private ConfirmDialog confirmDialog;

	private TextView tweet_api, tweetDate, tweetText, scrName, usrName, tweetLocName, sensitive_media;
	private Button ansButton, rtwButton, favButton, replyName, tweetLocGPS, retweeter;
	private ImageView profile_img, mediaButton;
	private Toolbar toolbar;

	@Nullable
	private Tweet tweet;
	private boolean hidden;


	@Override
	protected void attachBaseContext(Context newBase) {
		super.attachBaseContext(AppStyles.setFontScale(newBase));
	}


	@Override
	protected void onCreate(@Nullable Bundle b) {
		super.onCreate(b);
		setContentView(R.layout.page_tweet);
		ViewGroup root = findViewById(R.id.tweet_layout);
		toolbar = findViewById(R.id.tweet_toolbar);
		ansButton = findViewById(R.id.tweet_answer);
		rtwButton = findViewById(R.id.tweet_retweet);
		favButton = findViewById(R.id.tweet_favorite);
		usrName = findViewById(R.id.tweet_username);
		scrName = findViewById(R.id.tweet_screenname);
		profile_img = findViewById(R.id.tweet_profile);
		replyName = findViewById(R.id.tweet_answer_reference);
		tweetText = findViewById(R.id.tweet_detailed);
		tweetDate = findViewById(R.id.tweet_date);
		tweet_api = findViewById(R.id.tweet_api);
		tweetLocName = findViewById(R.id.tweet_location_name);
		tweetLocGPS = findViewById(R.id.tweet_location_coordinate);
		mediaButton = findViewById(R.id.tweet_media_attach);
		sensitive_media = findViewById(R.id.tweet_sensitive);
		retweeter = findViewById(R.id.tweet_retweeter_reference);

		// get parameter
		Object data = getIntent().getSerializableExtra(KEY_TWEET_DATA);
		long tweetId;
		String username;
		if (data instanceof Tweet) {
			tweet = (Tweet) data;
			Tweet embedded = tweet.getEmbeddedTweet();
			if (embedded != null) {
				username = embedded.getAuthor().getScreenname();
				tweetId = embedded.getId();
			} else {
				username = tweet.getAuthor().getScreenname();
				tweetId = tweet.getId();
				hidden = tweet.isHidden();
			}
		} else {
			username = getIntent().getStringExtra(KEY_TWEET_NAME);
			tweetId = getIntent().getLongExtra(KEY_TWEET_ID, -1);
		}

		// create list fragment for tweet replies
		Bundle param = new Bundle();
		param.putInt(KEY_FRAG_TWEET_MODE, TWEET_FRAG_ANSWER);
		param.putString(KEY_FRAG_TWEET_SEARCH, username);
		param.putLong(KEY_FRAG_TWEET_ID, tweetId);

		// insert fragment into view
		FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
		fragmentTransaction.replace(R.id.tweet_reply_fragment, TweetFragment.class, param);
		fragmentTransaction.commit();

		clip = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);

		settings = GlobalSettings.getInstance(this);
		ansButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.answer, 0, 0, 0);
		rtwButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.retweet, 0, 0, 0);
		tweetLocGPS.setCompoundDrawablesWithIntrinsicBounds(R.drawable.location, 0, 0, 0);
		sensitive_media.setCompoundDrawablesWithIntrinsicBounds(R.drawable.sensitive, 0, 0, 0);
		replyName.setCompoundDrawablesWithIntrinsicBounds(R.drawable.back, 0, 0, 0);
		retweeter.setCompoundDrawablesWithIntrinsicBounds(R.drawable.retweet, 0, 0, 0);
		tweetText.setMovementMethod(LinkAndScrollMovement.getInstance());
		tweetText.setLinkTextColor(settings.getHighlightColor());
		if (settings.likeEnabled()) {
			favButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.like, 0, 0, 0);
		} else {
			favButton.setCompoundDrawablesWithIntrinsicBounds(R.drawable.favorite, 0, 0, 0);
		}
		toolbar.setTitle("");
		setSupportActionBar(toolbar);
		AppStyles.setTheme(root, settings.getBackgroundColor());
		picasso = PicassoBuilder.get(this);

		linkPreview = new LinkDialog(this);
		confirmDialog = new ConfirmDialog(this);

		confirmDialog.setConfirmListener(this);
		retweeter.setOnClickListener(this);
		replyName.setOnClickListener(this);
		ansButton.setOnClickListener(this);
		rtwButton.setOnClickListener(this);
		favButton.setOnClickListener(this);
		profile_img.setOnClickListener(this);
		tweetLocGPS.setOnClickListener(this);
		mediaButton.setOnClickListener(this);
		rtwButton.setOnLongClickListener(this);
		favButton.setOnLongClickListener(this);
		retweeter.setOnLongClickListener(this);
		tweetLocGPS.setOnLongClickListener(this);
	}


	@Override
	protected void onStart() {
		super.onStart();
		if (statusAsync == null) {
			// print Tweet object and get and update it
			if (tweet != null) {
				statusAsync = new TweetAction(this, TweetAction.LOAD);
				statusAsync.execute(tweet.getId());
				setTweet(tweet);
			}
			// Load Tweet from database first if no tweet is defined
			else {
				long tweetId = getIntent().getLongExtra(KEY_TWEET_ID, -1);
				statusAsync = new TweetAction(this, TweetAction.LD_DB);
				statusAsync.execute(tweetId);
			}
		}
	}


	@Override
	protected void onDestroy() {
		if (statusAsync != null && statusAsync.getStatus() == RUNNING)
			statusAsync.cancel(true);
		linkPreview.cancel();
		super.onDestroy();
	}


	@Override
	public void onBackPressed() {
		Intent returnData = new Intent();
		returnData.putExtra(INTENT_TWEET_UPDATE_DATA, tweet);
		setResult(RETURN_TWEET_UPDATE, returnData);
		super.onBackPressed();
	}


	@Override
	public boolean onCreateOptionsMenu(@NonNull Menu m) {
		getMenuInflater().inflate(R.menu.tweet, m);
		AppStyles.setOverflowIcon(toolbar, settings.getIconColor());
		return super.onCreateOptionsMenu(m);
	}


	@Override
	public boolean onPrepareOptionsMenu(@NonNull Menu m) {
		if (tweet == null)
			return super.onPrepareOptionsMenu(m);

		MenuItem optDelete = m.findItem(R.id.menu_tweet_delete);
		MenuItem optHide = m.findItem(R.id.menu_tweet_hide);
		MenuItem optCopy = m.findItem(R.id.menu_tweet_copy);
		MenuItem optMetrics = m.findItem(R.id.menu_tweet_metrics);
		SubMenu copyMenu = optCopy.getSubMenu();

		Tweet currentTweet = tweet;
		if (tweet.getEmbeddedTweet() != null) {
			currentTweet = tweet.getEmbeddedTweet();
		}
		if (currentTweet.getRepliedUserId() == settings.getCurrentUserId()
				&& currentTweet.getAuthor().getId() != settings.getCurrentUserId()) {
			optHide.setVisible(true);
			if (hidden) {
				optHide.setTitle(R.string.menu_tweet_unhide);
			} else {
				optHide.setTitle(R.string.menu_tweet_hide);
			}
		}
		if (currentTweet.getAuthor().isCurrentUser()) {
			optDelete.setVisible(true);
			if (new Date().getTime() - currentTweet.getTimestamp() < 2419200000L) {
				optMetrics.setVisible(true);
			}
		}
		// add media link items
		// check if menu doesn't contain media links already
		if (copyMenu.size() == 2) {
			int mediaCount = currentTweet.getMediaUris().length;
			for (int i = 0; i < mediaCount; i++) {
				// create sub menu entry and use array index as item ID
				String text = getString(R.string.menu_media_link) + ' ' + (i + 1);
				copyMenu.add(MENU_GROUP_COPY, i, Menu.NONE, text);
			}
		}
		return true;
	}


	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item) {
		if (tweet == null)
			return super.onOptionsItemSelected(item);

		Tweet clickedTweet = tweet;
		if (tweet.getEmbeddedTweet() != null)
			clickedTweet = tweet.getEmbeddedTweet();
		User author = clickedTweet.getAuthor();
		// Delete tweet option
		if (item.getItemId() == R.id.menu_tweet_delete) {
			confirmDialog.show(ConfirmDialog.TWEET_DELETE);
		}
		// hide tweet
		else if (item.getItemId() == R.id.menu_tweet_hide) {
			if (hidden) {
				statusAsync = new TweetAction(this, TweetAction.UNHIDE);
			} else {
				statusAsync = new TweetAction(this, TweetAction.HIDE);
			}
			statusAsync.execute(tweet.getId());
		}
		// get tweet link
		else if (item.getItemId() == R.id.menu_tweet_browser) {
			String username = author.getScreenname().substring(1);
			String tweetLink = settings.getTwitterHostname() + username + "/status/" + clickedTweet.getId();
			Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(tweetLink));
			try {
				startActivity(intent);
			} catch (ActivityNotFoundException err) {
				Toast.makeText(this, R.string.error_connection_failed, LENGTH_SHORT).show();
			}
		}
		// copy tweet link to clipboard
		else if (item.getItemId() == R.id.menu_tweet_copy_text) {
			if (clip != null) {
				ClipData linkClip = ClipData.newPlainText("tweet text", clickedTweet.getText());
				clip.setPrimaryClip(linkClip);
				Toast.makeText(this, R.string.info_tweet_text_copied, LENGTH_SHORT).show();
			}
		}
		// copy tweet link to clipboard
		else if (item.getItemId() == R.id.menu_tweet_copy_tweetlink) {
			String username = author.getScreenname().substring(1);
			String tweetLink = settings.getTwitterHostname() + username + "/status/" + clickedTweet.getId();
			if (clip != null) {
				ClipData linkClip = ClipData.newPlainText("tweet link", tweetLink);
				clip.setPrimaryClip(linkClip);
				Toast.makeText(this, R.string.info_tweet_link_copied, LENGTH_SHORT).show();
			}
		}
		// open tweet metrics page
		else if (item.getItemId() == R.id.menu_tweet_metrics) {
			Intent metricsIntent = new Intent(getApplicationContext(), MetricsActivity.class);
			metricsIntent.putExtra(MetricsActivity.KEY_METRICS_TWEET, clickedTweet);
			startActivity(metricsIntent);
		}
		// copy media links
		else if (item.getGroupId() == MENU_GROUP_COPY) {
			int index = item.getItemId();
			Uri[] mediaLinks = clickedTweet.getMediaUris();
			if (index >= 0 && index < mediaLinks.length) {
				if (clip != null) {
					ClipData linkClip = ClipData.newPlainText("tweet media link", mediaLinks[index].toString());
					clip.setPrimaryClip(linkClip);
					Toast.makeText(this, R.string.info_tweet_medialink_copied, LENGTH_SHORT).show();
				}
			}
		}
		return true;
	}


	@Override
	public void onClick(View v) {
		if (tweet != null) {
			Tweet clickedTweet = tweet;
			if (tweet.getEmbeddedTweet() != null)
				clickedTweet = tweet.getEmbeddedTweet();
			// answer to the tweet
			if (v.getId() == R.id.tweet_answer) {
				String tweetPrefix = clickedTweet.getUserMentions();
				Intent tweetPopup = new Intent(this, TweetEditor.class);
				tweetPopup.putExtra(KEY_TWEETPOPUP_REPLYID, clickedTweet.getId());
				if (!tweetPrefix.isEmpty())
					tweetPopup.putExtra(KEY_TWEETPOPUP_TEXT, tweetPrefix);
				startActivity(tweetPopup);
			}
			// show user retweeting this tweet
			else if (v.getId() == R.id.tweet_retweet) {
				Intent userList = new Intent(this, UsersActivity.class);
				userList.putExtra(KEY_USERDETAIL_ID, clickedTweet.getId());
				userList.putExtra(KEY_USERDETAIL_MODE, USERLIST_RETWEETS);
				startActivity(userList);
			}
			// show user favoriting this tweet
			else if (v.getId() == R.id.tweet_favorite) {
				Intent userList = new Intent(this, UsersActivity.class);
				userList.putExtra(KEY_USERDETAIL_ID, clickedTweet.getId());
				userList.putExtra(KEY_USERDETAIL_MODE, USERLIST_FAVORIT);
				startActivity(userList);
			}
			// open profile of the tweet author
			else if (v.getId() == R.id.tweet_profile) {
				Intent profile = new Intent(getApplicationContext(), ProfileActivity.class);
				profile.putExtra(ProfileActivity.KEY_PROFILE_DATA, clickedTweet.getAuthor());
				startActivity(profile);
			}
			// open replied tweet
			else if (v.getId() == R.id.tweet_answer_reference) {
				Intent answerIntent = new Intent(getApplicationContext(), TweetActivity.class);
				answerIntent.putExtra(KEY_TWEET_ID, clickedTweet.getRepliedTweetId());
				answerIntent.putExtra(KEY_TWEET_NAME, clickedTweet.getReplyName());
				startActivity(answerIntent);
			}
			// open tweet location coordinates
			else if (v.getId() == R.id.tweet_location_coordinate) {
				Intent locationIntent = new Intent(Intent.ACTION_VIEW);
				locationIntent.setData(Uri.parse("geo:" + clickedTweet.getLocationCoordinates() + "?z=14"));
				try {
					startActivity(locationIntent);
				} catch (ActivityNotFoundException err) {
					Toast.makeText(getApplicationContext(), R.string.error_no_card_app, LENGTH_SHORT).show();
				}
			}
			// open tweet media
			else if (v.getId() == R.id.tweet_media_attach) {
				// open embedded image links
				if (clickedTweet.getMediaType() == Tweet.MEDIA_PHOTO) {
					Intent mediaIntent = new Intent(this, ImageViewer.class);
					mediaIntent.putExtra(ImageViewer.IMAGE_URIS, clickedTweet.getMediaUris());
					mediaIntent.putExtra(ImageViewer.IMAGE_DOWNLOAD, true);
					startActivity(mediaIntent);
				}
				// open embedded video link
				else if (clickedTweet.getMediaType() == Tweet.MEDIA_VIDEO) {
					if (!settings.isProxyEnabled() || (settings.isProxyEnabled() && settings.ignoreProxyWarning())) {
						Uri link = clickedTweet.getMediaUris()[0];
						Intent mediaIntent = new Intent(this, VideoViewer.class);
						mediaIntent.putExtra(VideoViewer.VIDEO_URI, link);
						mediaIntent.putExtra(VideoViewer.ENABLE_VIDEO_CONTROLS, true);
						startActivity(mediaIntent);
					} else {
						confirmDialog.show(ConfirmDialog.PROXY_CONFIRM);
					}
				}
				// open embedded gif link
				else if (clickedTweet.getMediaType() == Tweet.MEDIA_GIF) {
					Uri link = clickedTweet.getMediaUris()[0];
					Intent mediaIntent = new Intent(this, VideoViewer.class);
					mediaIntent.putExtra(VideoViewer.VIDEO_URI, link);
					mediaIntent.putExtra(VideoViewer.ENABLE_VIDEO_CONTROLS, false);
					startActivity(mediaIntent);
				}
			}
			// go to user retweeting this tweet
			else if (v.getId() == R.id.tweet_retweeter_reference) {
				Intent profile = new Intent(getApplicationContext(), ProfileActivity.class);
				profile.putExtra(ProfileActivity.KEY_PROFILE_DATA, tweet.getAuthor());
				startActivity(profile);
			}
		}
	}


	@Override
	public boolean onLongClick(View v) {
		if (tweet != null && (statusAsync == null || statusAsync.getStatus() != RUNNING)) {
			// retweet this tweet
			if (v.getId() == R.id.tweet_retweet) {
				if (tweet.isRetweeted()) {
					statusAsync = new TweetAction(this, TweetAction.UNRETWEET);
				} else {
					statusAsync = new TweetAction(this, TweetAction.RETWEET);
				}

				if (tweet.getEmbeddedTweet() != null)
					statusAsync.execute(tweet.getId(), tweet.getEmbeddedTweet().getRetweetId());
				else
					statusAsync.execute(tweet.getId());
				Toast.makeText(this, R.string.info_loading, LENGTH_SHORT).show();
				return true;
			}
			// favorite the tweet
			else if (v.getId() == R.id.tweet_favorite) {
				if (tweet.isFavorited()) {
					statusAsync = new TweetAction(this, TweetAction.UNFAVORITE);
				} else {
					statusAsync = new TweetAction(this, TweetAction.FAVORITE);
				}
				statusAsync.execute(tweet.getId());
				Toast.makeText(this, R.string.info_loading, LENGTH_SHORT).show();
				return true;
			}
			// go to original tweet
			else if (v.getId() == R.id.tweet_retweeter_reference) {
				Tweet embeddedTweet = tweet.getEmbeddedTweet();
				if (embeddedTweet != null) {
					Intent tweetIntent = new Intent(this, TweetActivity.class);
					tweetIntent.putExtra(KEY_TWEET_DATA, embeddedTweet);
					startActivity(tweetIntent);
				}
			}
			// copy tweet coordinates
			else if (v.getId() == R.id.tweet_location_coordinate) {
				if (clip != null) {
					ClipData linkClip;
					Tweet embeddedTweet = tweet.getEmbeddedTweet();
					if (embeddedTweet != null)
						linkClip = ClipData.newPlainText("Tweet location coordinates", embeddedTweet.getLocationCoordinates());
					else
						linkClip = ClipData.newPlainText("Tweet location coordinates", tweet.getLocationCoordinates());
					clip.setPrimaryClip(linkClip);
					Toast.makeText(this, R.string.info_tweet_location_copied, LENGTH_SHORT).show();
				}
			}
		}
		return false;
	}


	@Override
	public void onConfirm(int type, boolean rememberChoice) {
		if (tweet != null) {
			Tweet clickedTweet = tweet;
			if (tweet.getEmbeddedTweet() != null) {
				clickedTweet = tweet.getEmbeddedTweet();
			}
			if (type == ConfirmDialog.TWEET_DELETE) {
				statusAsync = new TweetAction(this, TweetAction.DELETE);
				statusAsync.execute(clickedTweet.getId(), clickedTweet.getRetweetId());
			} else if (type == ConfirmDialog.PROXY_CONFIRM) {
				settings.setIgnoreProxyWarning(rememberChoice);

				Uri link = clickedTweet.getMediaUris()[0];
				Intent mediaIntent = new Intent(this, VideoViewer.class);
				mediaIntent.putExtra(VideoViewer.VIDEO_URI, link);
				mediaIntent.putExtra(VideoViewer.ENABLE_VIDEO_CONTROLS, true);
				startActivity(mediaIntent);
			}
		}
	}


	@Override
	public void onTagClick(String tag) {
		Intent intent = new Intent(this, SearchActivity.class);
		intent.putExtra(KEY_SEARCH_QUERY, tag);
		startActivity(intent);
	}

	/**
	 * called when a link is clicked
	 *
	 * @param tag link string
	 */
	@Override
	public void onLinkClick(String tag) {
		Uri link = Uri.parse(tag);
		// check if the link points to another tweet
		if (LINK_PATTERN.matcher(link.getScheme() + "://" + link.getHost() + link.getPath()).matches()) {
			List<String> segments = link.getPathSegments();
			Intent intent = new Intent(this, TweetActivity.class);
			intent.putExtra(KEY_TWEET_ID, Long.parseLong(segments.get(2)));
			intent.putExtra(KEY_TWEET_NAME, segments.get(0));
			startActivity(intent);
		}
		// open link in browser or preview
		else {
			if (settings.linkPreviewEnabled()) {
				linkPreview.show(tag);
			} else {
				// open link in a browser
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setData(link);
				try {
					startActivity(intent);
				} catch (ActivityNotFoundException err) {
					Toast.makeText(this, R.string.error_connection_failed, LENGTH_SHORT).show();
				}
			}
		}
	}


	/**
	 * load tweet into UI
	 *
	 * @param tweetUpdate Tweet information
	 */
	public void setTweet(Tweet tweetUpdate) {
		tweet = tweetUpdate;
		if (tweetUpdate.getEmbeddedTweet() != null) {
			tweetUpdate = tweetUpdate.getEmbeddedTweet();
			retweeter.setText(tweet.getAuthor().getScreenname());
			retweeter.setVisibility(VISIBLE);
		} else {
			retweeter.setVisibility(GONE);
		}
		User author = tweetUpdate.getAuthor();
		invalidateOptionsMenu();

		NumberFormat buttonNumber = NumberFormat.getIntegerInstance();
		if (tweetUpdate.isRetweeted()) {
			AppStyles.setDrawableColor(rtwButton, settings.getRetweetIconColor());
		} else {
			AppStyles.setDrawableColor(rtwButton, settings.getIconColor());
		}
		if (tweetUpdate.isFavorited()) {
			AppStyles.setDrawableColor(favButton, settings.getFavoriteIconColor());
		} else {
			AppStyles.setDrawableColor(favButton, settings.getIconColor());
		}
		if (author.isVerified()) {
			usrName.setCompoundDrawablesWithIntrinsicBounds(R.drawable.verify, 0, 0, 0);
			AppStyles.setDrawableColor(usrName, settings.getIconColor());
		} else {
			usrName.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
		}
		if (author.isProtected()) {
			scrName.setCompoundDrawablesWithIntrinsicBounds(R.drawable.lock, 0, 0, 0);
			AppStyles.setDrawableColor(scrName, settings.getIconColor());
		} else {
			scrName.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
		}
		usrName.setText(author.getUsername());
		scrName.setText(author.getScreenname());
		tweetDate.setText(SimpleDateFormat.getDateTimeInstance().format(tweetUpdate.getTimestamp()));
		favButton.setText(buttonNumber.format(tweetUpdate.getFavoriteCount()));
		rtwButton.setText(buttonNumber.format(tweetUpdate.getRetweetCount()));
		tweet_api.setText(R.string.tweet_sent_from);
		tweet_api.append(tweetUpdate.getSource());

		if (!tweetUpdate.getText().isEmpty()) {
			Spannable sTweet = Tagger.makeTextWithLinks(tweetUpdate.getText(), settings.getHighlightColor(), this);
			tweetText.setVisibility(VISIBLE);
			tweetText.setText(sTweet);
		} else {
			tweetText.setVisibility(GONE);
		}
		if (tweetUpdate.getRepliedTweetId() > 0) {
			replyName.setText(tweetUpdate.getReplyName());
			replyName.setVisibility(VISIBLE);
		} else {
			replyName.setVisibility(GONE);
		}
		if (tweetUpdate.isSensitive()) {
			sensitive_media.setVisibility(VISIBLE);
		} else {
			sensitive_media.setVisibility(GONE);
		}
		switch (tweetUpdate.getMediaType()) {
			case Tweet.MEDIA_PHOTO:
				mediaButton.setVisibility(VISIBLE);
				mediaButton.setImageResource(R.drawable.image);
				break;

			case Tweet.MEDIA_VIDEO:
				mediaButton.setVisibility(VISIBLE);
				mediaButton.setImageResource(R.drawable.video);
				break;

			case Tweet.MEDIA_GIF:
				mediaButton.setVisibility(VISIBLE);
				mediaButton.setImageResource(R.drawable.gif);
				break;

			default:
				mediaButton.setVisibility(GONE);
				mediaButton.setImageResource(0);
				break;
		}
		AppStyles.setDrawableColor(mediaButton, settings.getIconColor());
		if (settings.imagesEnabled() && !author.getImageUrl().isEmpty()) {
			String profileImageUrl = author.getImageUrl();
			if (!author.hasDefaultProfileImage())
				profileImageUrl += settings.getImageSuffix();
			picasso.load(profileImageUrl).transform(new RoundedCornersTransformation(4, 0))
					.error(R.drawable.no_image).into(profile_img);
		} else {
			profile_img.setImageResource(0);
		}
		String placeName = tweetUpdate.getLocationName();
		if (placeName != null && !placeName.isEmpty()) {
			tweetLocName.setVisibility(VISIBLE);
			tweetLocName.setText(placeName);
		} else {
			tweetLocName.setVisibility(GONE);
		}
		String location = tweetUpdate.getLocationCoordinates();
		if (!location.isEmpty()) {
			tweetLocGPS.setVisibility(VISIBLE);
			tweetLocGPS.setText(location);
		} else {
			tweetLocGPS.setVisibility(GONE);
		}
		if (rtwButton.getVisibility() != VISIBLE) {
			rtwButton.setVisibility(VISIBLE);
			favButton.setVisibility(VISIBLE);
			ansButton.setVisibility(VISIBLE);
		}
	}

	/**
	 * called after a tweet action
	 *
	 * @param action action type
	 */
	public void OnSuccess(int action) {
		switch (action) {
			case TweetAction.RETWEET:
				Toast.makeText(this, R.string.info_tweet_retweeted, LENGTH_SHORT).show();
				break;

			case TweetAction.UNRETWEET:
				Toast.makeText(this, R.string.info_tweet_unretweeted, LENGTH_SHORT).show();
				// todo remove old retweet from list fragment
				break;

			case TweetAction.FAVORITE:
				if (settings.likeEnabled())
					Toast.makeText(this, R.string.info_tweet_liked, LENGTH_SHORT).show();
				else
					Toast.makeText(this, R.string.info_tweet_favored, LENGTH_SHORT).show();
				break;

			case TweetAction.UNFAVORITE:
				if (settings.likeEnabled())
					Toast.makeText(this, R.string.info_tweet_unliked, LENGTH_SHORT).show();
				else
					Toast.makeText(this, R.string.info_tweet_unfavored, LENGTH_SHORT).show();
				break;

			case TweetAction.HIDE:
				hidden = true;
				invalidateOptionsMenu();
				Toast.makeText(this, R.string.info_reply_hidden, LENGTH_SHORT).show();
				break;

			case TweetAction.UNHIDE:
				hidden = false;
				invalidateOptionsMenu();
				Toast.makeText(this, R.string.info_reply_unhidden, LENGTH_SHORT).show();
				break;

			case TweetAction.DELETE:
				if (tweet != null) {
					Toast.makeText(this, R.string.info_tweet_removed, LENGTH_SHORT).show();
					Intent returnData = new Intent();
					if (tweet.getEmbeddedTweet() != null)
						returnData.putExtra(INTENT_TWEET_REMOVED_ID, tweet.getEmbeddedTweet().getId());
					else
						returnData.putExtra(INTENT_TWEET_REMOVED_ID, tweet.getId());
					setResult(RETURN_TWEET_REMOVED, returnData);
					finish();
				}
				break;
		}
	}

	/**
	 * called when an error occurs
	 *
	 * @param error Error information
	 */
	public void onError(@Nullable TwitterException error) {
		ErrorHandler.handleFailure(this, error);
		if (tweet == null) {
			finish();
		} else {
			if (error != null && error.getErrorType() == ErrorHandler.TwitterError.RESOURCE_NOT_FOUND) {
				// Mark tweet as removed, so it can be removed from the list
				Intent returnData = new Intent();
				returnData.putExtra(INTENT_TWEET_REMOVED_ID, tweet.getId());
				setResult(RETURN_TWEET_REMOVED, returnData);
				finish();
			}
		}
	}
}