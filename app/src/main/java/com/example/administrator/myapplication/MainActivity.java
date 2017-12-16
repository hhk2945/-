package com.example.administrator.myapplication;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.database.Cursor;
import android.content.BroadcastReceiver;
import android.widget.Spinner;
import android.widget.Toast;

import android.widget.CalendarView;
import android.widget.TextView;

import java.util.ArrayList;

import static android.R.id.message;

public class MainActivity extends AppCompatActivity {
    /*
        Popup Layer를 위한 변수 선언.
     */
    private Button btnClosePopup;
    private Button btnSaveData;
    private Button btnCreatePopup;
    private PopupWindow popwin;
    private ArrayList<String> arraylist;
    private int mWidthPixels, mHeightPixels;
    private Spinner spinner;

    String selectedDate;
    TextView budgetView;
    String str="";
    mySQLiteOpenHelper openHelper;
    ArrayList List = new ArrayList();
    ArrayAdapter adapter;

    // DB에 수입/지출 데이터 저장을 위한 구조체
    public class tranData{
        private int inORout;
        private String date;
        private int amount;
        private String catalog;
        private String details;

        public tranData(int inORout, String date, int amount, String catalog, String details){
            this.inORout = inORout;
            this.date = date;
            this.amount = amount;
            this.catalog = catalog;
            this.details = details;
        }
        private String getValue(){
            return inORout +", '" + date +"', " +
                    amount +", '" + catalog +"', '" + details + "'";
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d("onCreate()","브로드캐스트리시버 등록됨");
        SmsReceiver temp = new SmsReceiver();
        Custom temp2 = new Custom();
        addList temp3 = new addList();


        final mySQLiteOpenHelper transaction = new mySQLiteOpenHelper(getApplicationContext(), "double_ledger", null, 1);
        temp.setDB(transaction);
        temp3.setDB(transaction);
        temp2.setDB(transaction);

        checkSNSPermissions(); // SMS사용 권한 요청

        budgetView = (TextView)findViewById(R.id.budgetView);
        budgetView.setText("보유 금액 : ");
        CalendarView calendar = (CalendarView) findViewById(R.id.calendarView);
        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener(){
            public void onSelectedDayChange(CalendarView view, int year, int month, int day) {
                if(month < 9) {
                    if(day < 10)
                        selectedDate = ((year - 2000) + "-0" + (month + 1) + "-0" + day);
                    else
                        selectedDate = ((year - 2000) + "-0" + (month + 1) + "-" + day);
                }
                else {
                    if(day < 10)
                        selectedDate = ((year - 2000) + "-" + (month + 1) + "-0" + day);
                    else
                        selectedDate = ((year - 2000) + "-" + (month + 1) + "-" + day);
                }

                List.clear();
                Cursor cursor = transaction.select("SELECT * FROM TRANS WHERE TRANS.Date='" + selectedDate + "'");

                while (cursor.moveToNext()) {
                    str="";
                    if(cursor.getInt(1) == 0){
                        str += "수입  금액 : " + cursor.getInt(3) + "원    카테고리 : " + cursor.getString(4) + "   사용내역 : " + cursor.getString(5)+'\n';
                    } else{
                        str += "지출  금액 : " + cursor.getInt(3) + "원    카테고리 : " + cursor.getString(4) + "   사용내역 : " + cursor.getString(5)+'\n';
                    }
                    makeList(List, str, transaction);
                }
                if(!cursor.moveToNext()){
                    str="";
                    makeList(List, str, transaction);
                }
                String tmp = selectedDate.substring(0, 2) + selectedDate.substring(3, 5);
                cursor = transaction.select("SELECT Budget FROM BUDGET WHERE Period = " + tmp);
                cursor.moveToNext();
                if(cursor.getCount() == 0)
                {
                    String prevPeriod;
                    if(selectedDate.charAt(4) == '0')
                    {
                        prevPeriod = String.valueOf(selectedDate.charAt(0)) + String.valueOf(selectedDate.charAt(1))
                                + "09";
                    }
                    else if(selectedDate.charAt(3) == '0' && selectedDate.charAt(4) == '1')
                    {
                        if(selectedDate.charAt(1) == '0')
                        {
                            prevPeriod = "0912";
                        }
                        else
                        {
                            prevPeriod = String.valueOf(selectedDate.charAt(0)) + String.valueOf((char)(selectedDate.charAt(1) - 1))
                                    + "12";
                        }
                    }
                    else
                        prevPeriod = String.valueOf(selectedDate.charAt(0)) + String.valueOf(selectedDate.charAt(1))
                                + String.valueOf(selectedDate.charAt(3)) + String.valueOf((char)(selectedDate.charAt(4) - 1));
                    Cursor prevCur = transaction.select("SELECT Budget FROM BUDGET WHERE Budget = " + prevPeriod);
                    prevCur.moveToNext();
                    if(prevCur.getCount() == 0)
                    {
                        budgetView.setText("보유 금액 : 0");
                    }
                    else
                    {
                        budgetView.setText(prevCur.getInt(0));
                    }
                }
                else
                {
                    budgetView.setText("보유 금액 : " + String.valueOf(cursor.getInt(0)));
                }
            }
        });


        Button b = (Button)findViewById(R.id.plus_btn);
        b.setOnClickListener(new View.OnClickListener() {
//            txt_main = (EditText)findViewById(R.id.txt_main);
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), addList.class); // 다음 넘어갈 클래스 지정
                intent.putExtra("date", selectedDate);
                startActivity(intent); // 다음 화면으로 넘어간다
            }
        });
        Button custom = (Button)findViewById(R.id.plus_custom);
        custom.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent =new Intent(getApplicationContext(), Custom.class);
                startActivity(intent);
            }
        });
    }

    // 리스트작성
    private void makeList(ArrayList List, String str, final mySQLiteOpenHelper transaction){
        adapter = new ArrayAdapter(this, R.layout.list_textview, List) ;
        final ListView listview = (ListView) findViewById(R.id.lists) ;
        listview.setAdapter(adapter) ;

        List.add(str);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                deleteList(position, transaction);

                // listview 선택 초기화.
                listview.clearChoices();
                adapter.notifyDataSetChanged();
            }
        }) ;
    }

    private void deleteList(int pos, mySQLiteOpenHelper transaction){

        List.remove(pos);
        Cursor cur = transaction.select("SELECT * FROM TRANS WHERE TRANS.DATE = '"+selectedDate+"'");
        cur.moveToNext();
        for(int i=0; i<pos;i++) cur.moveToNext();
        int ID = cur.getInt(0);
        int Amount = cur.getInt(3);
        int InorOut = cur.getInt(1);
        transaction.delete("TRANS","TRANS.Id = "+ID);

        int year = Integer.parseInt(selectedDate.substring(0, 2));
        int month = Integer.parseInt(selectedDate.substring(3, 5));
        cur = transaction.select("SELECT * FROM BUDGET WHERE Period = " + ((year * 100) + month));
        cur.moveToNext();

        if (InorOut == 0) { //수입 삭제
            transaction.update("BUDGET", "Budget = " + (cur.getInt(1) - Amount), "Period = " + cur.getInt(0));
        }
        else { // 지출삭제
            transaction.update("BUDGET", "Budget = " + (cur.getInt(1) + Amount), "Period = " + cur.getInt(0));
        }
    }

    //SNS 권한 확인
    private void checkSNSPermissions(){
        int permissioncheck = ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS);
        if(permissioncheck == PackageManager.PERMISSION_GRANTED){
            Toast.makeText(this, "권한 있음", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "권한 없음", Toast.LENGTH_LONG).show();

            if(ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.RECEIVE_SMS)){
                Toast.makeText(this, "권한 설명 필요함.", Toast.LENGTH_LONG).show();
            }else{
                String[] permissions = {
                        android.Manifest.permission.RECEIVE_SMS
                };

                //권한이 할당되지 않았으면 해당 권한을 요청
                ActivityCompat.requestPermissions(this, permissions,1);
            }
        }
    }

    //권한 결과
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        if (requestCode == 1){
            for(int i = 0; i<permissions.length; i++){
                if(grantResults[i] == PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this, permissions[i] + "권한이 승인됨", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(this, permissions[i] + "권한이 승인되지 않음,", Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}
