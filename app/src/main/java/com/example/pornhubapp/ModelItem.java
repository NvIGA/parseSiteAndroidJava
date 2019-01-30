package com.example.pornhubapp;

class ModelItem {
    private String name;
    private String link;

    ModelItem(String name, String link) {
        this.name = name;
        this.link = link;
    }

    public String getName() {
        return name;
    }

    public String getImgLink() {
        return link;
    }

    @Override
    public String toString() {
        return name;
    }
}
