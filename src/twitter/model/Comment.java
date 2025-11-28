package twitter.model;

public class Comment {
    private String writerId;
    private String content;

    public Comment(String writerId, String content){
        this.writerId = writerId;
        this.content = content;
    }

    public String getWriterId() { return writerId; }
    public String getContent() { return content; }

    @Override
    public String toString() {
        return writerId + ": " + content;
    }
}
