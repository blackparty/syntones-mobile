package com.syntones.response;

import com.syntones.model.Message;
import com.syntones.model.Tag;

import java.util.List;



public class TagsResponse {


	private Message message;
	private List<String> tags;


	public TagsResponse(){}


	public Message getMessage() {
		return message;
	}


	public void setMessage(Message message) {
		this.message = message;
	}


	public List<String> getTags() {
		return tags;
	}


	public void setTags(List<String> tags) {
		this.tags = tags;
	}




}
