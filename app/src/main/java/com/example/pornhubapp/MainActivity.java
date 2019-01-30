package com.example.pornhubapp;

import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    private String url = "https://www.pornhub.com/albums/female-straight-uncategorized?search=blue+hair";
    private String url2 = "https://www.pornhub.com/albums/female-straight-uncategorized?search=cosplay";
    String result;
    RecyclerView recyclerView;
    private LinearLayoutManager verticalLinearLayout;
    private LinearLayoutManager horizontalLinearLayout;
    private RecyclerView adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recyclerView);
        GridLayoutManager gridLayoutManager;
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            gridLayoutManager = new GridLayoutManager(this, 3);
        }else {
            gridLayoutManager = new GridLayoutManager(this, 2);
        }
        recyclerView.setLayoutManager(gridLayoutManager);

        DownloadTask downloadTask = new DownloadTask();
        try {
            result = downloadTask.execute(url2).get();
            Log.i("URL",  result);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ArrayList<ModelItem> girls = new ArrayList<>();


        Pattern patternName = Pattern.compile("<p class=\"[^\"]*?title[^\"]*?\">(.*?)</p>");
        Pattern patternImg = Pattern.compile("url\\('(.*?)'\\);\"");
        ArrayList<String> nameList = new ArrayList<>();
        ArrayList<String> imgList = new ArrayList<>();
        ArrayList<Integer> imgResource = new ArrayList<>();


        Matcher matcherImg = patternImg.matcher(result);
        Matcher matcherNames = patternName.matcher(result);

        while (matcherNames.find()) {
            Log.i("NamesG", matcherNames.group(1));
            nameList.add(matcherNames.group(1));
        }
        while (matcherImg.find()) {
            Log.i("Images", matcherImg.group(1));
            imgList.add(matcherImg.group(1));
        }
        Log.i("SearchImg", imgList.get(2));


//        for(int i = 0; i < imgList.size(); i++){
//            DownloadImage downloadImage = new DownloadImage();
//            Bitmap bitmap = null;
//            String url = imgList.get(i).toString();
//            try {
//                bitmap = downloadImage.execute(url).get();
//            } catch (ExecutionException e) {
//                e.printStackTrace();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            imgResource.add(bitmap);
//        }

        for(int i = 0; i < nameList.size(); i++){
            girls.add(new ModelItem(nameList.get(i), imgList.get(i)));
        }

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);


        AdapterRVA adapter = new AdapterRVA(girls);
        recyclerView.setAdapter(adapter);

    }


    public class AdapterRVA extends RecyclerView.Adapter<AdapterRVA.PornViewHolder>{

        List<ModelItem> girlsList;
        public AdapterRVA(List<ModelItem> girls){
            girlsList = girls;
        }
        @NonNull
        @Override
        public PornViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_view, viewGroup, false);
            PornViewHolder pornViewHolder = new PornViewHolder(v);
            return pornViewHolder;
        }



        @Override
        public void onBindViewHolder(@NonNull PornViewHolder pornViewHolder, int i) {

            DownloadImage downloadImage = new DownloadImage();
            Bitmap bitmap = null;
            String link = girlsList.get(i).getImgLink();
            try {
                bitmap = downloadImage.execute(link).get();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            pornViewHolder.imageView.setImageBitmap(bitmap);
            pornViewHolder.textView.setText(girlsList.get(i).getName());


        }

        @Override
        public int getItemCount() {
            if(girlsList.size() != 0){
            return girlsList.size();
            }return 0;
        }

        public class PornViewHolder extends RecyclerView.ViewHolder {

            CardView cv;
            ImageView imageView;
            TextView textView;

            public PornViewHolder(@NonNull View itemView) {
                super(itemView);
                cv = itemView.findViewById(R.id.card_view);
                imageView = itemView.findViewById(R.id.imageGirls);
                textView = itemView.findViewById(R.id.textView);
            }
        }
    }

    private static class DownloadImage extends AsyncTask<String, Void, Bitmap>{
        @Override
        protected Bitmap doInBackground(String... strings) {
            URL url = null;
            HttpURLConnection urlConnection = null;
            try {
                url = new URL(strings[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(in);
                return  bitmap;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if(urlConnection != null){
                    urlConnection.disconnect();

                }
            }
            return null;
        }
    }

    private static class DownloadTask extends AsyncTask<String, Void, String>{
        @Override
        protected String doInBackground(String... strings) {
            StringBuilder result = new StringBuilder();
            URL url = null;
            HttpURLConnection  urlConnection = null;
            try {
                url = new URL(strings[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(in);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String line = bufferedReader.readLine();
                while (line != null){
                    result.append(line);
                    line = bufferedReader.readLine();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                if(urlConnection != null){
                    urlConnection.disconnect();
                }
            }
            return result.toString();
        }
    }

}
