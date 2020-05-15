package com.example.schoolnews.comments;

import java.util.Date;

public class Comment {
    private String comment, user_id;
    private Date timestamp;

    private Comment() {
    }

    public Comment(String comment, String user_id, Date timestamp) {
        this.comment = comment;
        this.user_id = user_id;
        this.timestamp = timestamp;
    }

    public String getComment() {
        return comment;
    }

    public String getUser_id() {
        return user_id;
    }

    public Date getTimestamp() {
        return timestamp;
    }
}