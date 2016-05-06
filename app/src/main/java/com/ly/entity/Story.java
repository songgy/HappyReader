package com.ly.entity;

import java.io.Serializable;

/**
 * Created by ly on 2016/4/26.
 */
public class Story implements Serializable{
    private String id;
    private String title;
    private String content;
    private String story_img;
    private String story_time;
    private String like_count;
    private String comment_count;
    private User user;

    @Override
    public String toString() {
        return "Story{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", story_img='" + story_img + '\'' +
                ", story_time='" + story_time + '\'' +
                ", like_count='" + like_count + '\'' +
                ", comment_count='" + comment_count + '\'' +
                ", user=" + user +
                '}';
    }

    public Story() {
    }

    public Story(String id, String title, String content, String story_img, String story_time, String like_count, String comment_count, User user) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.story_img = story_img;
        this.story_time = story_time;
        this.like_count = like_count;
        this.comment_count = comment_count;
        this.user = user;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getStory_img() {
        return story_img;
    }

    public void setStory_img(String story_img) {
        this.story_img = story_img;
    }

    public String getStory_time() {
        return story_time;
    }

    public void setStory_time(String story_time) {
        this.story_time = story_time;
    }

    public String getLike_count() {
        return like_count;
    }

    public void setLike_count(String like_count) {
        this.like_count = like_count;
    }

    public String getComment_count() {
        return comment_count;
    }

    public void setComment_count(String comment_count) {
        this.comment_count = comment_count;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
