package net.dambakk.drinkmaster;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by chris on 29.07.2016.
 */
public class Adventures extends Fragment {

    public Adventures(){
        //Required empty constructor
        System.out.println("My Adventures was pressed");
    }

    public static Adventures newInstance(){
        Adventures fragment = new Adventures();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_adventures, container, false);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("My adventures");



        return super.onCreateView(inflater, container, savedInstanceState);

    }
}
