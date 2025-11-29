package twitter.service;

import twitter.model.Post;
import twitter.model.User;
import twitter.model.Comment;

import java.sql.*;
import java.util.*;

public class TwitterService {

    // TODO: 본인의 DB 환경에 맞게 수정하세요.
    private static final String DB_URL = "jdbc:mysql://localhost:3306/twitter_db?serverTimezone=UTC";
    private static final String DB_USER = "root";     // 아이디
    private static final String DB_PW   = "1212";     // 비밀번호

    public TwitterService() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PW);
    }

    /* 1. 계정 관련 */
    public boolean login(String id, String pwd) {
        String sql = "SELECT id FROM users WHERE id = ? AND pwd = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            pstmt.setString(2, pwd);
            try (ResultSet rs = pstmt.executeQuery()) { return rs.next(); }
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean signup(String id, String pwd) {
        String sql = "INSERT INTO users (id, pwd) VALUES (?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            pstmt.setString(2, pwd);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean changePassword(String userId, String newPwd) {
        String sql = "UPDATE users SET pwd = ? WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newPwd);
            pstmt.setString(2, userId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    /* 2. 타임라인 / 포스트 */
    public boolean writePost(String writerId, String content) {
        String sql = "INSERT INTO posts (writer_id, content) VALUES (?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, writerId);
            pstmt.setString(2, content);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); return false; }
    }

    public boolean deletePost(String postId) {
        String sql = "DELETE FROM posts WHERE post_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, Integer.parseInt(postId));
            return pstmt.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    public List<Post> getTimeline(String userId) {
        List<Post> list = new ArrayList<>();
        String sql = "SELECT * FROM posts " +
                     "WHERE writer_id = ? " +
                     "OR writer_id IN (SELECT target_id FROM follows WHERE follower_id = ?) " +
                     "ORDER BY created_at DESC";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userId);
            pstmt.setString(2, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(new Post(
                        String.valueOf(rs.getInt("post_id")),
                        rs.getString("writer_id"),
                        rs.getString("content"),
                        rs.getInt("num_likes"),
                        rs.getInt("num_dislikes")
                    ));
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }
    
    // 특정 유저 글 보기 (프로필용)
    public List<Post> getUserPosts(String targetUserId) {
        List<Post> list = new ArrayList<>();
        String sql = "SELECT * FROM posts WHERE writer_id = ? ORDER BY created_at DESC";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, targetUserId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(new Post(
                        String.valueOf(rs.getInt("post_id")),
                        rs.getString("writer_id"),
                        rs.getString("content"),
                        rs.getInt("num_likes"),
                        rs.getInt("num_dislikes")
                    ));
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    /* 3. 좋아요 / 싫어요 */
    public boolean toggleLike(String postId, String userId) { return togglePostReaction(postId, userId, true); }
    public boolean toggleDislike(String postId, String userId) { return togglePostReaction(postId, userId, false); }

    private boolean togglePostReaction(String postId, String userId, boolean isLike) {
        // (기존과 동일 로직, 지면 관계상 핵심만 유지)
        String checkSql = "SELECT 1 FROM likes WHERE post_id=? AND user_id=? AND is_like=?";
        boolean exists = false;
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(checkSql)) {
            pstmt.setInt(1, Integer.parseInt(postId)); pstmt.setString(2, userId); pstmt.setBoolean(3, isLike);
            try(ResultSet rs = pstmt.executeQuery()){ exists = rs.next(); }
        } catch(Exception e){ return false; }

        String sql1 = exists ? "DELETE FROM likes WHERE post_id=? AND user_id=? AND is_like=?" 
                             : "INSERT INTO likes (post_id, user_id, is_like) VALUES (?, ?, ?)";
        String col = isLike ? "num_likes" : "num_dislikes";
        String sql2 = exists ? "UPDATE posts SET "+col+"="+col+"-1 WHERE post_id=?" 
                             : "UPDATE posts SET "+col+"="+col+"+1 WHERE post_id=?";

        try (Connection conn = getConnection()) {
            try(PreparedStatement p = conn.prepareStatement(sql1)) {
                p.setInt(1, Integer.parseInt(postId)); p.setString(2, userId); p.setBoolean(3, isLike); p.executeUpdate();
            }
            try(PreparedStatement p = conn.prepareStatement(sql2)) {
                p.setInt(1, Integer.parseInt(postId)); p.executeUpdate();
            }
            return !exists;
        } catch(Exception e){ e.printStackTrace(); return false; }
    }

    /* 4. 팔로우 / 팔로잉 */
    // 나를 팔로우하는 사람 (Followers)
    public List<User> getFollowers(String userId) {
        List<User> list = new ArrayList<>();
        String sql = "SELECT u.id FROM users u JOIN follows f ON u.id = f.follower_id WHERE f.target_id = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) list.add(new User(rs.getString("id")));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    // ★ [New] 내가 팔로우하는 사람 (Following)
    public List<User> getFollowing(String userId) {
        List<User> list = new ArrayList<>();
        // users 테이블과 follows 테이블 조인 (내가 follower일 때의 target을 찾음)
        String sql = "SELECT u.id FROM users u JOIN follows f ON u.id = f.target_id WHERE f.follower_id = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) list.add(new User(rs.getString("id")));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public boolean isFollowing(String me, String target) {
        String sql = "SELECT 1 FROM follows WHERE follower_id = ? AND target_id = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, me); pstmt.setString(2, target);
            try (ResultSet rs = pstmt.executeQuery()) { return rs.next(); }
        } catch (Exception e) { return false; }
    }

    public boolean follow(String me, String target) {
        String sql = "INSERT INTO follows (follower_id, target_id) VALUES (?, ?)";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, me); pstmt.setString(2, target); return pstmt.executeUpdate() > 0;
        } catch (Exception e) { return false; }
    }

    public boolean unfollow(String me, String target) {
        String sql = "DELETE FROM follows WHERE follower_id = ? AND target_id = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, me); pstmt.setString(2, target); return pstmt.executeUpdate() > 0;
        } catch (Exception e) { return false; }
    }

    // 추천 친구 (나 아님 & 팔로우 안함)
    public List<User> getRecommendations(String userId) {
        List<User> list = new ArrayList<>();
        String sql = "SELECT id FROM users WHERE id != ? AND id NOT IN (SELECT target_id FROM follows WHERE follower_id = ?)";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userId); pstmt.setString(2, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) list.add(new User(rs.getString("id")));
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }
    
    public List<User> searchUsers(String keyword) {
        List<User> list = new ArrayList<>();
        String sql = "SELECT id FROM users WHERE id LIKE ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%"+keyword+"%");
            try(ResultSet rs = pstmt.executeQuery()){ while(rs.next()) list.add(new User(rs.getString("id"))); }
        } catch(Exception e){ e.printStackTrace(); }
        return list;
    }
    
    public List<Post> searchPosts(String keyword) {
        List<Post> list = new ArrayList<>();
        String sql = "SELECT * FROM posts WHERE content LIKE ? ORDER BY created_at DESC";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%"+keyword+"%");
            try(ResultSet rs = pstmt.executeQuery()){ 
                while(rs.next()) list.add(new Post(String.valueOf(rs.getInt("post_id")), rs.getString("writer_id"), rs.getString("content"), rs.getInt("num_likes"), rs.getInt("num_dislikes"))); 
            }
        } catch(Exception e){ e.printStackTrace(); }
        return list;
    }

    /* 5. 댓글 */
    public List<Comment> getComments(String postIdStr) {
        List<Comment> list = new ArrayList<>();
        String sql = "SELECT * FROM comments WHERE post_id = ? ORDER BY created_at ASC";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, Integer.parseInt(postIdStr));
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Comment c = new Comment(rs.getString("writer_id"), rs.getString("content"));
                    c.setCommentId(String.valueOf(rs.getInt("comment_id")));
                    c.setNumLikes(rs.getInt("num_likes"));
                    list.add(c);
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    public void addComment(String postIdStr, String writerId, String content) {
        String sql = "INSERT INTO comments (post_id, writer_id, content, num_likes) VALUES (?, ?, ?, 0)";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, Integer.parseInt(postIdStr)); pstmt.setString(2, writerId); pstmt.setString(3, content);
            pstmt.executeUpdate();
        } catch (Exception e) { e.printStackTrace(); }
    }

    public boolean toggleCommentLike(String commentId, String userId) {
        // (기존과 동일 로직)
        String check = "SELECT 1 FROM comment_likes WHERE comment_id=? AND user_id=?";
        boolean exists = false;
        try(Connection c = getConnection(); PreparedStatement p = c.prepareStatement(check)){
            p.setInt(1, Integer.parseInt(commentId)); p.setString(2, userId);
            try(ResultSet rs = p.executeQuery()){ exists = rs.next(); }
        } catch(Exception e){ return false; }
        
        String sql1 = exists ? "DELETE FROM comment_likes WHERE comment_id=? AND user_id=?" : "INSERT INTO comment_likes (comment_id, user_id) VALUES (?, ?)";
        String sql2 = exists ? "UPDATE comments SET num_likes=num_likes-1 WHERE comment_id=?" : "UPDATE comments SET num_likes=num_likes+1 WHERE comment_id=?";
        
        try(Connection c = getConnection()){
            try(PreparedStatement p = c.prepareStatement(sql1)){ p.setInt(1, Integer.parseInt(commentId)); p.setString(2, userId); p.executeUpdate(); }
            try(PreparedStatement p = c.prepareStatement(sql2)){ p.setInt(1, Integer.parseInt(commentId)); p.executeUpdate(); }
            return !exists;
        } catch(Exception e){ return false; }
    }
}