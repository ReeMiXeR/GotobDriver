package com.example.bezlepkin.gotobpartner;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Teacher on 27.06.2016.
 */
public class FrontActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        //String tokenPref = preferences.getString("token","none");
        //Log.e("WEEEEEE", tokenPref);
        //testRequest task = new testRequest();
        //task.execute(tokenPref);

        setContentView(R.layout.front_fragment);
/**
        if (savedInstanceState == null){
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new FrontFragment())
                    .commit();
        }**/
    }

    public boolean buttonClickMain (View v){
        switch (v.getId()){
            case R.id.login:
                Intent intent_login = new Intent(FrontActivity.this, LoginActivity.class);
                startActivity(intent_login);
                return true;
            case R.id.signup:
                Intent intent_singUp = new Intent();
                intent_singUp.setAction(Intent.ACTION_VIEW);
                intent_singUp.addCategory(Intent.CATEGORY_BROWSABLE);
                intent_singUp.setData(Uri.parse("http://partners.gotob.by"));
                startActivity(intent_singUp);
                return true;
        }
        return false;
    }

    private class testRequest extends AsyncTask<String, Void, String>{
        @Override
        protected String doInBackground(String... strings) {
            String response1 = "";

            try{
                URL url = new URL("http://api.gotob.by/v1/auth/check");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setReadTimeout(10000);
                connection.setConnectTimeout(15000);
                Log.e("0", "ok");
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);

                connection.setDoInput(true);
                connection.connect();
                //connection.setRequestProperty("Authorization:", "Bearer" + strings[0]);
                Log.e("1", "ok");
                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter("Authorization", "Bearer "+ strings[0]);

                String query = builder.build().getEncodedQuery();
                Log.e("2", "ok");
                OutputStream os = connection.getOutputStream();
                Log.e("3", "ok");
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                Log.e("4", "ok");
                writer.write(query);
                Log.e("5", "ok");
                writer.flush();
                Log.e("6", "ok");
                writer.close();
                os.close();
                Log.e("7", "ok");
                int responseCode = connection.getResponseCode();
                Log.e("8", responseCode + " - " + "ok");
                if (responseCode == HttpsURLConnection.HTTP_OK) {
                    String line;
                    BufferedReader br=new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    Log.e("9", "ok");
                    while ((line=br.readLine()) != null) {
                        response1+=line;
                    }
                }

                Log.e("10","XXXX" + response1);
                connection.connect();

            }catch (IOException e){
                e.printStackTrace();
            }
            String ok = "ok";
            return ok;
        }
    }
}
