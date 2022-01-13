package com.github.hextl.entity;

import java.util.List;

public class PageData {
     List<String> hrefList;
     String title;
     String content;

    public PageData(List<String> hrefList, String title, String content) {
        this.hrefList = hrefList;
        this.title = title;
        this.content = content;
    }

    public List<String> getHrefList() {
        return hrefList;
    }

    public void setHrefList(List<String> hrefList) {
        this.hrefList = hrefList;
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
}
