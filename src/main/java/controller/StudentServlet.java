package controller;

import dao.StudentDAO;
import java.io.IOException;
import java.sql.Date;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.ClassRoom;
import model.Student;
import model.User;

@WebServlet(name = "StudentServlet", urlPatterns = {"/student"})
public class StudentServlet extends HttpServlet {

    private static final int PAGE_SIZE = 10;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        User account = (User) session.getAttribute("account");
        if (account == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        StudentDAO dao = new StudentDAO();
        String action = request.getParameter("action");

        if ("create".equals(action) && account.isAdmin()) {
            forwardForm(request, response, "/WEB-INF/create.jsp");
            return;
        }

        if ("edit".equals(action) && account.isAdmin()) {
            Student student = getStudentFromRequest(request, dao);
            if (student == null) {
                request.setAttribute("error", "Khong tim thay sinh vien can cap nhat.");
                showStudentList(request, response, dao);
                return;
            }
            request.setAttribute("student", student);
            forwardForm(request, response, "/WEB-INF/edit.jsp");
            return;
        }

        if ("delete".equals(action) && account.isAdmin()) {
            Student student = getStudentFromRequest(request, dao);
            if (student == null) {
                request.setAttribute("error", "Khong tim thay sinh vien can xoa.");
                showStudentList(request, response, dao);
                return;
            }
            request.setAttribute("student", student);
            forwardForm(request, response, "/WEB-INF/delete.jsp");
            return;
        }

        showStudentList(request, response, dao);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        User account = (User) session.getAttribute("account");
        if (account == null || !account.isAdmin()) {
            response.sendRedirect(request.getContextPath() + "/student");
            return;
        }

        request.setCharacterEncoding("UTF-8");
        StudentDAO dao = new StudentDAO();
        String action = request.getParameter("action");

        try {
            if ("create".equals(action)) {
                dao.createStudent(readStudentFromRequest(request, false));
            } else if ("update".equals(action)) {
                dao.updateStudent(readStudentFromRequest(request, true));
            } else if ("delete".equals(action)) {
                String idStr = request.getParameter("id");
                if (idStr != null && !idStr.trim().isEmpty()) {
                    dao.deleteStudent(Integer.parseInt(idStr));
                }
            }
            response.sendRedirect(request.getContextPath() + "/student");
        } catch (IllegalArgumentException e) {
            String page = trimOrEmpty(request.getParameter("page"));
            String studentIdKeyword = trimOrEmpty(request.getParameter("studentIdKeyword"));
            String fullNameKeyword = trimOrEmpty(request.getParameter("fullNameKeyword"));
            request.setAttribute("error", e.getMessage());
            request.setAttribute("page", parsePage(page));
            request.setAttribute("studentIdKeyword", studentIdKeyword);
            request.setAttribute("fullNameKeyword", fullNameKeyword);

            if ("create".equals(action)) {
                request.setAttribute("student", readStudentDraft(request, false));
                forwardForm(request, response, "/WEB-INF/create.jsp");
                return;
            }

            if ("update".equals(action)) {
                request.setAttribute("student", readStudentDraft(request, true));
                forwardForm(request, response, "/WEB-INF/edit.jsp");
                return;
            }

            if ("delete".equals(action)) {
                request.setAttribute("student", getStudentFromRequest(request, dao));
                forwardForm(request, response, "/WEB-INF/delete.jsp");
                return;
            }

            showStudentList(request, response, dao);
        }
    }

    private void showStudentList(HttpServletRequest request, HttpServletResponse response, StudentDAO dao)
            throws ServletException, IOException {
        String studentIdKeyword = trimOrEmpty(request.getParameter("studentIdKeyword"));
        String fullNameKeyword = trimOrEmpty(request.getParameter("fullNameKeyword"));
        int currentPage = parsePage(request.getParameter("page"));

        int totalStudents = dao.countStudents(studentIdKeyword, fullNameKeyword);
        int totalPages = Math.max(1, (int) Math.ceil((double) totalStudents / PAGE_SIZE));
        if (currentPage > totalPages) {
            currentPage = totalPages;
        }

        List<Student> list = dao.searchStudents(studentIdKeyword, fullNameKeyword, currentPage, PAGE_SIZE);
        request.setAttribute("studentList", list);
        request.setAttribute("studentIdKeyword", studentIdKeyword);
        request.setAttribute("fullNameKeyword", fullNameKeyword);
        request.setAttribute("currentPage", currentPage);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("pageSize", PAGE_SIZE);
        request.setAttribute("totalStudents", totalStudents);
        request.getRequestDispatcher("/WEB-INF/student-list.jsp").forward(request, response);
    }

    private void forwardForm(HttpServletRequest request, HttpServletResponse response, String jspPath)
            throws ServletException, IOException {
        request.setAttribute("page", parsePage(request.getParameter("page")));
        request.setAttribute("studentIdKeyword", trimOrEmpty(request.getParameter("studentIdKeyword")));
        request.setAttribute("fullNameKeyword", trimOrEmpty(request.getParameter("fullNameKeyword")));
        request.setAttribute("classList", new StudentDAO().getAllClasses());
        request.getRequestDispatcher(jspPath).forward(request, response);
    }

    private Student readStudentFromRequest(HttpServletRequest request, boolean includeId) {
        Student student = new Student();

        if (includeId) {
            String idRaw = request.getParameter("id");
            if (idRaw == null || idRaw.isBlank()) {
                throw new IllegalArgumentException("Thieu ID sinh vien.");
            }
            student.setStudentId(parseInt(idRaw, "ID sinh vien"));
        }

        String fullName = request.getParameter("fullName");
        String dob = request.getParameter("dob");
        String gender = request.getParameter("gender");
        String email = request.getParameter("email");
        String address = request.getParameter("address");
        String gpa = request.getParameter("gpa");
        String classId = request.getParameter("classId");
        String studyStatus = request.getParameter("studyStatus");

        if (fullName == null || fullName.isBlank()) {
            throw new IllegalArgumentException("Ho ten khong duoc de trong.");
        }
        if (dob == null || dob.isBlank()) {
            throw new IllegalArgumentException("Ngay sinh khong duoc de trong.");
        }
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email khong duoc de trong.");
        }

        student.setFullName(fullName.trim());
        student.setDob(Date.valueOf(dob));
        student.setGender(gender != null ? gender.trim() : "");
        student.setEmail(email.trim());
        student.setAddress(address != null ? address.trim() : "");
        student.setGpa(parseDouble(gpa, "GPA"));
        student.setClassId(parseInt(classId, "Class ID"));
        student.setStudyStatus(studyStatus != null ? studyStatus.trim() : "");
        validateStudentData(student, includeId);
        return student;
    }

    private Student readStudentDraft(HttpServletRequest request, boolean includeId) {
        Student student = new Student();
        if (includeId) {
            String idRaw = request.getParameter("id");
            if (idRaw != null && !idRaw.isBlank()) {
                try {
                    student.setStudentId(Integer.parseInt(idRaw));
                } catch (NumberFormatException ignored) {
                }
            }
        }
        student.setFullName(trimOrEmpty(request.getParameter("fullName")));
        String dob = trimOrEmpty(request.getParameter("dob"));
        if (!dob.isEmpty()) {
            try {
                student.setDob(Date.valueOf(dob));
            } catch (IllegalArgumentException ignored) {
            }
        }
        student.setGender(trimOrEmpty(request.getParameter("gender")));
        student.setEmail(trimOrEmpty(request.getParameter("email")));
        student.setAddress(trimOrEmpty(request.getParameter("address")));
        String gpa = trimOrEmpty(request.getParameter("gpa"));
        if (!gpa.isEmpty()) {
            try {
                student.setGpa(Double.parseDouble(gpa));
            } catch (NumberFormatException ignored) {
            }
        }
        String classId = trimOrEmpty(request.getParameter("classId"));
        if (!classId.isEmpty()) {
            try {
                student.setClassId(Integer.parseInt(classId));
            } catch (NumberFormatException ignored) {
            }
        }
        student.setStudyStatus(trimOrEmpty(request.getParameter("studyStatus")));
        return student;
    }

    private Student getStudentFromRequest(HttpServletRequest request, StudentDAO dao) {
        String idRaw = request.getParameter("id");
        if (idRaw == null || idRaw.isBlank()) {
            return null;
        }
        try {
            return dao.getStudentById(Integer.parseInt(idRaw));
        } catch (NumberFormatException e) {
            request.setAttribute("error", "ID sinh vien khong hop le.");
            return null;
        }
    }

    private int parsePage(String rawPage) {
        try {
            int page = Integer.parseInt(rawPage);
            return Math.max(page, 1);
        } catch (NumberFormatException e) {
            return 1;
        }
    }

    private String trimOrEmpty(String value) {
        return value == null ? "" : value.trim();
    }

    private void validateStudentData(Student student, boolean includeId) {
        StudentDAO dao = new StudentDAO();
        if (student.getGpa() < 0 || student.getGpa() > 10) {
            throw new IllegalArgumentException("GPA phai nam trong khoang tu 0 den 10.");
        }
        if (!dao.isClassIdExists(student.getClassId())) {
            throw new IllegalArgumentException("Class ID khong ton tai. Vui long chon lop hop le.");
        }
        Integer excludeStudentId = includeId ? student.getStudentId() : null;
        if (dao.isEmailExists(student.getEmail(), excludeStudentId)) {
            throw new IllegalArgumentException("Email da ton tai, vui long nhap email khac.");
        }
    }

    private int parseInt(String raw, String fieldName) {
        try {
            return Integer.parseInt(raw);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(fieldName + " khong hop le.");
        }
    }

    private double parseDouble(String raw, String fieldName) {
        try {
            return Double.parseDouble(raw);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(fieldName + " khong hop le.");
        }
    }
}
