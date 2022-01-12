package com.github.hextl.entity;

import java.time.Instant;

public class News {
    Integer id;
    String url;
    String title;
    String content;
    Instant create_time;
    Instant modify_time;

    public News(String url, String title, String content, Instant create_time, Instant modify_time) {
        this.url = url;
        this.title = title;
        this.content = content;
        this.create_time = create_time;
        this.modify_time = modify_time;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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

    public Instant getCreate_time() {
        return create_time;
    }

    public void setCreate_time(Instant create_time) {
        this.create_time = create_time;
    }

    public Instant getModify_time() {
        return modify_time;
    }

    public void setModify_time(Instant modify_time) {
        this.modify_time = modify_time;
    }
}
