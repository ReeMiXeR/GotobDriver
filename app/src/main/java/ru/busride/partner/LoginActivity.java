package ru.busride.partner;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;


public class LoginActivity extends AppCompatActivity {
    final static String LOG_TAG = LoginActivity.class.getSimpleName();
    private String login_str = null;
    private String password_str = null;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    //обработчик нажатий кнопок на LoginActivity, привязан через XML файл login_activity
    public void buttonClickedLogin(View v){
        if (v.getId() == R.id.button_login){
            //получение введенного пароля и логина в string из view
            EditText login_view = (EditText) findViewById(R.id.login_text);
            login_str = login_view.getText().toString();

            EditText password_view = (EditText) findViewById(R.id.password_text);
            password_str = password_view.getText().toString();

            //отправка данных на сервер и получение ответа
            AsyncTaskRunner task = new AsyncTaskRunner();
            String[] strDriverData = new String[6];

            try {
                strDriverData = task.execute("a").get();
            }catch (Exception e) {
                Log.e(LOG_TAG, "Error: ", e);
            }

            if (strDriverData != null) {
                //сохранение токена и ИД водителя в настройках телефона
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("token", strDriverData[1]);
                editor.putString("id", strDriverData[0]);
                editor.apply();

                //запуск NavigationActivity
                Intent intent = new Intent(this, NavigationActivity.class);
                startActivity(intent);
            }
            else{
                new AlertDialog.Builder(this)
                        .setTitle("Ошибка")
                        .setMessage("Введен неверный логин или пароль.")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // continue with delete
                            }
                        })
                        .show();
            }

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }

    private class AsyncTaskRunner extends AsyncTask<String, Void, String[]> {
        final String LOG_TAG = AsyncTaskRunner.class.getSimpleName();
        final static String postKey1 = "phone";
        final static String postKey2 = "password";
        URL url = null;
        String response = "";
        final String jsonRequestParams = "request_params";
        final String jsonId = "id";
        final String jsonAccessToken = "access_token";
        final String jsonPhone = "phone";
        final String jsonFirstName = "first_name";
        final String jsonLastName = "last_name";
        final String jsonDriverType = "driver_type";
        final String jsonErrorCode = "error_code";
        private String[] strResponseArray = new String[6];


        protected String[] getDataFromJson(String responseString){
            //получение String[] из JSON
            try {
                JSONObject responseObject = new JSONObject(responseString);
                JSONObject jsonReqParamObj = responseObject.getJSONObject(jsonRequestParams);
                strResponseArray[0] = jsonReqParamObj.optString(jsonId);
                strResponseArray[1] = jsonReqParamObj.optString(jsonAccessToken);
                strResponseArray[2] = jsonReqParamObj.optString(jsonPhone);
                strResponseArray[3] = jsonReqParamObj.optString(jsonFirstName);
                strResponseArray[4] = jsonReqParamObj.optString(jsonLastName);
                strResponseArray[5] = jsonReqParamObj.optString(jsonDriverType);
            }catch (JSONException e){
                Log.e(LOG_TAG, "JSON exception: ", e);
            }
            return strResponseArray;
        }

        protected Boolean responseCheck(String response){
            try {
                JSONObject responseObject = new JSONObject(response);
                int responseCode = responseObject.getInt(jsonErrorCode);
                if (responseCode != 200)
                    return false;
                else
                    return true;
            } catch (JSONException e){
                Log.e(LOG_TAG, "response check error: ", e);
            }
            return true;
        }

        protected String[] doInBackground(String... params) {
            HttpURLConnection connection = null;
            try{
                Thread.sleep(1500);
                url = new URL("http://api.gotob.by/v1/auth/driver");
                //установка соединения
                connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(15000);
                connection.setReadTimeout(10000);
                connection.setRequestMethod("POST");
                connection.setDoInput(true);
                connection.setDoOutput(true);

                //добавление логина и пароля в запрос
                Uri.Builder builder = new Uri.Builder()
                        .appendQueryParameter(postKey1, "+79265400077")
                        .appendQueryParameter(postKey2, "1234");
                String query = builder.build().getEncodedQuery();
                //отправка лоигна и пароля
                OutputStream os = connection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(query);
                writer.flush();
                writer.close();
                os.close();

                int responseCode = connection.getResponseCode();
                //чтение входящего потока в случае успешного подключения
                if (responseCode == HttpsURLConnection.HTTP_OK) {
                    String line;
                    BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    while ((line=br.readLine()) != null) {
                        response +=line;
                    }
                    br.close();
                }
                boolean errorCode = responseCheck(response);
                if (errorCode != true){
                    return null;
                }
                else{
                //возвращаем полученные данные в String
                    return getDataFromJson(response);
                }
            }catch (Exception e){
                Log.e(LOG_TAG, "Error: ", e);
            }finally {
                if (connection!= null)
                    connection.disconnect();
            }
            return null;
        }

    }
}