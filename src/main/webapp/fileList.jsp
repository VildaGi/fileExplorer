<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="org.example.servlet.FileExplorerServlet" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.util.List" %>
<%@ page import="java.net.URLEncoder" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>File Explorer</title>
</head>
<body>
<div class="container">
    <div class="header">
        <h1>
            <span>📁</span>
            File Explorer
        </h1>
    </div>

    <div class="timestamp">
        <span>🕐</span>
        <%
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date generatedTime = (Date) request.getAttribute("generatedTime");
            if (generatedTime == null) {
                generatedTime = new Date();
            }
        %>
        Page generated: <%= sdf.format(generatedTime) %>
    </div>

    <div class="path-info">
        <span class="path-label">📍 Current path:</span>
        <span class="path-value"><%= request.getAttribute("currentPath") %></span>
    </div>

    <div class="content">
        <%
            String parentPath = (String) request.getAttribute("parentPath");
            List<?> files = (List<?>) request.getAttribute("files");
        %>

        <% if (parentPath != null && !parentPath.isEmpty()) { %>
        <a href="<%= request.getContextPath() %>/?path=<%= URLEncoder.encode(parentPath, "UTF-8") %>" class="back-button">
            ⬆ Up to parent directory
        </a>
        <% } else { %>
        <div class="root-message">
            ⬆ Already at root directory
        </div>
        <% } %>

        <table>
            <thead>
            <tr>
                <th>📄 Name</th>
                <th>Size</th>
                <th>Type</th>
            </tr>
            </thead>
            <tbody>
            <%
                if (files != null && !files.isEmpty()) {
                    for (Object obj : files) {
                        if (obj instanceof FileExplorerServlet.FileItem) {
                            FileExplorerServlet.FileItem item = (FileExplorerServlet.FileItem) obj;
            %>
            <tr>
                <td>
                    <% if (item.isDirectory()) { %>
                    <a href="<%= request.getContextPath() %>/?path=<%= URLEncoder.encode(item.getPath(), "UTF-8") %>" class="folder-link">
                        <span class="icon">📁</span>
                        <%= item.getName() %>
                    </a>
                    <% } else { %>
                    <a href="<%= request.getContextPath() %>/?action=download&file=<%= URLEncoder.encode(item.getPath(), "UTF-8") %>" class="file-link">
                        <span class="icon">📄</span>
                        <%= item.getName() %>
                    </a>
                    <% } %>
                </td>
                <td class="size-column">
                    <% if (!item.isDirectory()) { %>
                    <%= item.getFormattedSize() %>
                    <% } else { %>
                    -
                    <% } %>
                </td>
                <td class="type-column">
                    <% if (item.isDirectory()) { %>
                    Directory
                    <% } else { %>
                    File
                    <% } %>
                </td>
            </tr>
            <%
                    }
                }
            } else {
            %>
            <tr>
                <td colspan="3" class="empty-message">
                    📂 This directory is empty
                </td>
            </tr>
            <%
                }
            %>
            </tbody>
        </table>
    </div>
</div>
</body>
</html>