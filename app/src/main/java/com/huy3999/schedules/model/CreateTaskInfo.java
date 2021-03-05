package com.huy3999.schedules.model;

import java.util.ArrayList;

public class CreateTaskInfo {
    public String id;
    public final String name;
    public final String description;
    public final String state;
    public final String project_id;
    public final ArrayList<String> member;

    public CreateTaskInfo(String name, String description, String state, String project_id, ArrayList<String> member) {
        this.name = name;
        this.description = description;
        this.state = state;
        this.project_id = project_id;
        this.member = member;
    }
    public CreateTaskInfo(String id,String name, String description, String state, String project_id, ArrayList<String> member) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.state = state;
        this.project_id = project_id;
        this.member = member;
    }
}