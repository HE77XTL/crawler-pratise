package com.github.hextl.entity;

import java.time.Instant;

public class Link {
    Integer id;
    String url;
    Integer status;
    Instant create_time;
    Instant modify_time;

    public Link(Integer id, int status) {
        this.id = id;
        this.status = status;
    }

    public Link(String url, int status) {
        this.url = url;
        this.status = status;
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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
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


