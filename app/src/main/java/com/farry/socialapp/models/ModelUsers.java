package com.farry.socialapp.models;

public class ModelUsers {
    String name,phone,email,search,cover,uid,profile;
    public ModelUsers() {
    }

    public ModelUsers(String name, String phone, String profile, String email, String search, String cover,String uid) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.search = search;
        this.cover = cover;
        this.uid=uid;
        this.profile=profile;
    }


    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }
}
