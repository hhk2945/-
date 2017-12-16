package com.example.administrator.myapplication;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.telephony.SmsMessage;
import android.util.Log;


public class SmsReceiver extends BroadcastReceiver {
    public static final String TAG = "SmsReceiver";
    public SimpleDateFormat format = new SimpleDateFormat("yy-MM-dd");
    private static mySQLiteOpenHelper M;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG,"onReceive() 메서드 호출.");

        if(intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")){
            Log.i(TAG,"SMS 메시지가 수신되었습니다.");

            Bundle bundle = intent.getExtras();
            Object[] objs = (Object[])bundle.get("pdus");
            SmsMessage[] messages = new SmsMessage[objs.length];

            int smsCount = objs.length;
            for(int i =0; i< smsCount; i++){
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    String format = bundle.getString("format");
                    messages[i] = SmsMessage.createFromPdu((byte[])objs[i], format);
                } else{
                    messages[i] = SmsMessage.createFromPdu((byte[])objs[i]);
                }
            }

            Date receivedDate = new Date(messages[0].getTimestampMillis());

            String sender = messages[0].getOriginatingAddress();
            sender = sender.substring(3); //155 5521 5554
            Cursor cursor =M.select("SELECT * FROM BANK WHERE BANK.BankNum = "+Integer.parseInt(sender));
            cursor.moveToNext();

            Log.i(TAG, "SMS sender : " + sender);
            String contents = messages[0].getMessageBody().toLowerCase();

            int Amount; //금액
            String content; //내역
            String[] arr = contents.split("\n");
            Amount = Integer.parseInt(arr[cursor.getInt(1)].substring(0,(arr[cursor.getInt(1)].length()-1)));
            content = arr[cursor.getInt(2)];

            int[] array = {31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
            String date = format.format(receivedDate);
            M.insert("TRANS(InOrOut,Date,Amount,Category,Details)","1,'"+date+"',"+Amount+",'카드  ','"+content+"'");
            int year = Integer.parseInt(date.substring(0,2));
            int month = Integer.parseInt(date.substring(3,5));
            cursor = M.select("SELECT * FROM BUDGET WHERE Period = " + ((year*100)+month));
            cursor.moveToNext();
            M.update("BUDGET", "Budget = " + (cursor.getInt(1)-Amount), "Period = " + cursor.getInt(0));

            Cursor cursor1=M.select("SELECT * FROM BUDGET WHERE Period = " + ((year*100)+month));
            cursor1.moveToNext();
            int budget = cursor1.getInt(1);

            Cursor cursor2=M.select("SELECT * FROM CUSTOM ORDER BY CUSTOM.CustomAmount ASC");
            while(cursor2.moveToNext())
            {
                int custom_amount=cursor2.getInt(1);
                if(budget<custom_amount)
                {
                    NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

                    builder.setContentTitle("이중장부")
                            .setContentText(cursor2.getString(2))
                            .setTicker("")
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setAutoCancel(true)
                            .setWhen(System.currentTimeMillis())
                            .setDefaults(Notification.DEFAULT_ALL);



                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                        builder.setCategory(Notification.CATEGORY_MESSAGE)
                                .setPriority(Notification.PRIORITY_HIGH)
                                .setVisibility(Notification.VISIBILITY_PUBLIC);
                    }

                    NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                    nm.notify(1234, builder.build());
                    break;
                }

            }


        }
    }



    public void setDB(mySQLiteOpenHelper db){
        M = db;
    }
}