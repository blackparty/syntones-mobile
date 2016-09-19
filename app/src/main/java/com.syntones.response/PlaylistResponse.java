package com.syntones.response;

import com.syntones.model.Message;
import com.syntones.model.Playlist;

import java.util.List;




public class PlaylistResponse {
	private Message message;
	private List<Playlist> playlists;
	
	
	public PlaylistResponse(){}
	public PlaylistResponse(List<Playlist> playlists){
		this.playlists = playlists;
	}
	public PlaylistResponse(List<Playlist> playlists, Message message){
		this.playlists = playlists;
		this.message = message;
	}
	public Message getMessage() {
		return message;
	}
	public void setMessage(Message message) {
		this.message = message;
	}
	public List<Playlist> getPlaylists() {
		return playlists;
	}
	public void setPlaylists(List<Playlist> playlists) {
		this.playlists = playlists;
	}
	
}
