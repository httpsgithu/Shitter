package org.nuclearfog.twidda.backend.api;

import org.json.JSONObject;
import org.nuclearfog.twidda.model.Relation;

/**
 * implementaton of user relation
 *
 * @author nuclearfog
 */
class RelationV1 implements Relation {

    private boolean isHome;
    private boolean isFollowing;
    private boolean isFollower;
    private boolean isBlocked;
    private boolean isMuted;
    private boolean canDm;

    RelationV1(JSONObject json, long currentId) {
        isHome = json.optLong("id") == currentId;
        isFollowing = json.optBoolean("following");
        isFollower = json.optBoolean("followed_by");
        isBlocked = json.optBoolean("blocking");
        isMuted = json.optBoolean("muting");
        canDm = json.optBoolean("can_dm");
    }

    @Override
    public boolean isHome() {
        return isHome;
    }

    @Override
    public boolean isFollowing() {
        return isFollowing;
    }

    @Override
    public boolean isFollower() {
        return isFollower;
    }

    @Override
    public boolean isBlocked() {
        return isBlocked;
    }

    @Override
    public boolean isMuted() {
        return isMuted;
    }

    @Override
    public boolean canDm() {
        return canDm;
    }
}