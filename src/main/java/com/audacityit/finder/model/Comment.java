package com.audacityit.finder.model;

/**
 * @author Audacity IT Solutions Ltd.
 * @class Comment
 * @brief data structure class used by DetailViewFragment
 */

public class Comment {

    private String userName;
    private float rating;
    private String textComment;
    private String date;

    /**
     * @brief default constructor
     */
    public Comment() {
    }

    /**
     * @brief constructor with parameter
     * @param userName name of the commenter
     * @param rating rating point
     * @param textComment user comment
     * @param date date of comment
     */
    public Comment(String userName, float rating, String textComment, String date) {
        this.userName = userName;
        this.rating = rating;
        this.textComment = textComment;
        this.date = date;
    }

    /**
     * @brief get user name of the commenter
     * @return userName in String
     */
    public String getUserName() {
        return userName;
    }

    /**
     * @brief set user name of the commenter
     * @param userName in String
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * @brief get rating of the commenter
     * @return rating in float
     */
    public float getRating() {
        return rating;
    }

    /**
     * @brief set rating of the commenter
     * @param rating in float
     */
    public void setRating(float rating) {
        this.rating = rating;
    }

    /**
     * @brief get comment of the commenter
     * @return comment in String
     */
    public String getText() {
        return textComment;
    }

    /**
     * @brief set comment of the commenter
     * @param text in String
     */
    public void setText(String text) {
        this.textComment = text;
    }

    /**
     * @brief get date of the comment
     * @return date in String
     */
    public String getDate() {
        return date;
    }

    /**
     * @brief set date of the comment
     * @param date in String
     */
    public void setDate(String date) {
        this.date = date;
    }
}
