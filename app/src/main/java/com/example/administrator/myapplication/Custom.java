package com.example.administrator.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;


public class Custom extends Activity{
    private static mySQLiteOpenHelper M;
    private ArrayList List = new ArrayList();
    private ArrayAdapter adapter;
    private int ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.custom_set);

        adapter = new ArrayAdapter(this, R.layout.list_textview, List) ;
        final ListView listview = (ListView) findViewById(R.id.custom_list) ;
        listview.setAdapter(adapter) ;

        Cursor cursor = M.select("SELECT * FROM CUSTOM");
        String str="";
        while (cursor.moveToNext()) {
            str = "한도금액 : " + cursor.getInt(1) + "  알림문자 : " + cursor.getString(2)+'\n';
            int tempId = cursor.getInt(0);
            makeList(str, tempId);
        }

        // 뒤로가기
        Button cancel = (Button)findViewById(R.id.cancel_button);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(
                        getApplicationContext(),
                        MainActivity.class);
                startActivity(intent);
            }
        });

        Button set = (Button)findViewById(R.id.set_button);
        set.setOnClickListener((new View.OnClickListener(){
            @Override
            public void onClick(View view){
                String customtext = ((TextView)findViewById(R.id.alram_set)).getText().toString();
                int customAmount = Integer.parseInt(((TextView)findViewById(R.id.Amount_set)).getText().toString());
                M.insert("CUSTOM(CustomAmount, Customtext)", customAmount+",'"+customtext+"'");
                String str = "한도금액 : " + customAmount + "  알림문자 : " + customtext+'\n';
                List.add(str);
                adapter.notifyDataSetChanged();
        }
        }));
    }

    private void makeList(String str, int id){
        adapter = new ArrayAdapter(this, R.layout.list_textview, List) ;
        final ListView listview = (ListView) findViewById(R.id.custom_list) ;
        listview.setAdapter(adapter) ;

        List.add(str);
        ID = id;

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                deleteList(position, ID);
                adapter.notifyDataSetChanged();
            }
        }) ;
    }
    private void deleteList(int pos, int ID){
        List.remove(pos);
        M.delete("CUSTOM","CUSTOM.Id = "+ID);
    }
    public void setDB(mySQLiteOpenHelper db){
        M = db;
    }
}
