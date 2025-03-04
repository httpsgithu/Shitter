package org.nuclearfog.twidda.adapter;

import static org.nuclearfog.twidda.ui.fragments.TweetFragment.KEY_FRAG_TWEET_ID;
import static org.nuclearfog.twidda.ui.fragments.TweetFragment.KEY_FRAG_TWEET_MODE;
import static org.nuclearfog.twidda.ui.fragments.TweetFragment.KEY_FRAG_TWEET_SEARCH;
import static org.nuclearfog.twidda.ui.fragments.TweetFragment.TWEET_FRAG_FAVORS;
import static org.nuclearfog.twidda.ui.fragments.TweetFragment.TWEET_FRAG_HOME;
import static org.nuclearfog.twidda.ui.fragments.TweetFragment.TWEET_FRAG_LIST;
import static org.nuclearfog.twidda.ui.fragments.TweetFragment.TWEET_FRAG_MENT;
import static org.nuclearfog.twidda.ui.fragments.TweetFragment.TWEET_FRAG_SEARCH;
import static org.nuclearfog.twidda.ui.fragments.TweetFragment.TWEET_FRAG_TWEETS;
import static org.nuclearfog.twidda.ui.fragments.UserFragment.KEY_FRAG_DEL_USER;
import static org.nuclearfog.twidda.ui.fragments.UserFragment.KEY_FRAG_USER_ID_ALL;
import static org.nuclearfog.twidda.ui.fragments.UserFragment.KEY_FRAG_USER_MODE;
import static org.nuclearfog.twidda.ui.fragments.UserFragment.KEY_FRAG_USER_SEARCH;
import static org.nuclearfog.twidda.ui.fragments.UserFragment.USER_FRAG_BLOCKED_USERS;
import static org.nuclearfog.twidda.ui.fragments.UserFragment.USER_FRAG_FAVORIT;
import static org.nuclearfog.twidda.ui.fragments.UserFragment.USER_FRAG_FOLLOWER;
import static org.nuclearfog.twidda.ui.fragments.UserFragment.USER_FRAG_FOLLOWING;
import static org.nuclearfog.twidda.ui.fragments.UserFragment.USER_FRAG_FOLLOW_INCOMING;
import static org.nuclearfog.twidda.ui.fragments.UserFragment.USER_FRAG_FOLLOW_OUTGOING;
import static org.nuclearfog.twidda.ui.fragments.UserFragment.USER_FRAG_LIST_MEMBERS;
import static org.nuclearfog.twidda.ui.fragments.UserFragment.USER_FRAG_LIST_SUBSCRIBER;
import static org.nuclearfog.twidda.ui.fragments.UserFragment.USER_FRAG_MUTED_USERS;
import static org.nuclearfog.twidda.ui.fragments.UserFragment.USER_FRAG_RETWEET;
import static org.nuclearfog.twidda.ui.fragments.UserFragment.USER_FRAG_SEARCH;
import static org.nuclearfog.twidda.ui.fragments.UserListFragment.KEY_FRAG_LIST_LIST_TYPE;
import static org.nuclearfog.twidda.ui.fragments.UserListFragment.KEY_FRAG_LIST_OWNER_ID;
import static org.nuclearfog.twidda.ui.fragments.UserListFragment.KEY_FRAG_LIST_OWNER_NAME;
import static org.nuclearfog.twidda.ui.fragments.UserListFragment.LIST_USER_OWNS;
import static org.nuclearfog.twidda.ui.fragments.UserListFragment.LIST_USER_SUBSCR_TO;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import org.nuclearfog.twidda.ui.fragments.ListFragment;
import org.nuclearfog.twidda.ui.fragments.TrendFragment;
import org.nuclearfog.twidda.ui.fragments.TweetFragment;
import org.nuclearfog.twidda.ui.fragments.UserFragment;
import org.nuclearfog.twidda.ui.fragments.UserListFragment;

/**
 * custom adapter used for {@link androidx.viewpager.widget.ViewPager}
 *
 * @author nuclearfog
 */
public class FragmentAdapter extends FragmentStatePagerAdapter {

	private ListFragment[] fragments;

	/**
	 * @param fManager Activity Fragment Manager
	 */
	public FragmentAdapter(FragmentManager fManager) {
		super(fManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
		fragments = new ListFragment[0];
	}


	@Override
	@NonNull
	public Fragment getItem(int index) {
		return fragments[index];
	}


	@Override
	public int getCount() {
		return fragments.length;
	}

	/**
	 * Check if adapter is empty
	 *
	 * @return true if adapter does not contain fragments
	 */
	public boolean isEmpty() {
		return fragments.length == 0;
	}

	/**
	 * Clear all fragments
	 */
	public void clear() {
		fragments = new ListFragment[0];
		notifyDataSetChanged();
	}

	/**
	 * setup adapter for the home activity
	 */
	public void setupForHomePage() {
		Bundle paramHomeTl = new Bundle();
		Bundle paramMention = new Bundle();
		paramHomeTl.putInt(KEY_FRAG_TWEET_MODE, TWEET_FRAG_HOME);
		paramMention.putInt(KEY_FRAG_TWEET_MODE, TWEET_FRAG_MENT);
		fragments = new ListFragment[3];
		fragments[0] = new TweetFragment();
		fragments[1] = new TrendFragment();
		fragments[2] = new TweetFragment();
		fragments[0].setArguments(paramHomeTl);
		fragments[2].setArguments(paramMention);
		notifyDataSetChanged();
	}

	/**
	 * setup adapter for viewing user tweets and favorites
	 *
	 * @param userId ID of the user
	 */
	public void setupProfilePage(long userId) {
		Bundle paramTweet = new Bundle();
		Bundle paramFavorite = new Bundle();
		paramTweet.putLong(KEY_FRAG_TWEET_ID, userId);
		paramFavorite.putLong(KEY_FRAG_TWEET_ID, userId);
		paramTweet.putInt(KEY_FRAG_TWEET_MODE, TWEET_FRAG_TWEETS);
		paramFavorite.putInt(KEY_FRAG_TWEET_MODE, TWEET_FRAG_FAVORS);
		fragments = new ListFragment[2];
		fragments[0] = new TweetFragment();
		fragments[1] = new TweetFragment();
		fragments[0].setArguments(paramTweet);
		fragments[1].setArguments(paramFavorite);
		notifyDataSetChanged();
	}

	/**
	 * setup adapter for search for tweet and user search
	 *
	 * @param search Search string
	 */
	public void setupSearchPage(String search) {
		Bundle paramTweetSearch = new Bundle();
		Bundle paramUserSearch = new Bundle();
		paramTweetSearch.putString(KEY_FRAG_TWEET_SEARCH, search);
		paramUserSearch.putString(KEY_FRAG_USER_SEARCH, search);
		paramTweetSearch.putInt(KEY_FRAG_TWEET_MODE, TWEET_FRAG_SEARCH);
		paramUserSearch.putInt(KEY_FRAG_USER_MODE, USER_FRAG_SEARCH);
		fragments = new ListFragment[2];
		fragments[0] = new TweetFragment();
		fragments[1] = new UserFragment();
		fragments[0].setArguments(paramTweetSearch);
		fragments[1].setArguments(paramUserSearch);
		notifyDataSetChanged();
	}

	/**
	 * setup adapter for a list of user lists created by an user
	 *
	 * @param userId   ID of the user
	 * @param username screen name of the owner
	 */
	public void setupListPage(long userId, String username) {
		Bundle paramUserlistOwnership = new Bundle();
		Bundle paramUserlistSubscription = new Bundle();
		if (userId > 0) {
			paramUserlistOwnership.putLong(KEY_FRAG_LIST_OWNER_ID, userId);
			paramUserlistSubscription.putLong(KEY_FRAG_LIST_OWNER_ID, userId);
		} else {
			paramUserlistOwnership.putString(KEY_FRAG_LIST_OWNER_NAME, username);
			paramUserlistSubscription.putString(KEY_FRAG_LIST_OWNER_NAME, username);
		}
		paramUserlistOwnership.putInt(KEY_FRAG_LIST_LIST_TYPE, LIST_USER_OWNS);
		paramUserlistSubscription.putInt(KEY_FRAG_LIST_LIST_TYPE, LIST_USER_SUBSCR_TO);
		fragments = new ListFragment[2];
		fragments[0] = new UserListFragment();
		fragments[1] = new UserListFragment();
		fragments[0].setArguments(paramUserlistOwnership);
		fragments[1].setArguments(paramUserlistSubscription);
		notifyDataSetChanged();
	}

	/**
	 * setup adapter for a page of tweets and users in an user list
	 *
	 * @param listId      ID of an user list
	 * @param ownerOfList true if current user owns this list
	 */
	public void setupListContentPage(long listId, boolean ownerOfList) {
		Bundle paramUserlistTl = new Bundle();
		Bundle paramUserlistMember = new Bundle();
		Bundle paramUserlistSubscriber = new Bundle();
		paramUserlistTl.putLong(KEY_FRAG_TWEET_ID, listId);
		paramUserlistTl.putInt(KEY_FRAG_TWEET_MODE, TWEET_FRAG_LIST);
		paramUserlistMember.putInt(KEY_FRAG_USER_MODE, USER_FRAG_LIST_MEMBERS);
		paramUserlistMember.putBoolean(KEY_FRAG_DEL_USER, ownerOfList);
		paramUserlistMember.putLong(KEY_FRAG_USER_ID_ALL, listId);
		paramUserlistSubscriber.putLong(KEY_FRAG_USER_ID_ALL, listId);
		paramUserlistSubscriber.putInt(KEY_FRAG_USER_MODE, USER_FRAG_LIST_SUBSCRIBER);
		fragments = new ListFragment[3];
		fragments[0] = new TweetFragment();
		fragments[1] = new UserFragment();
		fragments[2] = new UserFragment();
		fragments[0].setArguments(paramUserlistTl);
		fragments[1].setArguments(paramUserlistMember);
		fragments[2].setArguments(paramUserlistSubscriber);
		notifyDataSetChanged();
	}

	/**
	 * setup adapter for a page of muted / blocked users
	 */
	public void setupMuteBlockPage() {
		Bundle paramMuteList = new Bundle();
		Bundle paramBlockList = new Bundle();
		paramMuteList.putInt(KEY_FRAG_USER_MODE, USER_FRAG_MUTED_USERS);
		paramBlockList.putInt(KEY_FRAG_USER_MODE, USER_FRAG_BLOCKED_USERS);
		fragments = new ListFragment[2];
		fragments[0] = new UserFragment();
		fragments[1] = new UserFragment();
		fragments[0].setArguments(paramMuteList);
		fragments[1].setArguments(paramBlockList);
		notifyDataSetChanged();
	}

	/**
	 * setup adapter to show follow requesting users
	 */
	public void setupFollowRequestPage() {
		Bundle paramFollowing = new Bundle();
		Bundle paramFollower = new Bundle();
		paramFollowing.putInt(KEY_FRAG_USER_MODE, USER_FRAG_FOLLOW_INCOMING);
		paramFollower.putInt(KEY_FRAG_USER_MODE, USER_FRAG_FOLLOW_OUTGOING);
		fragments = new ListFragment[2];
		fragments[0] = new UserFragment();
		fragments[1] = new UserFragment();
		fragments[0].setArguments(paramFollower);
		fragments[1].setArguments(paramFollowing);
		notifyDataSetChanged();
	}

	/**
	 * setup adapter to show "following" of an user
	 *
	 * @param userId ID of the user
	 */
	public void setupFollowingPage(long userId) {
		Bundle paramFollowing = new Bundle();
		paramFollowing.putLong(KEY_FRAG_USER_ID_ALL, userId);
		paramFollowing.putInt(KEY_FRAG_USER_MODE, USER_FRAG_FOLLOWING);
		fragments = new ListFragment[1];
		fragments[0] = new UserFragment();
		fragments[0].setArguments(paramFollowing);
		notifyDataSetChanged();
	}

	/**
	 * setup adapter to show "follower" of an user
	 *
	 * @param userId ID of the user
	 */
	public void setupFollowerPage(long userId) {
		Bundle paramFollower = new Bundle();
		paramFollower.putLong(KEY_FRAG_USER_ID_ALL, userId);
		paramFollower.putInt(KEY_FRAG_USER_MODE, USER_FRAG_FOLLOWER);
		fragments = new ListFragment[1];
		fragments[0] = new UserFragment();
		fragments[0].setArguments(paramFollower);
		notifyDataSetChanged();
	}

	/**
	 * setup adapter to show users retweeting a tweet
	 *
	 * @param tweetId ID of the tweet
	 */
	public void setupRetweeterPage(long tweetId) {
		Bundle paramRetweeter = new Bundle();
		paramRetweeter.putLong(KEY_FRAG_USER_ID_ALL, tweetId);
		paramRetweeter.putInt(KEY_FRAG_USER_MODE, USER_FRAG_RETWEET);
		fragments = new ListFragment[1];
		fragments[0] = new UserFragment();
		fragments[0].setArguments(paramRetweeter);
		notifyDataSetChanged();
	}

	/**
	 * setup adapter to show users liking a tweet
	 *
	 * @param tweetId ID of the tweet
	 */
	public void setFavoriterPage(long tweetId) {
		Bundle paramFavoriter = new Bundle();
		paramFavoriter.putInt(KEY_FRAG_USER_MODE, USER_FRAG_FAVORIT);
		paramFavoriter.putLong(KEY_FRAG_USER_ID_ALL, tweetId);
		fragments = new ListFragment[1];
		fragments[0] = new UserFragment();
		fragments[0].setArguments(paramFavoriter);
		notifyDataSetChanged();
	}

	/**
	 * called when app settings change
	 */
	public void notifySettingsChanged() {
		for (ListFragment fragment : fragments) {
			fragment.reset();
		}
	}

	/**
	 * called to scroll page to top
	 *
	 * @param index tab position of page
	 */
	public void scrollToTop(int index) {
		fragments[index].onTabChange();
	}
}