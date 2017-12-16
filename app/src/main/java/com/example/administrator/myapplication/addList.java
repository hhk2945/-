package com.example.administrator.myapplication;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

import static android.R.attr.format;

public class addList extends Activity {
    private Spinner spinnerCate;
    private Spinner spinnerInout;
    private String tranData;

    private static mySQLiteOpenHelper M;    // for insert
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_list_layout);  // layout xml 과 자바파일을 연결

        Intent intent = getIntent();    // 선택한 date 받아서
        String date = intent.getExtras().getString("date");
        EditText editdate = (EditText) findViewById(R.id.addDate) ;
        editdate.setText(date); // 새로운 페이지에 넣는다.

        setSpinner();
        setListener();

        Button b1 = (Button)findViewById(R.id.set_button);
        b1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                insert_add_Element();
                startActivity(intent); // 다음 화면으로 넘어간다
            }
        });
        Button b2 = (Button)findViewById(R.id.cancel_button);
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent); // 다음 화면으로 넘어간다
            }
        });
    } // end onCreate()

    public void setSpinner(){
        /*
        지출/수입 Spinner 세팅
         */
        spinnerInout = (Spinner) findViewById(R.id.InOrOut);
        List<String> a = new ArrayList<String>();
        a.add("지출");
        a.add("수입");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, a);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerInout.setAdapter(dataAdapter);

        /*
        카테고리 리스트 Spinner 세팅
        카테고리 정보들을 DB에서 가져온다.
         */
        spinnerCate = (Spinner) findViewById(R.id.category);
        List<String> b = new ArrayList<String>();
        Cursor cursor = M.select("SELECT * FROM CATEGORY");

        cursor.moveToNext();
        String name1 = cursor.getString(0);
        b.add(name1);

        cursor.moveToNext();
        String name2 = cursor.getString(0);
        b.add(name2);

        cursor.moveToNext();
        String name3 = cursor.getString(0);
        b.add(name3);

        cursor.moveToNext();
        String name4 = cursor.getString(0);
        b.add(name4);

        ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, b);
        dataAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCate.setAdapter(dataAdapter2);
    }
    public void setListener() {
        spinnerInout.setOnItemSelectedListener(new CustomOnItemSelectedListener());
        spinnerCate.setOnItemSelectedListener(new CustomOnItemSelectedListener());
    }
    /*
        확인버튼 입력 시 가계부 내역을 DB에 저장
        함수호출은 xml button의 onclick으로 호출.
     */
    private void insert_add_Element(){

        Spinner temp = (Spinner)findViewById(R.id.InOrOut);
        String temp0 = temp.getSelectedItem().toString();
        Log.d("Spinner", temp0);
        if(temp0 == "지출") tranData = "1,";
        else tranData = "0,";

        EditText temp1 = (EditText) findViewById(R.id.addDate);
        Log.d("Date", temp1.getText().toString());
        tranData += "'" + temp1.getText().toString() + "',";    // String은 ' 추가

        EditText temp4 = (EditText) findViewById(R.id.add_capacitypick);
        String temp5 = temp4.getText().toString();
        Log.d("capacity", temp5);
        tranData += temp5 + ',';

        Spinner temp2 = (Spinner) findViewById(R.id.category);
        String temp3 = temp2.getSelectedItem().toString();
        Log.d("category", temp3);
        tranData += "'" + temp3 + "',";

        EditText temp6 = (EditText) findViewById(R.id.set_memo);
        String temp7 = temp6.getText().toString();
        Log.d("memo", temp7);
        tranData += "'" + temp7 + "'";

//      M.insert("TRANS(InOrOut,Date,Amount,Category,Details)","1,'"+format.format(receivedDate)+"',"+Amount+",'카드 지출  ','"+content+"'");\
        Log.d("tranData", tranData);
        M.insert("TRANS(InOrOut, Date, Amount, Category, Details)", tranData);

        int[] arr = {31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
        String period = String.valueOf(temp1.getText().charAt(0)) + String.valueOf(temp1.getText().charAt(1))
                + String.valueOf(temp1.getText().charAt(3)) + String.valueOf(temp1.getText().charAt(4));
        String prevPeriod;
        if(temp1.getText().charAt(4) == '0')
        {
            prevPeriod = String.valueOf(temp1.getText().charAt(0)) + String.valueOf(temp1.getText().charAt(1))
                    + "09";
        }
        else if(temp1.getText().charAt(3) == '0' && temp1.getText().charAt(4) == '1')
        {
            if(temp1.getText().charAt(1) == '0')
            {
                prevPeriod = "0912";
            }
            else
            {
                prevPeriod = String.valueOf(temp1.getText().charAt(0)) + String.valueOf((char)(temp1.getText().charAt(1) - 1))
                        + "12";
            }
        }
        else
            prevPeriod = String.valueOf(temp1.getText().charAt(0)) + String.valueOf(temp1.getText().charAt(1))
                    + String.valueOf(temp1.getText().charAt(3)) + String.valueOf((char)(temp1.getText().charAt(4) - 1));

        Cursor cur = M.select("SELECT * FROM BUDGET WHERE Period = " + period);
        cur.moveToNext();
        int capacity = Integer.decode(temp4.getText().toString());
        int today = Calendar.DAY_OF_MONTH;
        int month = Integer.decode(period.substring(2));
        if (cur.getCount() == 0) {
            Cursor prev = M.select("SELECT * FROM BUDGET WHERE Period = " + prevPeriod); prev.moveToNext();
            if(temp0 == "지출")
                capacity *= -1;
            if(prev.getCount() == 0)
                M.insert("BUDGET", period + ", " + capacity + ", " + capacity / (arr[month - 1] - today + 1));
            else
                M.insert("BUDGET", period + ", " + (capacity + prev.getInt(1)) + ", " + ((capacity + prev.getInt(1)) / (arr[month - 1] - today)));
        }
        else
        {
            if(temp0 == "수입")
            {
                M.update("BUDGET", "Budget = " + (cur.getInt(1) + capacity) + ", AmountPerDay = " + ((cur.getInt(1) + capacity) / (arr[month - 1] - today)), "Period = " + period);
            }
            else
            {
                M.update("BUDGET", "Budget = " + (cur.getInt(1) - capacity), "Period = " + period);

                Cursor cursor1=M.select("SELECT * FROM BUDGET WHERE Period = " + period);
                cursor1.moveToNext();
                int budget = cursor1.getInt(1);

                Cursor cursor2=M.select("SELECT * FROM CUSTOM ORDER BY CUSTOM.CustomAmount ASC");
                while(cursor2.moveToNext())
                {
                    int custom_amount=cursor2.getInt(1);
                    if(budget<custom_amount)
                    {
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);

                        builder.setContentTitle("이중장부")
                                .setContentText(cursor2.getString(2))
                                .setTicker("")
                                .setSmallIcon(R.mipmap.ic_launcher)
                                //.setLargeIcon(BitmapFactory.decodeResource(res, R.mipmap.ic_launcher))
                                // .setContentIntent(contentIntent)
                                .setAutoCancel(true)
                                .setWhen(System.currentTimeMillis())
                                .setDefaults(Notification.DEFAULT_ALL);



                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                            builder.setCategory(Notification.CATEGORY_MESSAGE)
                                    .setPriority(Notification.PRIORITY_HIGH)
                                    .setVisibility(Notification.VISIBILITY_PUBLIC);
                        }

                        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                        nm.notify(1234, builder.build());
                        break;
                    }

                }
            }
        }
    }
    public void setDB(mySQLiteOpenHelper db){
        M = db;
    }
}