package com.syntones.response;


import com.syntones.model.Message;

public class RemoveToPlaylistResponse {
	private Message message;
	
	
	public RemoveToPlaylistResponse(){
	}


	public Message getMessage() {
		return message;
	}


	public void setMessage(Message message) {
		this.message = message;
	}
	
}
