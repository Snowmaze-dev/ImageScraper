package com.example.imagescraper;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity implements  View.OnClickListener {

    Button butt;
    ProgressBar progress;
    EditText ed;
    File s;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        butt = findViewById(R.id.butt);
        ed = findViewById(R.id.ed);
        progress = findViewById(R.id.progress);
        butt.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        String link = ed.getText().toString();
        if (Patterns.WEB_URL.matcher(link).matches()) {
            new Scrap().execute(link);
        }
        else {
            Toast.makeText(getApplicationContext(), "Неправильная ссылка", Toast.LENGTH_LONG).show();
        }
    }

    private class Scrap extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            progress.setVisibility(View.VISIBLE);
            butt.setVisibility(View.GONE);
        }

        @Override
        protected String doInBackground(String... strings) {
            String html = "";
            try {
                URL url = null;
                url = new URL(strings[0]);
                URLConnection con1 = url.openConnection();
                BufferedReader reader = new BufferedReader(new InputStreamReader(con1.getInputStream()));
                String line = "";
                int i = 0;
                File directory = new File(Environment.getExternalStorageDirectory(), "MyPhotosApp");
                if (!directory.exists()) {
                    directory.mkdirs();
                }
                while(true) {
                    i++;
                    s = new File(directory, String.valueOf(i));
                    if (!s.exists()) {
                        s.mkdirs();
                        break;
                    }
                }
                while ((line = reader.readLine()) != null) {
                    html += line;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return html;
        }

        @Override
        protected void onPostExecute(String s) {

            File directory = new File(Environment.getExternalStorageDirectory(), "MyPhotosApp");
            String regex = "http(s?)://([\\w-]+\\.)+[\\w-]+(/[\\w- ./]*)+\\.(?:[gG][iI][fF]|[jJ][pP][gG]|[jJ][pP][eE][gG]|[pP][nN][gG]|[bB][mM][pP])";
            Matcher m = Pattern.compile(regex).matcher(s);
            if (!directory.exists()) {
                directory.mkdirs();
            }
                    String c = "";
                        while(m.find()) {
                        new download().execute(s.substring(m.start(), m.end()));
                }
            progress.setVisibility(View.GONE);
            butt.setVisibility(View.VISIBLE);
        }
    }
private class download extends AsyncTask < String, Void, Bitmap > {

    @Override
    protected Bitmap doInBackground(String...strings) {
        Bitmap result = null;
        try {
            URL url = new URL(strings[0]);
            result = BitmapFactory.decodeStream(url.openStream());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
    @Override
    protected void onPostExecute(Bitmap bitmap) {

        if (bitmap == null) {
            Toast.makeText(getApplicationContext(), "Что-то пошло не так", Toast.LENGTH_LONG).show();
        } else {
            try {
                int i = 0;
                File outputFile;
                while(true) {
                    i++;
                    outputFile = new File(s, i + ".jpg");
                    if (!outputFile.isFile()) {
                        outputFile.createNewFile();
                        break;
                    }
                }
                FileOutputStream fos = new FileOutputStream(outputFile);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.close();
            } catch (FileNotFoundException e) {
                Log.e("MyActivity", "Ошибка1");
            } catch (IOException e) {
                Log.e("MyActivity","Ошибка2");
            }
        }
    }
}
}