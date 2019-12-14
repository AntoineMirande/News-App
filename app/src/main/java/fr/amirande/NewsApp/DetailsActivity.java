package fr.amirande.NewsApp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

        TextView title = findViewById(R.id.title);
        title.setText(article.getTitle());
        TextView date = findViewById(R.id.date);
        date.setText(article.getPublishedAt());
        TextView source = findViewById(R.id.source);
        source.setText(article.getSource().getName());
        TextView desc = findViewById(R.id.description);
        desc.setText(article.getDescription());
        TextView author = findViewById(R.id.author);
        author.setText(article.getAuthor());
        String imageUrl = article.getUrlToImage();
        ImageView imageView = (ImageView)findViewById(R.id.image);
        Picasso.with(this).load(imageUrl).into(imageView);

        Button btnClose = (Button) findViewById(R.id.close);
        Button btnUrl = (Button) findViewById(R.id.openurl);

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnUrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailsActivity.this, WebViewActivity.class);
                intent.putExtra("url", article.getUrl());
                startActivity(intent);
            }
        });
    }
}