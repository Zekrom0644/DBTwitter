package twitter.service;

import twitter.model.Post;
import twitter.model.User;
import twitter.model.Comment;

import java.util.*;

/**
 * 지금은 DB 연동 전이므로,
 * 메모리 안에서 더미 데이터로만 동작하는 서비스 클래스.
 * 나중에 JDBC를 연결할 때 이 부분을 교체하면 된다.
 */
public class TwitterService {

    // 타임라인 더미 데이터
    private final List<Post> timeline = new ArrayList<>();

    // postId -> 댓글 목록
    private final Map<String, List<Comment>> commentStore = new HashMap<>();

    // 팔로워 / 팔로잉 더미 데이터
    private final Map<String, List<User>> followersMap = new HashMap<>();
    private final Map<String, Set<String>> followingSetMap = new HashMap<>();

    public TwitterService() {
        initDummyPosts();
        initDummyFollowers();
        initDummyComments();
    }

    /* --------------------------------------------------
     * 더미 초기화
     * -------------------------------------------------- */
    private void initDummyPosts() {
        // postId는 "P1", "P2", ... 형식
        for (int i = 1; i <= 10; i++) {
            String postId = "P" + i;
            Post p = new Post("@", "Dummy post #" + i
                    + " - Welcome to Twitter GUI! This is a sample timeline item for scrolling test.");
            p.setPostId(postId);
            timeline.add(p);
            commentStore.put(postId, new ArrayList<>());
        }
    }

    private void initDummyFollowers() {
        // userId 기준 더미 팔로워
        List<User> list = new ArrayList<>();
        list.add(new User("alice"));
        list.add(new User("bob"));
        list.add(new User("charlie"));

        followersMap.put("@", list);   // 테스트용으로 로그인 유저를 "@" 라고 가정
        followingSetMap.put("@", new HashSet<>(Arrays.asList("alice")));
    }

    private void initDummyComments() {
        // 첫 번째 포스트에만 예시 댓글 2개
        String firstPostId = "P1";
        List<Comment> cl = commentStore.get(firstPostId);
        if (cl == null) {
            cl = new ArrayList<>();
            commentStore.put(firstPostId, cl);
        }
        cl.add(new Comment("alice", "Nice post!"));
        cl.add(new Comment("bob", "I agree."));
    }

    /* --------------------------------------------------
     * 계정 관련 (현재는 모두 더미)
     * -------------------------------------------------- */
    public boolean login(String id, String pwd) {
        // TODO: JDBC 연결 후 DB에서 검증
        return true;
    }

    public boolean signup(String id, String pwd) {
        // TODO: DB에 INSERT
        return true;
    }

    /* --------------------------------------------------
     * 타임라인 / 포스트
     * -------------------------------------------------- */
    public boolean writePost(String id, String content) {
        // 나중에 DB에 INSERT.
        String postId = "P" + (timeline.size() + 1);
        Post p = new Post(id, content);
        p.setPostId(postId);
        timeline.add(0, p); // 새 글을 앞에 추가
        commentStore.put(postId, new ArrayList<>());
        return true;
    }

    /** 로그인한 사용자의 타임라인(현재는 전부 동일) */
    public List<Post> getTimeline(String id) {
        // 실사용에서는 id에 따라 다른 timeline을 가져오면 됨
        return new ArrayList<>(timeline);
    }

    /* --------------------------------------------------
     * 팔로워 / 팔로잉 (더미)
     * -------------------------------------------------- */
    public List<User> getFollowers(String id) {
        return new ArrayList<>(followersMap.getOrDefault(id, Collections.emptyList()));
    }

    public boolean isFollowing(String me, String target) {
        return followingSetMap
                .getOrDefault(me, Collections.emptySet())
                .contains(target);
    }

    public void follow(String me, String target) {
        followingSetMap.computeIfAbsent(me, k -> new HashSet<>()).add(target);
    }

    public void unfollow(String me, String target) {
        followingSetMap.computeIfAbsent(me, k -> new HashSet<>()).remove(target);
    }

    /* --------------------------------------------------
     * 댓글 (여기가 핵심)
     * -------------------------------------------------- */

    /** postId에 해당하는 댓글 목록 반환 */
    public List<Comment> getComments(String postId) {
        return new ArrayList<>(commentStore.getOrDefault(postId, Collections.emptyList()));
    }

    /**
     * 댓글 추가.
     * @param postId   어느 포스트에 다는지
     * @param writerId 댓글 작성자
     * @param content  내용
     */
    public void addComment(String postId, String writerId, String content) {
        List<Comment> list = commentStore.computeIfAbsent(postId, k -> new ArrayList<>());
        list.add(new Comment(writerId, content));
    }
}
