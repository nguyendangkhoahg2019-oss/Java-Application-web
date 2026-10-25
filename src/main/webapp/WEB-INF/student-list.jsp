<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
    <head>
        <title>Student List</title>
        <style>
            body {
                font-family: Arial, sans-serif;
                margin: 24px;
                background: #f5f7fb;
                color: #1f2937;
            }

            table {
                width: 100%;
                border-collapse: collapse;
                margin-top: 20px;
            }

            th, td {
                border: 1px solid #ccc;
                padding: 8px;
                text-align: left;
                vertical-align: top;
            }

            .panel {
                border: 1px solid #ddd;
                padding: 16px;
                margin-top: 16px;
                background: #fff;
                border-radius: 12px;
                box-shadow: 0 6px 18px rgba(15, 23, 42, 0.08);
            }

            .grid {
                display: grid;
                grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
                gap: 12px;
            }

            .grid label {
                display: block;
                font-weight: bold;
                margin-bottom: 4px;
            }

            .grid input,
            .grid select {
                width: 100%;
                padding: 8px;
                box-sizing: border-box;
            }

            .actions {
                display: flex;
                gap: 8px;
                align-items: center;
                flex-wrap: wrap;
            }

            .inline-form {
                display: inline;
            }

            .error {
                color: #b00020;
                font-weight: bold;
            }

            .topbar {
                display: flex;
                justify-content: space-between;
                align-items: center;
                gap: 12px;
                flex-wrap: wrap;
            }

            .btn,
            button,
            input[type="submit"] {
                border: none;
                border-radius: 8px;
                padding: 10px 16px;
                cursor: pointer;
                background: #2563eb;
                color: #fff;
                text-decoration: none;
            }

            .btn-secondary {
                background: #475569;
            }

            .btn-danger {
                background: #dc2626;
            }

            .btn-light {
                background: #e2e8f0;
                color: #0f172a;
            }

            .search-form input {
                width: 100%;
                padding: 10px;
                box-sizing: border-box;
                border: 1px solid #cbd5e1;
                border-radius: 8px;
            }

            .pagination {
                display: flex;
                gap: 8px;
                flex-wrap: wrap;
                margin-top: 16px;
                align-items: center;
            }

            .pagination a,
            .pagination span {
                padding: 8px 12px;
                border-radius: 8px;
                background: #fff;
                border: 1px solid #cbd5e1;
                text-decoration: none;
                color: #0f172a;
            }

            .pagination .current {
                background: #2563eb;
                color: #fff;
                border-color: #2563eb;
            }
        </style>
    </head>
    <body>
        <div class="topbar">
            <div>
                <h2>Students List</h2>
                <p>Hi, ${sessionScope.account.username} | <a href="${pageContext.request.contextPath}/logout">Logout</a></p>
            </div>
            <c:if test="${sessionScope.account.roleId == 1}">
                <a class="btn" href="${pageContext.request.contextPath}/student?action=create&page=${currentPage}&studentIdKeyword=${studentIdKeyword}&fullNameKeyword=${fullNameKeyword}">Add</a>
            </c:if>
        </div>

        <c:if test="${not empty error}">
            <p class="error">${error}</p>
        </c:if>

        <div class="panel">
            <form class="search-form" action="${pageContext.request.contextPath}/student" method="get">
                <div class="grid">
                    <div>
                        <label for="studentIdKeyword">Major ID</label>
                        <input id="studentIdKeyword" type="text" name="studentIdKeyword" value="${studentIdKeyword}" placeholder="Major ID" />
                    </div>
                    <div>
                        <label for="fullNameKeyword">Student Name</label>
                        <input id="fullNameKeyword" type="text" name="fullNameKeyword" value="${fullNameKeyword}" placeholder="Enter Full Name" />
                    </div>
                </div>
                <p class="actions">
                    <button type="submit">Search</button>
                    <a class="btn btn-light" href="${pageContext.request.contextPath}/student">Reset</a>
                </p>
            </form>
        </div>

        <div class="panel">
            <p>There are currently ${totalStudents} students.</p>

            <table>
                <tr>
                    <th>ID</th>
                    <th>Full Name</th>
                    <th>Date</th>
                    <th>Gender</th>
                    <th>Email</th>
                    <th>Address</th>
                    <th>GPA</th>
                    <th>Major ID</th>
                    <th>Status</th>
                    <c:if test="${sessionScope.account.roleId == 1}">
                        <th>Action</th>
                    </c:if>
                </tr>
                <c:choose>
                    <c:when test="${empty studentList}">
                        <tr>
                            <td colspan="${sessionScope.account.roleId == 1 ? 10 : 9}">No suitable students found.</td>
                        </tr>
                    </c:when>
                    <c:otherwise>
                        <c:forEach items="${studentList}" var="s">
                            <tr>
                                <td>${s.studentId}</td>
                                <td>${s.fullName}</td>
                                <td>${s.dob}</td>
                                <td>${s.gender}</td>
                                <td>${s.email}</td>
                                <td>${s.address}</td>
                                <td>${s.gpa}</td>
                                <td>${s.classId}</td>
                                <td>${s.studyStatus}</td>
                                <c:if test="${sessionScope.account.roleId == 1}">
                                    <td class="actions">
                                        <a class="btn btn-secondary" href="${pageContext.request.contextPath}/student?action=edit&id=${s.studentId}&page=${currentPage}&studentIdKeyword=${studentIdKeyword}&fullNameKeyword=${fullNameKeyword}">Edit</a>
                                        <a class="btn btn-danger" href="${pageContext.request.contextPath}/student?action=delete&id=${s.studentId}&page=${currentPage}&studentIdKeyword=${studentIdKeyword}&fullNameKeyword=${fullNameKeyword}">Delete</a>
                                    </td>
                                </c:if>
                            </tr>
                        </c:forEach>
                    </c:otherwise>
                </c:choose>
            </table>

            <c:if test="${totalPages > 1}">
                <div class="pagination">
                    <c:if test="${currentPage > 1}">
                        <a href="${pageContext.request.contextPath}/student?page=${currentPage - 1}&studentIdKeyword=${studentIdKeyword}&fullNameKeyword=${fullNameKeyword}">Previous</a>
                    </c:if>

                    <c:forEach begin="1" end="${totalPages}" var="pageNumber">
                        <c:choose>
                            <c:when test="${pageNumber == currentPage}">
                                <span class="current">${pageNumber}</span>
                            </c:when>
                            <c:otherwise>
                                <a href="${pageContext.request.contextPath}/student?page=${pageNumber}&studentIdKeyword=${studentIdKeyword}&fullNameKeyword=${fullNameKeyword}">${pageNumber}</a>
                            </c:otherwise>
                        </c:choose>
                    </c:forEach>

                    <c:if test="${currentPage < totalPages}">
                        <a href="${pageContext.request.contextPath}/student?page=${currentPage + 1}&studentIdKeyword=${studentIdKeyword}&fullNameKeyword=${fullNameKeyword}">Next page</a>
                    </c:if>
                </div>
            </c:if>
        </div>
    </body>
</html>
