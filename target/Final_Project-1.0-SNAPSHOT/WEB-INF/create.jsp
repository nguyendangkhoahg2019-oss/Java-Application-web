<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
    <head>
        <title>Them Sinh Vien</title>
        <style>
            body {
                font-family: Arial, sans-serif;
                margin: 24px;
                background: #f5f7fb;
            }
            .panel {
                max-width: 900px;
                margin: 0 auto;
                background: #fff;
                border-radius: 12px;
                padding: 24px;
                box-shadow: 0 6px 18px rgba(15, 23, 42, 0.08);
            }
            .grid {
                display: grid;
                grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
                gap: 12px;
            }
            label {
                display: block;
                font-weight: bold;
                margin-bottom: 4px;
            }
            input, select {
                width: 100%;
                padding: 10px;
                box-sizing: border-box;
                border: 1px solid #cbd5e1;
                border-radius: 8px;
            }
            .actions {
                display: flex;
                gap: 8px;
                margin-top: 16px;
            }
            .btn {
                border: none;
                border-radius: 8px;
                padding: 10px 16px;
                cursor: pointer;
                background: #2563eb;
                color: #fff;
                text-decoration: none;
            }
            .btn-light {
                background: #e2e8f0;
                color: #0f172a;
            }
            .error {
                color: #b00020;
                font-weight: bold;
            }
        </style>
    </head>
    <body>
        <div class="panel">
            <h2>Create New Students</h2>
            <c:if test="${not empty error}">
                <p class="error">${error}</p>
            </c:if>
            <form action="${pageContext.request.contextPath}/student" method="post">
                <input type="hidden" name="action" value="create" />
                <input type="hidden" name="page" value="${page}" />
                <input type="hidden" name="studentIdKeyword" value="${studentIdKeyword}" />
                <input type="hidden" name="fullNameKeyword" value="${fullNameKeyword}" />
                <div class="grid">
                    <div>
                        <label for="fullName">Full Name</label>
                        <input id="fullName" type="text" name="fullName" value="${student.fullName}" required />
                    </div>
                    <div>
                        <label for="dob">Date</label>
                        <input id="dob" type="date" name="dob" value="${student.dob}" required />
                    </div>
                    <div>
                        <label for="gender">Gender</label>
                        <select id="gender" name="gender">
                            <option value="Nam" ${student.gender == 'Nu' ? '' : 'selected'}>Nam</option>
                            <option value="Nu" ${student.gender == 'Nu' ? 'selected' : ''}>Nu</option>
                        </select>
                    </div>
                    <div>
                        <label for="email">Email</label>
                        <input id="email" type="email" name="email" value="${student.email}" required />
                    </div>
                    <div>
                        <label for="address">Address</label>
                        <input id="address" type="text" name="address" value="${student.address}" />
                    </div>
                    <div>
                        <label for="gpa">GPA</label>
                        <input id="gpa" type="number" step="0.01" min="0" max="10" name="gpa" value="${student.gpa}" required />
                    </div>
                    <div>
                        <label for="classId">Major ID</label>
                        <select id="classId" name="classId" required>
                            <option value="">Chosse Class</option>
                            <c:forEach items="${classList}" var="c">
                                <option value="${c.classId}" ${student.classId == c.classId ? 'selected' : ''}>${c.classId} - ${c.className} (${c.roomNumber})</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div>
                        <label for="studyStatus">Status</label>
                        <input id="studyStatus" type="text" name="studyStatus" value="${empty student.studyStatus ? 'Dang hoc' : student.studyStatus}" required />
                    </div>
                </div>
                <div class="actions">
                    <button class="btn" type="submit">Save</button>
                    <a class="btn btn-light" href="${pageContext.request.contextPath}/student?page=${page}&studentIdKeyword=${studentIdKeyword}&fullNameKeyword=${fullNameKeyword}">Cancel</a>
                </div>
            </form>
        </div>
    </body>
</html>
