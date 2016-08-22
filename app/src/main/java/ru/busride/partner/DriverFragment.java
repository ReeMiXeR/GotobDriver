package ru.busride.partner;

import android.support.v4.app.Fragment;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

public class DriverFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    String[][] strTripData = new String[20][];
    final static String LOG_TAG = DriverFragment.class.getSimpleName();
    ArrayList<TripsInfo> tripsInfoArray = new ArrayList<TripsInfo>();
    ListAdapter adapter;
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(true);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String id = sharedPreferences.getString("id", null);

        //создаем объект asynctask и получаем из него string[][] инфомации о поездках
        DriverTripTask task = new DriverTripTask();

        try {
            strTripData = task.execute(id).get();
        }catch (Exception e){
            e.printStackTrace();
        }

        tripsInfoArray.clear();

        for(int i = 0; i < 8; i++){
            tripsInfoArray.add(new TripsInfo(strTripData, i));
        }

        adapter.notifyDataSetChanged();
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String id = sharedPreferences.getString("id", null);

        //создаем объект asynctask и получаем из него string[][] инфомации о поездках
        DriverTripTask task = new DriverTripTask();
        try {
            strTripData = task.execute(id).get();
        }catch (Exception e){
            e.printStackTrace();
        }

        for(int i = 0; i < 8; i++){
            tripsInfoArray.add(new TripsInfo(strTripData, i));
        }

        View rootView = inflater.inflate(R.layout.driver_fragment, container, false);
        adapter = new ListAdapter(getActivity(), tripsInfoArray);

        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);

        ListView listView = (ListView) rootView.findViewById(R.id.driver_list_view);
        listView.setAdapter(adapter);

        return rootView;
    }

    private class DriverTripTask extends AsyncTask <String, Void, String[][]>{
        final String LOG_TAG = DriverTripTask.class.getSimpleName();
        final String jsonTripId = "id";
        final String jsonDepartDate = "departure_date";
        final String jsonArriveDate = "arrival_date";
        final String jsonSeats = "seats";
        final String jsonDepartPoint = "departure_point";
        final String jsonArrivalPoint = "arrival_point";
        final String jsonPrice = "price";


        protected String[][] getDataFromJson(String responseString) throws JSONException{
            JSONArray jsonArray = new JSONArray(responseString);
            String[][] dataArray = new String[10][10];
            for (int i = 0; i < jsonArray.length(); i++){
                JSONObject jsonTripObj = jsonArray.getJSONObject(i);
                dataArray[i][0] = jsonTripObj.optString(jsonTripId, "null");
                dataArray[i][1] = jsonTripObj.optString(jsonDepartDate,  "null");
                dataArray[i][2] = jsonTripObj.optString(jsonArriveDate,  "null");
                dataArray[i][3] = jsonTripObj.optString(jsonDepartPoint,  "null");
                dataArray[i][4] = jsonTripObj.optString(jsonArrivalPoint,  "null");
                dataArray[i][5] = jsonTripObj.optString(jsonSeats, "null");
                dataArray[i][6] = jsonTripObj.optString(jsonPrice, "null");
                }
            return dataArray;

        }
        @Override
        protected String[][] doInBackground(String... strings) {
            final String strUrl = "http://api.busride.ru/v1/trips?driver_id=";
            String strResponse = "";
            String strId = strings[0];
            HttpURLConnection connection = null;

            //проверка на передачу id в asynctask
            if(strId == null){
                return null;
            }

            try {
                URL url = new URL(strUrl + strId);
                //открываем соединение
                connection = (HttpURLConnection) url.openConnection();
                connection.setReadTimeout(10000);
                connection.setConnectTimeout(15000);
                connection.setRequestMethod("GET");
                connection.setDoInput(true);

                int responseCode = connection.getResponseCode();
                Log.e(LOG_TAG, "Response code - " + responseCode);
                //при удачном подключении записиь ответа в strResponse
                if (responseCode == HttpsURLConnection.HTTP_OK) {
                    String line;
                    BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    while ((line=br.readLine()) != null) {
                        strResponse+=line;
                    }
                    br.close();
                }

                return getDataFromJson(strResponse);
            }catch(Exception e){
                Log.e(LOG_TAG, "Error: ", e);
            }finally{
                if (connection != null)
                    connection.disconnect();
            }
            return null;
        }
    }


    public class TripsInfo{
        //класс для загрузки информации о рейсе в listView с помощью listAdapter
        String id;
        String departure_time;
        String arrival_time;
        String depart_point;
        String arrival_point;
        String seats;
        String price;

        TripsInfo(String[][] strTripData, int num){
            id = strTripData[num][0];
            departure_time = strTripData[num][1];
            arrival_time = strTripData[num][2];
            depart_point = strTripData[num][3];
            arrival_point = strTripData[num][4];
            seats = strTripData[num][5];
            price = strTripData[num][6];
        }
    }
}
