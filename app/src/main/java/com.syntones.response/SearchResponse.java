package com.syntones.response;

import com.syntones.model.Message;
import com.syntones.model.Song;

import java.util.List;


public class SearchResponse {
	private Message message;
	private List<Song> allSongsInTheDB;
	
	public SearchResponse() {
	}
	public SearchResponse(Message message) {
		this.message = message;
	}

	
	
	
	public List<Song> getAllSongsInTheDB() {
		return allSongsInTheDB;
	}
	public void setAllSongsInTheDB(List<Song> allSongsInTheDB) {
		this.allSongsInTheDB = allSongsInTheDB;
	}
	public Message getMessage() {
		return message;
	}

	public void setMessage(Message message) {
		this.message = message;
	}
}
