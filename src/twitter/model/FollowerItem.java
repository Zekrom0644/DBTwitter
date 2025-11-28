package twitter.model;

public class FollowerItem {
    private String userId;
    private boolean isFollowing;

    public FollowerItem(String userId, boolean isFollowing) {
        this.userId = userId;
        this.isFollowing = isFollowing;
    }

    public String getUserId() {
        return userId;
    }

    public boolean isFollowing() {
        return isFollowing;
    }

    public void setFollowing(boolean following) {
        isFollowing = following;
    }

    @Override
    public String toString() {
        return userId;
    }
}
