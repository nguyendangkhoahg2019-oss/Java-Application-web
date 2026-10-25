<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
    <head>
        <title>Confirm Student Delete</title>
        <style>
            body {
                font-family: Arial, sans-serif;
                margin: 24px;
                background: #f5f7fb;
            }
            .panel {
                max-width: 720px;
                margin: 0 auto;
                background: #fff;
                border-radius: 12px;
                padding: 24px;
                box-shadow: 0 6px 18px rgba(15, 23, 42, 0.08);
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
                background: #dc2626;
                color: #fff;
                text-decoration: none;
            }
            .btn-light {
                background: #e2e8f0;
                color: #0f172a;
            }
            .notice {
                color: #b91c1c;
                font-style: italic;
            }
            .error {
                color: #b00020;
                font-weight: bold;
            }
        </style>
    </head>
    <body>
        <div class="panel">
            <h2>Confirm Student Delete</h2>
            <c:if test="${not empty error}">
                <p class="error">${error}</p>
            </c:if>
            <p>Do you want to delete!!!<strong>${student.fullName}</strong> (MSSV: ${student.studentId}) not?</p>
            <p class="notice">All the data will delete from system.</p>
            <form action="${pageContext.request.contextPath}/student" method="post">
                <input type="hidden" name="action" value="delete" />
                <input type="hidden" name="id" value="${student.studentId}" />
                <input type="hidden" name="page" value="${page}" />
                <input type="hidden" name="studentIdKeyword" value="${studentIdKeyword}" />
                <input type="hidden" name="fullNameKeyword" value="${fullNameKeyword}" />
                <div class="actions">
                    <button class="btn" type="submit">Confirm</button>
                    <a class="btn btn-light" href="${pageContext.request.contextPath}/student?page=${page}&studentIdKeyword=${studentIdKeyword}&fullNameKeyword=${fullNameKeyword}">Huy</a>
                </div>
            </form>
        </div>
    </body>
</html>
