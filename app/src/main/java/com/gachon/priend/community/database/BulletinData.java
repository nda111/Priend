package com.gachon.priend.community.database;

public class BulletinData {
    private short id;
    private String title;
    private String content;
    private int resId;

    public short getId() {
        return id;
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

    public void setId(short id) {
        this.id = id;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getResId() {
        return resId;
    }

    public void setResId(int resId) {
        this.resId = resId;
    }
}