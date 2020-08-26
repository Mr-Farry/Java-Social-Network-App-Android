package com.farry.socialapp.models;

import java.io.Serializable;

public class ModelVoiceCall implements Serializable
{
    String caller,reciever;

    public ModelVoiceCall() {
    }

    public String getCaller() {
        return caller;
    }

    public void setCaller(String caller) {
        this.caller = caller;
    }

    public String getReciever() {
        return reciever;
    }

    public void setReciever(String reciever) {
        this.reciever = reciever;
    }
}
