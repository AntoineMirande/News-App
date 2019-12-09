package fr.amirande.NewsApp;

import fr.amirande.NewsApp.Models.News;
import fr.amirande.NewsApp.Models.Sources;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiInterface {

    @GET("everything")
    Call<News> getNews(
            @Query("language") String language,
            @Query("apiKey") String apiKey,
            @Query("sources") String sources
    );

    @GET("sources")
    Call<Sources> getSources(
            @Query("language") String language,
            @Query("apiKey") String apiKey
    );
}
