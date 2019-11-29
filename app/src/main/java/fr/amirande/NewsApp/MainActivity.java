package fr.amirande.NewsApp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import fr.amirande.NewsApp.Models.Articles;
import fr.amirande.NewsApp.Models.News;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    final String API_KEY = "d31f5fa5f03443dd8a1b9e3fde92ec34";
    Adapter adapter;
    List<Articles> articles = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        final String language = getLanguage();
        retrieveJson(language, API_KEY);
    }

    public void retrieveJson(String language, final String apiKey){

        String sources = "google-news-fr";
        Call<News> call = ApiClient.getInstance().getApi().getNews(language, apiKey, sources);
        call.enqueue(new Callback<News>() {
            @Override
            public void onResponse(Call<News> call, Response<News> response) {
                Log.d("debug", new Gson().toJson(response));
                if (response.isSuccessful() && response.body().getArticles() != null) {
                    articles.clear();
                    articles = response.body().getArticles();
                    adapter = new Adapter(MainActivity.this, articles);
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<News> call, Throwable t) {
                Log.d("debug", new Gson().toJson(t));
                Toast.makeText(MainActivity.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    public String getLanguage(){
        Locale locale = Locale.getDefault();
        String language = locale.getLanguage();
        return language.toLowerCase();
    }
}
