package com.example.retrofit.Interface;

import com.example.retrofit.Model.Post;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface JsonApi {

    @GET("posts") // Metodo con parte de la URL final
    Call<List<Post>> getPosts();// Invocación al metodo retrofic que envia un request a un webserver y retorna una respuesta

}
