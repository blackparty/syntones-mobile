package com.syntones.response;

import com.syntones.model.Message;
import com.syntones.model.Playlist;

import java.util.List;


public class PlaylistSongsResponse {
	private Playlist playlist;
	private Message message;

	public PlaylistSongsResponse() {
	}

	public Playlist getPlaylist() {
		return playlist;
	}

	public void setPlaylist(Playlist playlist) {
		this.playlist = playlist;
	}

	public Message getMessage() {
		return message;
	}

	public void setMessage(Message message) {
		this.message = message;
	}
	
}
