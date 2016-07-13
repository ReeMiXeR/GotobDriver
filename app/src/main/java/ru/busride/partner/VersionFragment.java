package ru.busride.partner;

/**
 * Created by Shcherbakov on 11.07.2016.
 */
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;



public class VersionFragment extends Fragment {

    public VersionFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.version_fragment, container,
                false);

        return rootView;
    }

}