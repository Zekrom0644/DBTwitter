package twitter.model;

public class Comment {
    private String commentId; // DB 연동용 ID
    private String writerId;
    private String content;
    private int numLikes;     // 좋아요 수

    public Comment(String writerId, String content){
        this.writerId = writerId;
        this.content = content;
        this.numLikes = 0;
    }

    public String getWriterId() { return writerId; }
    public String getContent() { return content; }
    
    // Getter & Setter 추가
    public String getCommentId() { return commentId; }
    public void setCommentId(String commentId) { this.commentId = commentId; }
    
    public int getNumLikes() { return numLikes; }
    public void setNumLikes(int numLikes) { this.numLikes = numLikes; }

    @Override
    public String toString() {
        return writerId + ": " + content;
    }
}