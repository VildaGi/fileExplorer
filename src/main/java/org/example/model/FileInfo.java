package org.example.model;

public class FileInfo {

    private String name;
    private String path;
    private boolean directory;
    private long size;
    private String lastModified;

    public FileInfo(String name, String path, boolean directory, long size, String lastModified) {
        this.name = name;
        this.path = path;
        this.directory = directory;
        this.size = size;
        this.lastModified = lastModified;
    }

    public String getName() { return name; }
    public String getPath() { return path; }
    public boolean isDirectory() { return directory; }
    public long getSize() { return size; }
    public String getLastModified() { return lastModified; }
}