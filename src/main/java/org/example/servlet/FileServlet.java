package org.example.servlet;

import org.example.model.FileInfo;
import org.example.model.User;
import org.example.service.PathService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@WebServlet("/files")
public class FileServlet extends HttpServlet {
    //    private static final String BASE_DIR ="/home/alex/files"; // Linux
    private static final String BASE_DIR = "C:/files"; // Windows

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        var session = req.getSession(false);

        if (session == null || session.getAttribute("user") == null) {
            resp.sendRedirect("login");
            return;
        }

        User user = (User) session.getAttribute("user");

        Path userBaseDir = Paths.get(BASE_DIR, user.getLogin())
                .toAbsolutePath()
                .normalize();

        if (!Files.exists(userBaseDir)) {
            Files.createDirectories(userBaseDir);
        }

        PathService pathService = new PathService(userBaseDir);

        Path requestedPath = pathService.resolvePath(req.getParameter("path"));

        if (!pathService.isSafePath(requestedPath)) {
            resp.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        if (!Files.exists(requestedPath)) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "There's not the path\n" +
                    "PARAM = " + req.getParameter("path") + "\n" +
                    "RESOLVED = " + requestedPath + "\n" +
                    "ABS = " + requestedPath.toAbsolutePath());
            return;
        };

        if (isDownload(req, requestedPath)) {
            downloadFile(requestedPath, resp);
            return;
        }

        if (Files.isDirectory(requestedPath)) {
            showDirectory(requestedPath, req, resp, pathService);
            return;
        }

        resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid request");
    }

    // Проверка скачивания
    private boolean isDownload(HttpServletRequest request, Path path) {
        return Files.isRegularFile(path)
                && "true".equals(request.getParameter("download"));
    }

    // Отображение папки
    private void showDirectory(Path dir,
                               HttpServletRequest req,
                               HttpServletResponse resp,
                               PathService pathService)
            throws ServletException, IOException {

        List<FileInfo> files = listFiles(dir, pathService);

        String parentPath = pathService.getParentPath(dir);

        String currentTime = FORMATTER.format(LocalDateTime.now());

        req.setAttribute("files", files);
        req.setAttribute(
                "currentPath", pathService.getRelativePath(dir)
        );
        req.setAttribute("parentPath", parentPath);
        req.setAttribute("currentTime", currentTime);

        req.getRequestDispatcher("/WEB-INF/jsp/files.jsp")
                .forward(req, resp);
    }

    // Получаем полный список файлов/папок по пути
    private List<FileInfo> listFiles(Path dir, PathService pathService) throws IOException {
        List<FileInfo> result = new ArrayList<>();

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
            for (Path path : stream) {

                String relative = pathService.getRelativePath(path);

                boolean isDir = Files.isDirectory(path);

                long size = isDir ? 0 : Files.size(path);

                String lastModified = FORMATTER.format(
                        Files.getLastModifiedTime(path)
                                .toInstant()
                                .atZone(ZoneId.systemDefault())
                                .toLocalDateTime()
                );

                result.add(new FileInfo(
                        path.getFileName().toString(),
                        relative,
                        isDir,
                        size,
                        lastModified
                ));
            }
        }

        result.sort(
                Comparator.comparing(FileInfo::isDirectory)
                        .reversed()
                        .thenComparing(FileInfo::getName)
        );

        return result;
    }

    private void downloadFile(Path file, HttpServletResponse response) throws IOException {

        // Говорит браузеру считать данный файл бинарным, чтобы его можно было скачать
        response.setContentType("application/octet-stream");

        String fileName = file.getFileName().toString();

        // ASCII fallback name позволяет скачивать файлы с кириллицей в названии
        String asciiFileName = fileName.replaceAll("[^\\x20-\\x7E]", "_");

        String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8)
                .replace("+", "%20");

        response.setHeader("Content-Disposition",
                "attachment; filename=\"" + asciiFileName + "\"; " +
                        "filename*=UTF-8''" + encodedFileName);

        try (InputStream in = Files.newInputStream(file);
             OutputStream out = response.getOutputStream()) {

            in.transferTo(out);
            out.flush();
        }
    }
}