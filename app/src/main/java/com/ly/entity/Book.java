package com.ly.entity;

import java.io.Serializable;

/**
 * 书本实体类
 */
public class Book implements Serializable {
    private int  id;
    private String name;
    private String path;
    private long seek;
    private long space;

    @Override
    public String toString() {
        return "Book{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", path='" + path + '\'' +
                ", seek='" + seek + '\'' +
                ", space='" + space + '\'' +
                '}';
    }

    public Book(int id, String name, String path, long seek, long space) {
        this.id = id;
        this.name = name;
        this.path = path;
        this.seek = seek;
        this.space = space;
    }

    public long getSpace() {
        return space;
    }

    public void setSpace(long space) {
        this.space = space;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getSeek() {
        return seek;
    }

    public void setSeek(long seek) {
        this.seek = seek;
    }


    public Book() {
    }
}
