package com.syntones.response;


import com.syntones.model.Message;

public class LogoutResponse {
    private Message message;


    public LogoutResponse() {
        super();
    }

    public LogoutResponse(Message message) {
        super();
        this.message = message;
    }

    public Message getMessage() {
        return message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }


}
