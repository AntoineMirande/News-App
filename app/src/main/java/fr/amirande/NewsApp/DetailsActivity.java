package fr.amirande.NewsApp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import fr.amirande.NewsApp.Models.Articles;

public class DetailsActivity extends AppCompatActivity {
    Articles article;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        Intent intent = getIntent();
        String target = intent.getStringExtra("article");
        Gson gS = new Gson();
        article = gS.fromJson(target, Articles.class);

        TextView title = findViewById(R.id.articleTitle);
        title.setText(article.getTitle());
        TextView date = findViewById(R.id.articleDate);
        date.setText(article.getPublishedAt());
        String imageUrl = article.getUrlToImage();
        ImageView imageView = (ImageView)findViewById(R.id.image);
        Picasso.with(this).load(imageUrl).into(imageView);
    }
}