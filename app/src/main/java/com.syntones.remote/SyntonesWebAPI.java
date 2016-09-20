package com.syntones.remote;

/**
 * Created by Courtney Love on 9/2/2016.
 */

import android.content.Context;

import com.syntones.model.Playlist;
import com.syntones.model.PlaylistSong;
import com.syntones.model.Song;
import com.syntones.model.User;
import com.syntones.response.LibraryResponse;
import com.syntones.response.LoginResponse;
import com.syntones.response.PlaylistResponse;
import com.syntones.response.PlaylistSongsResponse;
import com.syntones.response.RemovePlaylistResponse;
import com.syntones.response.RemoveToPlaylistResponse;
import com.syntones.response.SongListResponse;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface SyntonesWebAPI {

    String ENDPOINT = "http://10.0.2.2:8081/syntones-web/";

    @POST("register")
    Call<User> createUser(@Body User user);

    @POST("login")
    Call<LoginResponse> logInUser(@Body User user);

    @POST("savePlaylist")
    Call<PlaylistResponse> createPlaylist(@Body Playlist playlist);

    @POST("playlist")
    Call<PlaylistResponse> getPlaylistFromDB(@Body User user);

    @POST("removePlaylist")
    Call<RemovePlaylistResponse> removePlaylist(@Body Playlist playlist);

    @GET("songlist")
    Call<SongListResponse> getAllSongsFromDB();

    @POST("addToPlaylist")
    Call<LibraryResponse> addToPlaylist(@Body PlaylistSong playlistSong);

    @POST("playlistSong")
    Call<PlaylistSongsResponse> getSongsPlaylist(@Body Map<String, String> data);


    class Factory {

        private static SyntonesWebAPI service;

        public static SyntonesWebAPI getInstance(Context context) {

            if (service == null) {

                Retrofit retrofit = new Retrofit.Builder().addConverterFactory(GsonConverterFactory.create()).baseUrl(ENDPOINT).build();

                service = retrofit.create(SyntonesWebAPI.class);

                return service;
            } else {

                return service;
            }

        }
    }


}
