package com.naimur978.forum.Models;

public class ModelChatlist {
    String id;//we'll need this id to get chat list, sender/receiver id

    public ModelChatlist(String id){
        this.id = id;
    }

    public ModelChatlist(){}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
