package com.example.bestphotocollections.Model;

import java.util.ArrayList;

public class ItemGroup {
    private String Name;
    private String uid;
    private ArrayList<ItemData> item_list;
    String Uri;
    String Metadata;

    public String getUri() {
        return Uri;
    }

    public void setUri(String uri) {
        Uri = uri;
    }

    public String getMetadata() {
        return Metadata;
    }

    public void setMetadata(String metadata) {
        Metadata = metadata;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setName(String name) {
        Name = name;
    }

    public void setItem_list(ArrayList<ItemData> item_list) {
        this.item_list = item_list;
    }

    public String getName() {
        return Name;
    }

    public ArrayList<ItemData> getItem_list() {
        return item_list;
    }
}


