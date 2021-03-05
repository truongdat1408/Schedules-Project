package com.huy3999.schedules.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Objects;

public class Project implements Parcelable {
    @SerializedName("id")
    public final String id;
    @SerializedName("name")
    public final String name;
    @SerializedName("color")
    public final String color;
    @SerializedName("member")
    public final ArrayList<String> member;

    public Project(String id, String name, String color, ArrayList<String> member) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.member = member;
    }

    protected Project(Parcel in) {
        id = in.readString();
        name = in.readString();
        color = in.readString();
        member = in.createStringArrayList();
    }

    public static final Creator<Project> CREATOR = new Creator<Project>() {
        @Override
        public Project createFromParcel(Parcel in) {
            return new Project(in);
        }

        @Override
        public Project[] newArray(int size) {
            return new Project[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int i) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(color);
        dest.writeStringList(member);
    }

    @Override
    public boolean equals(Object object) {
        boolean isEqual= false;
        if (object != null && object instanceof Project)
        {
            isEqual = (this.id == ((Project) object).id);
        }
        return isEqual;
    }
}
