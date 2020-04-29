
package com.example.schoolnews.news;

import java.util.Date;

public class News {
    private String user_id;
    private String news_name;
    private String news_text;
    private String news_image;
    private Date timestamp;

    public News(){
    }

    public News(String user_id, String news_name, String news_text, String news_image, Date timestamp) {
        this.user_id = user_id;
        this.news_name = news_name;
        this.news_text = news_text;
        this.news_image = news_image;
        this.timestamp = timestamp;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getNews_name() {
        return news_name;
    }

    public String getNews_text() {
        return news_text;
    }

    public String getNews_image() {
        return news_image;
    }

    public Date getTimestamp() {
        return timestamp;
    }
}