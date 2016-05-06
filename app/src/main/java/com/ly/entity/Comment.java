package com.ly.entity;

import java.io.Serializable;

/**
 * 评论实体
 */
public class Comment implements Serializable {
    private String id;
    private String content;
    private String comment_time;
    private User user;

    @Override
    public String toString() {
        return "Comment{" +
                "id='" + id + '\'' +
                ", content='" + content + '\'' +
                ", comment_time='" + comment_time + '\'' +
                ", user=" + user +
                '}';
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getComment_time() {
        return comment_time;
    }

    public void setComment_time(String comment_time) {
        this.comment_time = comment_time;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Comment() {
    }

    public Comment(String id, String content, String comment_time, User user) {
        this.id = id;
        this.content = content;
        this.comment_time = comment_time;
        this.user = user;
    }
}
