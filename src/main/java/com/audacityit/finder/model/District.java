package com.audacityit.finder.model;

/**
 * @author Audacity IT Solutions Ltd.
 * @class District
 * @brief data structure class used by HomeActivity
 */

public class District {

    private String id;
    private String title;

    /**
     * @brief default constructor
     * @param id district id
     * @param title district title
     */
    public District(String id, String title) {
        this.id = id;
        this.title = title;
    }

    /**
     * @brief get id of district
     * @return id in String
     */
    public String getId() {
        return id;
    }

    /**
     * @brief get title of district
     * @return title in String
     */
    public String getTitle() {
        return title;
    }
}
