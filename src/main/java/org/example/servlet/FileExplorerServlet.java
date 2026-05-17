package org.example.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

@WebServlet("/")
public class FileExplorerServlet extends HttpServlet {

    private static final String ROOT_PATH = System.getProperty("user.home");

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        if ("download".equals(action)) {
            handleDownload(request, response);
        } else {
            showDirectory(request, response);
        }
    }

    private void showDirectory(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Получаем путь из параметра или используем корневой
        String currentPath = request.getParameter("path");
        if (currentPath == null || currentPath.trim().isEmpty()) {
            currentPath = ROOT_PATH;
        } else {
            currentPath = URLDecoder.decode(currentPath, "UTF-8");
        }

        File currentDir = new File(currentPath);

        // Проверка безопасности - убеждаемся, что путь внутри разрешенной директории
        try {
            String canonicalCurrentDir = currentDir.getCanonicalPath();
            String canonicalRootPath = new File(ROOT_PATH).getCanonicalPath();

            // Если пытаются выйти за пределы ROOT_PATH
            if (!canonicalCurrentDir.startsWith(canonicalRootPath)) {
                currentDir = new File(ROOT_PATH);
            }
        } catch (IOException e) {
            currentDir = new File(ROOT_PATH);
        }

        // Проверяем существует ли директория
        if (!currentDir.exists() || !currentDir.isDirectory()) {
            currentDir = new File(ROOT_PATH);
        }

        // Получаем список файлов
        List<FileItem> files = new ArrayList<>();
        File[] fileList = currentDir.listFiles();

        if (fileList != null) {
            for (File file : fileList) {
                try {
                    files.add(new FileItem(
                            file.getName(),
                            file.isDirectory(),
                            file.length(),
                            file.getCanonicalPath()
                    ));
                } catch (IOException e) {
                    continue;
                }
            }

            // Сортируем: папки сначала, затем файлы, оба по алфавиту
            Collections.sort(files, new Comparator<FileItem>() {
                @Override
                public int compare(FileItem f1, FileItem f2) {
                    if (f1.isDirectory() == f2.isDirectory()) {
                        return f1.getName().compareToIgnoreCase(f2.getName());
                    }
                    return f1.isDirectory() ? -1 : 1;
                }
            });
        }


        String parentPath = currentDir.getParent();
        if (parentPath != null) {
            try {
                String canonicalParent = new File(parentPath).getCanonicalPath();
                String canonicalRoot = new File(ROOT_PATH).getCanonicalPath();
                if (canonicalParent.startsWith(canonicalRoot)) {
                    request.setAttribute("parentPath", parentPath);
                } else {
                    request.setAttribute("parentPath", null);
                }
            } catch (IOException e) {
                request.setAttribute("parentPath", null);
            }
        } else {
            request.setAttribute("parentPath", null);
        }

        // Передаем атрибуты в JSP
        request.setAttribute("currentPath", currentDir.getAbsolutePath());
        request.setAttribute("files", files);
        request.setAttribute("generatedTime", new Date());

        // Forward на JSP
        request.getRequestDispatcher("fileList.jsp").forward(request, response);
    }

    private void handleDownload(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        String filePath = request.getParameter("file");
        if (filePath == null || filePath.trim().isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "File path is required");
            return;
        }

        filePath = URLDecoder.decode(filePath, "UTF-8");
        File file = new File(filePath);

        // Проверка безопасности - убеждаемся, что файл внутри разрешенной директории
        try {
            String canonicalFilePath = file.getCanonicalPath();
            String canonicalRootPath = new File(ROOT_PATH).getCanonicalPath();

            if (!canonicalFilePath.startsWith(canonicalRootPath)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied");
                return;
            }
        } catch (IOException e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error resolving path");
            return;
        }

        if (!file.exists() || file.isDirectory()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "File not found");
            return;
        }

        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=\"" +
                new String(file.getName().getBytes(StandardCharsets.UTF_8), "ISO-8859-1") + "\"");
        response.setContentLengthLong(file.length());

        // Отправка файла
        try (FileInputStream fis = new FileInputStream(file);
             OutputStream os = response.getOutputStream()) {

            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            os.flush();
        }
    }

    // Вспомогательный класс для представления файла/папки
    public static class FileItem {
        private final String name;
        private final boolean isDirectory;
        private final long size;
        private final String path;

        public FileItem(String name, boolean isDirectory, long size, String path) {
            this.name = name;
            this.isDirectory = isDirectory;
            this.size = size;
            this.path = path;
        }

        public String getName() { return name; }
        public boolean isDirectory() { return isDirectory; }
        public long getSize() { return size; }
        public String getPath() { return path; }

        public String getFormattedSize() {
            if (size < 1024) return size + " B";
            if (size < 1024 * 1024) return String.format("%.2f KB", size / 1024.0);
            if (size < 1024 * 1024 * 1024) return String.format("%.2f MB", size / (1024.0 * 1024));
            return String.format("%.2f GB", size / (1024.0 * 1024 * 1024));
        }
    }
}