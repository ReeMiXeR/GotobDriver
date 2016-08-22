package ru.busride.partner;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class ListAdapter extends BaseAdapter{
    Context ctx;
    LayoutInflater lInflater;
    ArrayList<DriverFragment.TripsInfo> objects;

    ListAdapter(Context context, ArrayList<DriverFragment.TripsInfo> products) {
        ctx = context;
        objects = products;
        lInflater = (LayoutInflater) ctx
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    // кол-во элементов
    @Override
    public int getCount() {
        return objects.size();
    }

    // элемент по позиции
    @Override
    public Object getItem(int position) {
        return objects.get(position);
    }

    // id по позиции
    @Override
    public long getItemId(int position) {
        return position;
    }

    // пункт списка
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // используем созданные, но не используемые view
        View view = convertView;
        if (view == null) {
            view = lInflater.inflate(R.layout.driver_list_view_row, parent, false);
        }

        DriverFragment.TripsInfo infoArray = getProduct(position);

        // заполняем View в пункте списка
        ((TextView) view.findViewById(R.id.trip_id_view)).setText(infoArray.id);
        ((TextView) view.findViewById(R.id.depart_time_view)).setText(dateFormat(infoArray.departure_time));
        ((TextView) view.findViewById(R.id.arrive_time_view)).setText(dateFormat(infoArray.arrival_time));
        ((TextView) view.findViewById(R.id.depart_point_view)).setText(infoArray.depart_point);
        ((TextView) view.findViewById(R.id.arrive_point_view)).setText(infoArray.arrival_point);
        ((TextView) view.findViewById(R.id.seats_view)).setText(infoArray.seats);
        ((TextView) view.findViewById(R.id.price_view)).setText(infoArray.price);

        return view;
    }

    DriverFragment.TripsInfo getProduct(int position) {
        return ((DriverFragment.TripsInfo) getItem(position));
    }

    private String dateFormat(String date){
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat outputFormat = new SimpleDateFormat("HH:mm dd MMMM");
            Date newDate = inputFormat.parse(date);
            return outputFormat.format(newDate);
        }catch (ParseException e){
            e.printStackTrace();
        }
        return null;
    }


}
