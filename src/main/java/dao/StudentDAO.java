package dao;

import db.DBContext;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import model.ClassRoom;
import model.Student;

public class StudentDAO extends DBContext {

    public List<Student> getAllActiveStudents() {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT * FROM Students WHERE is_deleted = 0 ORDER BY student_id DESC";
        try (PreparedStatement st = connection.prepareStatement(sql);
                ResultSet rs = st.executeQuery()) {
            while (rs.next()) {
                list.add(mapStudent(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
    
    public List<Student> searchStudents(String studentIdKeyword, String fullNameKeyword, int page, int pageSize) {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT * FROM Students "
                + "WHERE is_deleted = 0 "
                + "AND (? = '' OR CAST(student_id AS NVARCHAR(50)) LIKE ?) "
                + "AND (? = '' OR full_name COLLATE Latin1_General_CI_AI LIKE ? COLLATE Latin1_General_CI_AI) "
                + "ORDER BY student_id DESC "
                + "OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
        try (PreparedStatement st = connection.prepareStatement(sql)) {
            String normalizedStudentId = normalizeKeyword(studentIdKeyword);
            String normalizedFullName = normalizeKeyword(fullNameKeyword);
            st.setString(1, normalizedStudentId);
            st.setString(2, "%" + normalizedStudentId + "%");
            st.setString(3, normalizedFullName);
            st.setString(4, "%" + normalizedFullName + "%");
            st.setInt(5, Math.max(page - 1, 0) * pageSize);
            st.setInt(6, pageSize);
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) {
                    list.add(mapStudent(rs));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public int countStudents(String studentIdKeyword, String fullNameKeyword) {
        String sql = "SELECT COUNT(*) FROM Students "
                + "WHERE is_deleted = 0 "
                + "AND (? = '' OR CAST(student_id AS NVARCHAR(50)) LIKE ?) "
                + "AND (? = '' OR full_name COLLATE Latin1_General_CI_AI LIKE ? COLLATE Latin1_General_CI_AI)";
        try (PreparedStatement st = connection.prepareStatement(sql)) {
            String normalizedStudentId = normalizeKeyword(studentIdKeyword);
            String normalizedFullName = normalizeKeyword(fullNameKeyword);
            st.setString(1, normalizedStudentId);
            st.setString(2, "%" + normalizedStudentId + "%");
            st.setString(3, normalizedFullName);
            st.setString(4, "%" + normalizedFullName + "%");
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public Student getStudentById(int studentId) {
        String sql = "SELECT * FROM Students WHERE student_id = ? AND is_deleted = 0";
        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setInt(1, studentId);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    return mapStudent(rs);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void createStudent(Student student) {
        String sql = "INSERT INTO Students (full_name, dob, gender, email, address, gpa, class_id, study_status, is_deleted) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, 0)";
        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setString(1, student.getFullName());
            st.setDate(2, student.getDob());
            st.setString(3, student.getGender());
            st.setString(4, student.getEmail());
            st.setString(5, student.getAddress());
            st.setDouble(6, student.getGpa());
            st.setInt(7, student.getClassId());
            st.setString(8, student.getStudyStatus());
            st.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException(resolvePersistenceError(e));
        }
    }

    public void updateStudent(Student student) {
        String sql = "UPDATE Students SET full_name = ?, dob = ?, gender = ?, email = ?, address = ?, gpa = ?, class_id = ?, study_status = ? "
                + "WHERE student_id = ? AND is_deleted = 0";
        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setString(1, student.getFullName());
            st.setDate(2, student.getDob());
            st.setString(3, student.getGender());
            st.setString(4, student.getEmail());
            st.setString(5, student.getAddress());
            st.setDouble(6, student.getGpa());
            st.setInt(7, student.getClassId());
            st.setString(8, student.getStudyStatus());
            st.setInt(9, student.getStudentId());
            st.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException(resolvePersistenceError(e));
        }
    }

    public void deleteStudent(int studentId) {
        String sql = "UPDATE Students SET is_deleted = 1 WHERE student_id = ?";
        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setInt(1, studentId);
            st.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Khong the xoa sinh vien.");
        }
    }

    public boolean isClassIdExists(int classId) {
        String sql = "SELECT 1 FROM Classes WHERE class_id = ?";
        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setInt(1, classId);
            try (ResultSet rs = st.executeQuery()) {
                return rs.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isEmailExists(String email, Integer excludeStudentId) {
        String sql = "SELECT 1 FROM Students WHERE email = ? AND (? IS NULL OR student_id <> ?)";
        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setString(1, email);
            if (excludeStudentId == null) {
                st.setNull(2, java.sql.Types.INTEGER);
                st.setNull(3, java.sql.Types.INTEGER);
            } else {
                st.setInt(2, excludeStudentId);
                st.setInt(3, excludeStudentId);
            }
            try (ResultSet rs = st.executeQuery()) {
                return rs.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<ClassRoom> getAllClasses() {
        List<ClassRoom> list = new ArrayList<>();
        String sql = "SELECT class_id, class_name, room_number FROM Classes ORDER BY class_id";
        try (PreparedStatement st = connection.prepareStatement(sql);
                ResultSet rs = st.executeQuery()) {
            while (rs.next()) {
                ClassRoom classRoom = new ClassRoom();
                classRoom.setClassId(rs.getInt("class_id"));
                classRoom.setClassName(rs.getString("class_name"));
                classRoom.setRoomNumber(rs.getString("room_number"));
                list.add(classRoom);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    private String normalizeKeyword(String keyword) {
        return keyword == null ? "" : keyword.trim();
    }

    private String resolvePersistenceError(Exception e) {
        String message = e.getMessage() == null ? "" : e.getMessage().toLowerCase();
        if (message.contains("unique") || message.contains("uq__students__") || message.contains("duplicate")) {
            return "Email da ton tai, vui long nhap email khac.";
        }
        if (message.contains("fk_student_class") || message.contains("foreign key")) {
            return "Class ID khong ton tai.";
        }
        if (message.contains("check") || message.contains("gpa")) {
            return "GPA khong hop le. Vui long nhap trong khoang cho phep.";
        }
        return "Khong the luu thong tin sinh vien.";
    }

    private Student mapStudent(ResultSet rs) throws Exception {
        Student s = new Student();
        s.setStudentId(rs.getInt("student_id"));
        s.setFullName(rs.getString("full_name"));
        s.setDob(rs.getDate("dob"));
        s.setGender(rs.getString("gender"));
        s.setEmail(rs.getString("email"));
        s.setAddress(rs.getString("address"));
        s.setGpa(rs.getDouble("gpa"));
        s.setClassId(rs.getInt("class_id"));
        s.setStudyStatus(rs.getString("study_status"));
        s.setIsDeleted(rs.getBoolean("is_deleted"));
        s.setCreatedAt(rs.getTimestamp("created_at"));
        return s;
    }
}
