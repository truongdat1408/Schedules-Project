package com.huy3999.schedules.model;

import java.util.ArrayList;

public class CreateProjectInfo {
    public final String name;
    public final String color;
    public final ArrayList<String> member;

    public CreateProjectInfo(String name, String color, ArrayList<String> member) {
        this.name = name;
        this.color = color;
        this.member = member;
    }
}
