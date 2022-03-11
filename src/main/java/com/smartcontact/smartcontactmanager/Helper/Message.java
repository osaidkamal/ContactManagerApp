package com.smartcontact.smartcontactmanager.Helper;

public class Message {
    public static final String RecipientType = null;
    private String content;
    private String type;
    public Message(String content, String type) {
        this.content = content;
        this.type = type;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    
}
