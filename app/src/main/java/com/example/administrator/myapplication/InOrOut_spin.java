package com.example.administrator.myapplication;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by whcks on 2016-12-05.
 */

public class InOrOut_spin extends Activity implements AdapterView.OnItemSelectedListener {
    private ArrayList<String> arraylist;
    public InOrOut_spin(Context context){

        arraylist = new ArrayList<String>();
        arraylist.add("지출");
        arraylist.add("수입");

        Log.d("InOrOut", "data 세팅까지는");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
                android.R.layout.simple_spinner_dropdown_item, arraylist);
        //스피너 속성
        Spinner sp = (Spinner) this.findViewById(R.id.InOrOut);
        sp.setPrompt("골라봐"); // 스피너 제목
        sp.setAdapter(adapter);
        sp.setOnItemSelectedListener(this);
    }
    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
                               long arg3) {
        Log.d("onItemSelected", "리스너도 받아들인다");
        Toast.makeText(this, arraylist.get(arg2), Toast.LENGTH_LONG).show();//해당목차눌렸을때
    }
    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
    }
}
