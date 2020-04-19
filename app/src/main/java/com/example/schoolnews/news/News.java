package com.example.schoolnews.news;

public class News {
    private String author_name;
    private String author_school;
    private String author_class_number;
    private String author_class_letter;
    private String news_name;
    private String news_text;

    public News(){
    }

    public News(String author_name, String author_school, String author_class_number, String author_class_letter, String news_name, String news_text){
        this.author_name = author_name;
        this.author_school = author_school;
        this.author_class_number = author_class_number;
        this.author_class_letter = author_class_letter;
        this.news_name = news_name;
        this.news_text = news_text;
    }

    public String getAuthor_name() {
        return author_name;
    }

    public String getAuthor_school() {
        return author_school;
    }

    public String getAuthor_class_number() {
        return author_class_number;
    }

    public String getAuthor_class_letter() {
        return author_class_letter;
    }

    public String getNews_name() {
        return news_name;
    }

    public String getNews_text() {
        return news_text;
    }
}
