package com.syntones.model;

import java.util.List;

public class UserTransaction {
	
	private User user;
	private List<Song> playedSong;
	private int counter;
	private String sessionId; 
	
	public UserTransaction(){
		
	}
	
	public UserTransaction(User user,List<Song> playedSong,int counter){
		this.user = user;
		this.playedSong = playedSong;
		this.counter = counter;
	}
	public int getCounter(){
		return counter;
	}
	public void setCounter(int counter){
		this.counter = counter;
	}
	
	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public List<Song> getPlayedSong() {
		return playedSong;
	}
	public void setPlayedSong(List<Song> playedSong) {
		this.playedSong = playedSong;
	}
	
}
