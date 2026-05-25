package org.example.service;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;

public class PathService {

    private final Path baseDir;

    public PathService(String baseDir) {
        this.baseDir = Paths.get(baseDir).toAbsolutePath().normalize();
    }

    public PathService(Path baseDir) {
        this.baseDir = baseDir.toAbsolutePath().normalize();
    }

    // Определение пути
    public Path resolvePath(String pathParam) {
        if (pathParam == null || pathParam.isBlank()) {
            return baseDir;
        }

        pathParam = URLDecoder.decode(pathParam, StandardCharsets.UTF_8);
        pathParam = pathParam.replace("\\", "/");
        pathParam = pathParam.replaceAll("^/+", "");

        return baseDir.resolve(pathParam).normalize();
    }

    // Безопасность - проверка Path Traversal
    public boolean isSafePath(Path path) {
        Path base = baseDir.toAbsolutePath().normalize();
        Path target = path.toAbsolutePath().normalize();

        return target.startsWith(base);
    }

    public String getRelativePath(Path path) {
        return baseDir.relativize(path)
                .toString()
                .replace("\\", "/");
    }

    public String getParentPath(Path path) {
        Path parent = path.getParent();

        String parentPath = "/";

        if (parent != null) {
            parent = parent.normalize();

            if (isSafePath(parent)) {
                parentPath = "/" + getRelativePath(parent);
            }
        }

        return parentPath;
    }
}