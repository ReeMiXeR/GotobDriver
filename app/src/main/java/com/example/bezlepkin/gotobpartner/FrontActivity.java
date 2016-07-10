package com.example.bezlepkin.gotobpartner;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;

import javax.net.ssl.HttpsURLConnection;


public class FrontActivity extends AppCompatActivity {
    final static String LOG_TAG = FrontActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //заггрузка токена из настроек, если нету - возвращает null
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String tokenPref = preferences.getString("token", null);

        //тестовый запрос на сервер с токеном
        Boolean checkAnswer = null;
        testRequest task = new testRequest();
        try {
            checkAnswer = task.execute(tokenPref).get();
        }catch (InterruptedException | ExecutionException e){
            Log.e(LOG_TAG, "IEException: ", e);
        }
        //если токен прошел проверка - переход на DriverActivity
        if (checkAnswer == true){
            Intent intent = new Intent(this, DriverActivity.class);
            //startActivity(intent);
        }

        setContentView(R.layout.front_activity);
    }

    //обработчик нажатий кнопок в FrontActivity(переход на LoginActivity/сайт регистрации)(привязан к кнопке через XML front_activity)
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

    //класс для отправки запроса на сервер, на вход - токен, на выходе true/false
    private class testRequest extends AsyncTask<String, Void, Boolean>{
        @Override
        protected Boolean doInBackground(String... strings) {

            HttpURLConnection connection = null;
            Boolean taskResponse = null;
            if (strings[0] == null){
                return taskResponse = false;
            }
            try{
                URL url = new URL("http://api.busride.ru/v1/auth/check");
                //открытие подключения
                connection = (HttpURLConnection) url.openConnection();
                connection.setReadTimeout(10000);
                connection.setConnectTimeout(15000);
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                //добавление токена в заголовок
                connection.setRequestProperty("Authorization", "Bearer "+ strings[0]);
                connection.setRequestProperty("cache-control", "no-cache");
                connection.setRequestProperty("postman-token", "22794fec-c24d-e051-2f72-004eb45cae9e");

                //проверка кода ответа
                int responseCode = connection.getResponseCode();
                Log.e(LOG_TAG, "Response code - " + responseCode);
                if (responseCode == HttpsURLConnection.HTTP_OK)
                    taskResponse = true;
                else
                    taskResponse = false;

            }catch (IOException e){
                Log.e(LOG_TAG, "IOException: ", e);
            }finally {
                if (connection != null){
                    connection.disconnect();
                }
                return taskResponse;
            }

        }
    }
}
