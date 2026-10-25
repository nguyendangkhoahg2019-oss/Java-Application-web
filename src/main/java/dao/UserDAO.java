package dao;

import db.DBContext;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.User;

public class UserDAO extends DBContext {

    private String hashSha256(String raw) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(raw.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02X", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(UserDAO.class.getName()).log(Level.SEVERE, null, ex);
            return "";
        }
    }

    public User login(String username, String rawPassword) {
        String sql = "SELECT u.user_id, u.username, u.role_id, u.student_id, u.is_active, r.role_name "
                + "FROM Users u INNER JOIN Roles r ON u.role_id = r.role_id "
                + "WHERE u.username = ? AND u.password_hash = ? AND u.is_active = 1";
        String hashedPass = hashSha256(rawPassword);

        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setString(1, username);
            st.setString(2, hashedPass);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    User u = new User();
                    u.setUserId(rs.getInt("user_id"));
                    u.setUsername(rs.getString("username"));
                    u.setRoleId(rs.getInt("role_id"));
                    u.setRoleName(rs.getString("role_name"));
                    u.setStudentId(rs.getObject("student_id") != null ? rs.getInt("student_id") : null);
                    u.setActive(rs.getBoolean("is_active"));
                    return u;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
