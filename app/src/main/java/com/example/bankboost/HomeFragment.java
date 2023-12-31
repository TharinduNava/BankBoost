package com.example.bankboost;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


public class HomeFragment extends Fragment {

    ImageView btnBank1, btnBank2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_home, container, false);

        // Image View for Commercial Bank
        btnBank1 = v.findViewById(R.id.btnBank1);
        btnBank1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), Combank.class);
                startActivity(intent);
            }
        });

        // Image View for Bank of Ceylon
        btnBank2 = v.findViewById(R.id.btnBank2);
        btnBank2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), BOC.class);
                startActivity(intent);
            }
        });

        return v;
    }
}