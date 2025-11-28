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
    private static final String DB_PW   = "1212";     // 비밀번호 (설정하신 비번으로)

    public TwitterService() {
        try {
            // JDBC 드라이버 로드
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    // DB 연결 헬퍼 메서드
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PW);
    }

    /* ==================================================
     * 1. 계정 관련 (Login / Signup / Password)
     * ================================================== */
    public boolean login(String id, String pwd) {
        String sql = "SELECT id FROM users WHERE id = ? AND pwd = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, id);
            pstmt.setString(2, pwd);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next(); 
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean signup(String id, String pwd) {
        String sql = "INSERT INTO users (id, pwd) VALUES (?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, id);
            pstmt.setString(2, pwd);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // [New] 비밀번호 변경 기능
    public boolean changePassword(String userId, String newPwd) {
        String sql = "UPDATE users SET pwd = ? WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, newPwd);
            pstmt.setString(2, userId);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /* ==================================================
     * 2. 타임라인 / 포스트 (Timeline / Write / Delete)
     * ================================================== */
    public boolean writePost(String writerId, String content) {
        String sql = "INSERT INTO posts (writer_id, content) VALUES (?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, writerId);
            pstmt.setString(2, content);
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // [New] 게시글 삭제 기능
    public boolean deletePost(String postId) {
        String sql = "DELETE FROM posts WHERE post_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, Integer.parseInt(postId));
            
            return pstmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // [Update] 타임라인: 나 + 내가 팔로우한 사람의 글만 최신순 조회
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
                    String pid = String.valueOf(rs.getInt("post_id"));
                    String wId = rs.getString("writer_id");
                    String txt = rs.getString("content");
                    int likes = rs.getInt("num_likes");
                    int dislikes = rs.getInt("num_dislikes");
                    
                    Post p = new Post(pid, wId, txt, likes, dislikes);
                    list.add(p);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /* ==================================================
     * 3. 게시글 좋아요 / 싫어요 (토글 방식)
     * ================================================== */
    
    // 헬퍼 메서드: 이미 반응했는지 확인
    private boolean hasPostReacted(String postId, String userId, boolean isLike) {
        String sql = "SELECT 1 FROM likes WHERE post_id = ? AND user_id = ? AND is_like = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, Integer.parseInt(postId));
            pstmt.setString(2, userId);
            pstmt.setBoolean(3, isLike);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        } catch (Exception e) {
            return false;
        }
    }

    public boolean toggleLike(String postId, String userId) {
        return togglePostReaction(postId, userId, true);
    }

    public boolean toggleDislike(String postId, String userId) {
        return togglePostReaction(postId, userId, false);
    }

    // 좋아요/싫어요 로직 통합 메서드
    private boolean togglePostReaction(String postId, String userId, boolean isLike) {
        boolean exists = hasPostReacted(postId, userId, isLike);
        
        String deleteSql = "DELETE FROM likes WHERE post_id=? AND user_id=? AND is_like=?";
        String insertSql = "INSERT INTO likes (post_id, user_id, is_like) VALUES (?, ?, ?)";
        
        // 카운트 컬럼 결정
        String col = isLike ? "num_likes" : "num_dislikes";
        
        // 이미 있으면 취소(-1), 없으면 추가(+1)
        String updatePostSql = exists 
                ? "UPDATE posts SET " + col + " = " + col + " - 1 WHERE post_id = ?" 
                : "UPDATE posts SET " + col + " = " + col + " + 1 WHERE post_id = ?";

        try (Connection conn = getConnection()) {
            // 1. likes 테이블 처리
            if (exists) {
                try (PreparedStatement pstmt = conn.prepareStatement(deleteSql)) {
                    pstmt.setInt(1, Integer.parseInt(postId));
                    pstmt.setString(2, userId);
                    pstmt.setBoolean(3, isLike);
                    pstmt.executeUpdate();
                }
            } else {
                try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
                    pstmt.setInt(1, Integer.parseInt(postId));
                    pstmt.setString(2, userId);
                    pstmt.setBoolean(3, isLike);
                    pstmt.executeUpdate();
                }
            }
            
            // 2. posts 테이블 카운트 업데이트
            try (PreparedStatement pstmt = conn.prepareStatement(updatePostSql)) {
                pstmt.setInt(1, Integer.parseInt(postId));
                pstmt.executeUpdate();
            }
            
            return !exists; // true: 추가됨, false: 취소됨
            
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /* ==================================================
     * 4. 팔로우 / 팔로잉
     * ================================================== */
    public List<User> getFollowers(String userId) {
        List<User> list = new ArrayList<>();
        String sql = "SELECT u.id FROM users u " +
                     "JOIN follows f ON u.id = f.follower_id " +
                     "WHERE f.target_id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(new User(rs.getString("id")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean isFollowing(String me, String target) {
        String sql = "SELECT 1 FROM follows WHERE follower_id = ? AND target_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, me);
            pstmt.setString(2, target);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            return false;
        }
    }

    public void follow(String me, String target) {
        String sql = "INSERT INTO follows (follower_id, target_id) VALUES (?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, me);
            pstmt.setString(2, target);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void unfollow(String me, String target) {
        String sql = "DELETE FROM follows WHERE follower_id = ? AND target_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, me);
            pstmt.setString(2, target);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /* ==================================================
     * 5. 댓글 (Comment) 및 댓글 좋아요
     * ================================================== */
    public List<Comment> getComments(String postIdStr) {
        List<Comment> list = new ArrayList<>();
        String sql = "SELECT * FROM comments WHERE post_id = ? ORDER BY created_at ASC";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            int pid = Integer.parseInt(postIdStr);
            pstmt.setInt(1, pid);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String cId = String.valueOf(rs.getInt("comment_id"));
                    String wId = rs.getString("writer_id");
                    String content = rs.getString("content");
                    int nLikes = rs.getInt("num_likes"); // DB 컬럼이 추가되어야 함
                    
                    // Comment 모델 생성자에 맞춰 데이터 주입
                    Comment c = new Comment(wId, content);
                    c.setCommentId(cId);
                    c.setNumLikes(nLikes);
                    
                    list.add(c);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public void addComment(String postIdStr, String writerId, String content) {
        String sql = "INSERT INTO comments (post_id, writer_id, content, num_likes) VALUES (?, ?, ?, 0)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            int pid = Integer.parseInt(postIdStr);
            pstmt.setInt(1, pid);
            pstmt.setString(2, writerId);
            pstmt.setString(3, content);
            
            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // [New] 댓글 좋아요 토글 기능
    public boolean toggleCommentLike(String commentId, String userId) {
        // 1. 이미 좋아요 눌렀는지 확인
        String checkSql = "SELECT 1 FROM comment_likes WHERE comment_id=? AND user_id=?";
        boolean exists = false;
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(checkSql)) {
            pstmt.setInt(1, Integer.parseInt(commentId));
            pstmt.setString(2, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                exists = rs.next();
            }
        } catch (Exception e) { return false; }

        String sqlExec;
        String sqlUpdate;

        if (exists) {
            // 취소
            sqlExec = "DELETE FROM comment_likes WHERE comment_id=? AND user_id=?";
            sqlUpdate = "UPDATE comments SET num_likes = num_likes - 1 WHERE comment_id=?";
        } else {
            // 추가
            sqlExec = "INSERT INTO comment_likes (comment_id, user_id) VALUES (?, ?)";
            sqlUpdate = "UPDATE comments SET num_likes = num_likes + 1 WHERE comment_id=?";
        }

        try (Connection conn = getConnection()) {
            try (PreparedStatement pstmt = conn.prepareStatement(sqlExec)) {
                pstmt.setInt(1, Integer.parseInt(commentId));
                pstmt.setString(2, userId);
                pstmt.executeUpdate();
            }
            try (PreparedStatement pstmt = conn.prepareStatement(sqlUpdate)) {
                pstmt.setInt(1, Integer.parseInt(commentId));
                pstmt.executeUpdate();
            }
            return !exists; // true: 증가, false: 감소
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}