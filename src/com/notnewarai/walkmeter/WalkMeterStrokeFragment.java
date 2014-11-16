package com.notnewarai.walkmeter;

import android.app.Activity;


import android.content.Context;

import android.os.Bundle;

import android.support.v4.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;

import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Spinner;


public class WalkMeterStrokeFragment extends Fragment implements OnClickListener {
	
	private ImageView strokeImageView;
	private TextView allDistanceTextView;
	private Spinner whereListSpinner;

		
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	
    	View view =inflater.inflate(R.layout.walkmeter_stroke_fragment, container, false);
    
    	strokeImageView = (ImageView)view.findViewById(R.id.strokeImageView);
    	allDistanceTextView = (TextView)view.findViewById(R.id.allDistanceTextView);
    	whereListSpinner = (Spinner)view.findViewById(R.id.whereListSpinner);
    	
    	
    	return view;
    }
    
    @Override
    public void onAttach(Activity act){
        super.onAttach(act);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
      super.onActivityCreated(savedInstanceState);
     
      
    }

    @Override
    public void onResume() {
        super.onResume();
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
    }
   
    
    @Override
    public void onClick(View V)
    {

    } 
    

    
    
}

