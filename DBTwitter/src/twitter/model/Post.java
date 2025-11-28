package twitter.model;

public class Post {

    private String postId;      // 게시글 ID (DB 연동 대비)
    private String writerId;    // 작성자 ID
    private String content;     // 글 내용
    private int numLikes;       // 좋아요 수
    private int numDislikes;    // 싫어요 수

    // 생성자 1: 최소 정보만 (테스트용)
    public Post(String writerId, String content) {
        this.writerId = writerId;
        this.content = content;
        this.numLikes = 0;
        this.numDislikes = 0;
    }

    // 생성자 2: 모든 값 지정 (DB에서 불러올 때)
    public Post(String postId, String writerId, String content,
                int numLikes, int numDislikes) {
        this.postId = postId;
        this.writerId = writerId;
        this.content = content;
        this.numLikes = numLikes;
        this.numDislikes = numDislikes;
    }

    // ------------------------------ Getter ------------------------------

    public String getPostId() {
        return postId;
    }

    public String getWriterId() {
        return writerId;
    }

    public String getContent() {
        return content;
    }

    public int getNumLikes() {
        return numLikes;
    }

    public int getNumDislikes() {
        return numDislikes;
    }

    // ------------------------------ Setter (옵션) ------------------------------

    public void setPostId(String postId) {
        this.postId = postId;
    }
    
    public void setNumLikes(int n) {
        this.numLikes = n;
    }

    public void setNumDislikes(int n) {
        this.numDislikes = n;
    }

    // ------------------------------ toString ------------------------------

    @Override
    public String toString() {
        return writerId + ": " + content;
    }
}
