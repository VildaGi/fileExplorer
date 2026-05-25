<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
<head>
    <title>File Manager</title>
</head>
<body>

<h2>File Manager</h2>

<p>${currentTime}</p>
<p>/${currentPath}</p>

<c:if test="${not empty parentPath}">
    <c:url var="upUrl" value="/files">
        <c:param name="path" value="${parentPath}" />
    </c:url>

    <a href="${upUrl}">⬆️ Вверх</a>
</c:if>

<br><br>

<table border="1" cellpadding="5">
    <tr>
        <th>Имя</th>
        <th>Размер (байты)</th>
        <th>Дата</th>
    </tr>

    <c:forEach var="file" items="${files}">
        <tr>

            <td>
                <c:choose>
                    <c:when test="${file.directory}">
                        <c:url var="folderUrl" value="/files">
                            <c:param name="path" value="${file.path}" />
                        </c:url>

                        📁 <a href="${folderUrl}">${file.name}/</a>
                    </c:when>
                    <c:otherwise>
                        <c:url var="fileUrl" value="/files">
                            <c:param name="path" value="${file.path}" />
                            <c:param name="download" value="true" />
                        </c:url>

                        📄 <a href="${fileUrl}">${file.name}</a>
                    </c:otherwise>
                </c:choose>
            </td>

            <td>
                <c:choose>
                    <c:when test="${file.directory}">
                        -
                    </c:when>
                    <c:otherwise>
                        ${file.size}
                    </c:otherwise>
                </c:choose>
            </td>

            <td>
                    ${file.lastModified}
            </td>

        </tr>
    </c:forEach>

</table>

<form action="logout" method="get" style="float:right;">
    <button type="submit">Logout</button>
</form>

</body>
</html>