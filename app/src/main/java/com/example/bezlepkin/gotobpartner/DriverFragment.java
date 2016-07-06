package com.example.bezlepkin.gotobpartner;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
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

/**
 * Created by Teacher on 02.07.2016.
 */
public class DriverFragment extends Fragment {
    String[][] strTripData = new String[10][];
    final static String LOG_TAG = DriverFragment.class.getSimpleName();
    ArrayList<TripsInfo> tripsInfos = new ArrayList<TripsInfo>();
    ListAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //String strDriverData = getActivity().getIntent().getExtras().getString("strDriverData");
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String id = sharedPreferences.getString("id", "null");
        Log.e(LOG_TAG, "id - " + id);


        RoadTripTask task = new RoadTripTask();
        try {
            strTripData = task.execute(id).get();
        }catch (Exception e){
            e.printStackTrace();
        }

        String _id;
        String _departure_time;
        String _arrival_time;
        String _depart_point;
        String _arrival_point;
        String _seats;
        String _price;

        for(int i = 0; i < 8; i++){
            Log.e(LOG_TAG, "1 - ok");
            _id = strTripData[i][0];
            _departure_time = strTripData[i][1];
            _arrival_time = strTripData[i][2];
            _depart_point = strTripData[i][3];
            _arrival_point = strTripData[i][4];
            _seats = strTripData[i][5];
            _price = strTripData[i][6];

            Log.e(LOG_TAG, "2 - ok");
            tripsInfos.add(new TripsInfo(_id, _departure_time, _arrival_time, _depart_point, _arrival_point, _seats, _price));
            Log.e(LOG_TAG, "3 - ok");
        }

        View rootView = inflater.inflate(R.layout.driver_fragment, container, false);
        adapter = new ListAdapter(getActivity(), tripsInfos);

        ListView listView = (ListView) rootView.findViewById(R.id.driver_list_view);
        listView.setAdapter(adapter);



        return rootView;
    }

    private class RoadTripTask extends AsyncTask <String, Void, String[][]>{
        final String LOG_TAG = RoadTripTask.class.getSimpleName();
        final String jsonTripId = "id";
        final String jsonDepartDate = "departure_date";
        final String jsonArriveDate = "arrival_date";
        final String jsonSeats = "seats";
        final String jsonDepartPoint = "departure_point";
        final String jsonArrivalPoint = "arrival_point";
        final String jsonPrice = "price";


        protected String[][] GetDataFromJson(String responseString) throws JSONException{
                JSONArray jsonArray = new JSONArray(responseString);
                String[][] dataArry = new String[10][10];
                for (int i = 0; i < jsonArray.length(); i++){
                    JSONObject jsonTripObj = jsonArray.getJSONObject(i);
                    dataArry[i][0] = jsonTripObj.optString(jsonTripId, "null");
                    dataArry[i][1] = jsonTripObj.optString(jsonDepartDate,  "null");
                    dataArry[i][2] = jsonTripObj.optString(jsonArriveDate,  "null");
                    dataArry[i][3] = jsonTripObj.optString(jsonDepartPoint,  "null");
                    dataArry[i][4] = jsonTripObj.optString(jsonArrivalPoint,  "null");
                    dataArry[i][5] = jsonTripObj.optString(jsonSeats, "null");
                    dataArry[i][6] = jsonTripObj.optString(jsonPrice, "null");
                    Log.e(LOG_TAG, "RESP -" + dataArry[i][0] + " - " + dataArry[i][1] + " - " + dataArry[i][2] + " - " + dataArry[i][3] + " - " + dataArry[i][4]);
                }
                return dataArry;

        }

        @Override
        protected String[][] doInBackground(String... strings) {
            String strUrl = "http://api.gotob.by/v1/trips?driver_id=";
            String strId = strings[0];
            String strResponse = "";

            try {
                URL url = new URL(strUrl + strId);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setReadTimeout(10000);
                connection.setConnectTimeout(15000);
                connection.setRequestMethod("GET");
                connection.setDoInput(true);

                int responseCode = connection.getResponseCode();

                Log.e(LOG_TAG, "response code - " + responseCode);


                if (responseCode == HttpsURLConnection.HTTP_OK) {
                    String line;
                    BufferedReader br=new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    while ((line=br.readLine()) != null) {
                        strResponse+=line;
                    }
                }

                Log.e(LOG_TAG,strResponse);
                connection.disconnect();

                return GetDataFromJson(strResponse);
            }catch(Exception e){
                Log.e(LOG_TAG, e.toString());
                e.printStackTrace();
            }
            return null;
        }
    }

    public class TripsInfo{
        String id;
        String departure_time;
        String arrival_time;
        String depart_point;
        String arrival_point;
        String seats;
        String price;
        TripsInfo(String _id, String _departure_time, String _arrival_time, String _depart_point, String _arrival_point, String _seats, String _price){
            id = _id;
            departure_time = _departure_time;
            arrival_time = _arrival_time;
            depart_point = _depart_point;
            arrival_point = _arrival_point;
            seats = _seats;
            price = _price;
        }
    }
}
