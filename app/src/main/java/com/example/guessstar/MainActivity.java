package com.example.guessstar;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    private Button button0;
    private Button button1;
    private Button button2;
    private Button button3;
    private ImageView imageViewStar;

    private String url = "https://www.imdb.com/list/ls045252306/";
    private ArrayList<String> urls;
    private ArrayList<String> names;
    private ArrayList<Button> buttons;
    private int numberOfOuestion;
    private int numberOfRightAnswer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button0 = findViewById(R.id.button0);
        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
        button3 = findViewById(R.id.button3);
        imageViewStar = findViewById(R.id.imageViewStar);
        urls = new ArrayList<String>();
        names = new ArrayList<String>();
        buttons = new ArrayList<Button>();
        buttons.add(button0);
        buttons.add(button1);
        buttons.add(button2);
        buttons.add(button3);
        Pattern patternImage = Pattern.compile("height=\"209\"src=\"(.*?)\"width=\"140\" />");
        Pattern patternName = Pattern.compile("<img alt=\"(.*?)\"");
        Matcher matcherImg = patternImage.matcher(getContect());
        Matcher matcherName = patternName.matcher(getContect());
        while (matcherImg.find()) {
            urls.add(matcherImg.group(1));
        }
        while (matcherName.find()) {
            names.add(matcherName.group(1));
        }
        for (String name : urls) {
            Log.i("Name", name);
        }
        playGame();
    }

    private void playGame() {
        generateQuestion();
        Picasso.with(this).load(urls.get(numberOfOuestion)).into(imageViewStar);
        for (int i = 0; i < buttons.size(); i++) {
            if (i == numberOfRightAnswer) {
                buttons.get(i).setText(names.get(numberOfOuestion));
            } else {
                int wrongAnswer = generateWrongAnswer();
                buttons.get(i).setText(names.get(wrongAnswer));
            }
        }

    }

    private void generateQuestion() {
        numberOfOuestion = (int) (Math.random() * names.size());
        numberOfRightAnswer = (int) (Math.random() * buttons.size());
    }

    private int generateWrongAnswer() {
        return (int) (Math.random() * names.size());
    }

    public void onClickAnswer(View view) {
        Button button = (Button) view;
        String tag = button.getTag().toString();
        if (Integer.parseInt(tag) == numberOfRightAnswer) {
            Toast.makeText(this, "Вірно!", Toast.LENGTH_SHORT).show();
        } else
            Toast.makeText(this, "Невірно, правильна відповідь: " + names.get(numberOfOuestion), Toast.LENGTH_SHORT).show();
        playGame();
    }

    private static class DownloadContentTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            StringBuilder result = new StringBuilder();
            URL url = null;
            HttpURLConnection urlConnection = null;
            try {
                url = new URL(strings[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                BufferedReader bufferedReader = new BufferedReader(reader);
                String line = bufferedReader.readLine();
                while (line != null) {
                    result.append(line);
                    line = bufferedReader.readLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null)
                    urlConnection.disconnect();
            }
            return result.toString();
        }
    }

    private String getContect() {
        DownloadContentTask task = new DownloadContentTask();
        try {
            String result = task.execute(url).get();
            Log.i("MyResult", result);
            return result;
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
