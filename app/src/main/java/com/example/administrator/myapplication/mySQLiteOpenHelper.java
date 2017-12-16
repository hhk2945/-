package com.example.administrator.myapplication;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by ch_01 on 2016-11-27.
 * Help to manipulate a database.
 * 생성자를 통해 SQLiteOpenHelper를 생성하고
 * onCreate에서 데이터베이스 테이블을 생성함.
 */



public class mySQLiteOpenHelper extends SQLiteOpenHelper {
    SQLiteDatabase ledger;

    public mySQLiteOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        Log.v("DBHelper", "Constructure executed!!");
        ledger = getWritableDatabase();
    }

    @Override

    public void onCreate(SQLiteDatabase db) {
        ledger = db;
        db.execSQL("CREATE TABLE TRANS ("
        + "Id INTEGER PRIMARY KEY AUTOINCREMENT, "
        + "InOrOut INTEGER, "
        + "Date TEXT, "
        + "Amount INTEGER, "
        + "Category TEXT, "
        + "Details BLOB);");
        db.execSQL("CREATE TABLE BUDGET ("
        + "Period INTEGER PRIMARY KEY NOT NULL, "
        + "Budget INTEGER, "
        + "AmountPerDay INTEGER);");
        db.execSQL("CREATE TABLE CATEGORY ("
        + "Name TEXT PRIMARY KEY NOT NULL, "
        + "Icon BLOB);");
        db.execSQL("CREATE TABLE BANK ("
        + "BankNum INTEGER PRIMARY KEY NOT NULL, "
        + "Payaddr INTEGER, "
        + "Dataaddr INTEGER);");
        db.execSQL("CREATE TABLE CUSTOM ("
        + "Id INTEGER PRIMARY KEY AUTOINCREMENT, "
        + "CustomAmount INTEGER, "
        + "Customtext TEXT);");
        Log.v("DBHelper", "onCreate executed!");

        db.execSQL("INSERT INTO CATEGORY(Name) VALUES('식비')");
        db.execSQL("INSERT INTO CATEGORY(Name) VALUES('생필품')");
        db.execSQL("INSERT INTO CATEGORY(Name) VALUES('유흥')");
        db.execSQL("INSERT INTO CATEGORY(Name) VALUES('기타')");

        db.execSQL("INSERT INTO BANK(BankNum,Payaddr,Dataaddr) VALUES(55215556, 2, 3);");
        db.execSQL("INSERT INTO BANK(BankNum,Payaddr,Dataaddr) VALUES(22820543, 3, 2);");
        db.execSQL("INSERT INTO BANK(BankNum,Payaddr,Dataaddr) VALUES(98895320, 2, 3);");
    }

    /**
     * 데이터베이스에 insert문 쿼리를 삽입한다.
     * 'INSERT INTO "table이름(어트리뷰트)" VALUES("어트리뷰트 당 할당할 값");'이 Query로 변환되어 삽입된다.
     * table은 table의 이름과 삽입할 어트리뷰트를 값으로 받는다. ex) TRANSACTION(Id, Date, Amount)
     * value는 어트리뷰트에 대칭되는 값을 받아온다.              ex) 1, '2016-11-17', 20000
     */
    public void insert(String table, String value)
    {
        String query = "INSERT INTO " + table + " VALUES(" + value + ");";
        ledger.execSQL(query);
    }

    /**
     * 데이터베이스에 update문 쿼리를 삽입한다.
     * 'UPDATE "table이름" SET "어트리뷰트" WHERE "조건";'이 Query로 변환되어 삽입된다.
     * table은 table의 이름과 삽입할 어트리뷰트를 값으로 받는다. ex) TRANSACTION
     * set은 업데이트할 어트리뷰트와 변경사항을 받아온다.        ex) Date = '2016-10-11'
     * where은 조건을 받아온다                                   ex) Id = 1
     */
    public void update(String table, String set, String where)
    {
        ledger.execSQL("UPDATE " + table + " SET " + set + " WHERE " + where + ";");
    }

    /**
     *  데이터베이스에 delete문 쿼리를 삽입한다.
     *  'DELETE FROM "table이름" WHERE "조건";'이 Query로 변환되어 삽입된다.
     *  table은 table 이름을 값으로 받는다.    ex) TRANSACTION
     *  where은 조건을 받는다.                 ex) Id = 1
     */
    public void delete(String table, String where)
    {
        ledger.execSQL("DELETE FROM " + table + " WHERE " + where + ";");
    }

    /**
     * 데이터베이스에 쿼리를 삽입한다.
     * query에는 SELECT FROM WHERE문의 전체를 받아온다.
     * ex) SELECT * FROM TRANSACTION WHERE DATA = '2016-11-12';
     */
    public Cursor select(String query)
    {
        Cursor cursor = ledger.rawQuery(query, null);

        return cursor;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}