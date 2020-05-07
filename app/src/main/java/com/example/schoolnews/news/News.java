
package com.example.schoolnews.news;

import java.util.Date;
import java.util.List;

public class News {
    private String user_id;
    private String news_name;
    private String news_text;
    private String news_image;
    private List<String> newsImages;
    private Date timestamp;

    public News(){
    }

    public News(String user_id, String news_name, String news_text, List<String> newsImages, Date timestamp, String news_image) {
        this.user_id = user_id;
        this.news_name = news_name;
        this.news_text = news_text;
        this.newsImages = newsImages;
        this.timestamp = timestamp;
        this.news_image = news_image;
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

    public List<String> getNewsImages() {
        return newsImages;
    }
}