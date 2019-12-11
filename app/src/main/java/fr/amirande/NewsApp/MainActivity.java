package fr.amirande.NewsApp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import fr.amirande.NewsApp.Models.Articles;
import fr.amirande.NewsApp.Models.News;
import fr.amirande.NewsApp.Models.Source;
import fr.amirande.NewsApp.Models.Sources;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    RecyclerView recyclerView;
    Adapter adapter;
    Spinner spinnerView;
    ArrayAdapter<String> sourceArrayAdapter;
    private OnClickInterface onclickInterface;
    final String language = getLanguage();
    final String API_KEY = "d31f5fa5f03443dd8a1b9e3fde92ec34";

    List<Source> sources = new ArrayList<>();
    List<String> sourcesNames = new ArrayList<>();
    List<Articles> articles = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (isNetworkConnected()) {
            spinnerView = findViewById(R.id.sources_spinner);
            spinnerView.setOnItemSelectedListener(this);
            retrieveSources(language, API_KEY);

            recyclerView = findViewById(R.id.recyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            onclickInterface = new OnClickInterface() {
                @Override
                public void setClick(Articles a) {
                    Log.d("debug", "setClick: " + a.getTitle());
                    Intent i = new Intent(MainActivity.this, DetailsActivity.class);
                    Gson gS = new Gson();
                    String target = gS.toJson(a);
                    i.putExtra("article", target);
                    startActivity(i);
                }
            };

            retrieveNews("google-news-fr", language, API_KEY);
        } else {
            try {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

                alertDialogBuilder.setTitle("Info");
                alertDialogBuilder.setMessage("Internet not available, Check your internet connectivity and try again");
                alertDialogBuilder.setIcon(android.R.drawable.ic_dialog_alert);
                alertDialogBuilder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            } catch (Exception e) {
                Log.d("debug", "Show Dialog: " + e.getMessage());
            }
        }
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        Source selectedItem = sources.get(pos);
        Log.d("debug", "onItemSelected: " + parent.getItemAtPosition(pos));
        retrieveNews(selectedItem.getId(), language, API_KEY);
    }

    public void onNothingSelected(AdapterView<?> adapterView) {}

    public void retrieveNews(String sources, String language, final String apiKey){

        Call<News> call = ApiClient.getInstance().getApi().getNews(language, apiKey, sources);
        call.enqueue(new Callback<News>() {
            @Override
            public void onResponse(Call<News> call, Response<News> response) {
                Log.d("debug", "news : " + new Gson().toJson(response));
                if (response.isSuccessful() && response.body().getArticles() != null) {
                    articles.clear();
                    articles = response.body().getArticles();
                    adapter = new Adapter(MainActivity.this, articles, onclickInterface);
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

    public void retrieveSources(String language, final String apiKey){

        Call<Sources> call = ApiClient.getInstance().getApi().getSources(language, apiKey);
        call.enqueue(new Callback<Sources>() {
            @Override
            public void onResponse(Call<Sources> call, Response<Sources> response) {
                Log.d("debug", "sources : " + new Gson().toJson(response));
                if (response.isSuccessful() && response.body().getSources() != null) {
                    sources.clear();
                    sources = response.body().getSources();
                    sourceArrayAdapter = new ArrayAdapter<String>(MainActivity.this,
                            R.layout.spinner_item, sourcesNames);
                    for (final Source source : sources) {
                        sourcesNames.add(source.getName());
                    }
                    Log.d("debug", "sources names: " + sourcesNames);
                    sourceArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerView.setAdapter(sourceArrayAdapter);
                }
            }

            @Override
            public void onFailure(Call<Sources> call, Throwable t) {
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
