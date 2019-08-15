package com.example.myapplication;


import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;


public class MyDBOpenHelper extends SQLiteOpenHelper {

    public MyDBOpenHelper(Context context) {
        super(context, "dat.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 创建数据库
        db.execSQL("CREATE TABLE [boil](\n" +
                "  [id] INT PRIMARY KEY NOT NULL UNIQUE, \n" +
                "  [date_time] DATETIME NOT NULL, \n" +
                "  [count] INT);");

        db.execSQL("CREATE TABLE [boil_products](\n" +
                "  [id] DOUBLE PRIMARY KEY NOT NULL UNIQUE, \n" +
                "  [boil_id] INT NOT NULL, \n" +
                "  [product_id] INT NOT NULL, \n" +
                "  [count] DOUBLE, \n" +
                "  [unit] VARCHAR);");

        db.execSQL("CREATE TABLE [products]([rowid] INTEGER, [id] INT, [name] VARCHAR)");

        String string = "卤牛肉\n" +
                "卤猪头肉\n" +
                "卤猪蹄\n" +
                "卤肺\n" +
                "卤豆腐干\n" +
                "卤鸡尖\n" +
                "卤鸡爪\n" +
                "卤鸭爪\n" +
                "卤鸭翅\n" +
                "板鸭\n" +
                "泡鸡爪\n" +
                "火鸡翅膀\n" +
                "烟熏猪头肉\n" +
                "烤鸭\n" +
                "白水肉\n" +
                "盐水鸡\n" +
                "盐水鸭\n" +
                "素（海丝花）\n" +
                "素（海带）\n" +
                "素（素毛肚）\n" +
                "素（蕨菜）\n" +
                "肚条\n" +
                "肥肠\n" +
                "脆骨（喉管）\n" +
                "郡把\n" +
                "香酥鸭\n" +
                "鸡胗（郡肝）\n" +
                "鸭心\n" +
                "鸭肝\n" +
                "鸭脖";
        String[] products = string.split("\n");
        ContentValues contentValues = new ContentValues();
        int i = 0;
        for (; i < products.length; i++) {
            contentValues.put("id", String.valueOf(i));
            contentValues.put("name", products[i]);
            db.insert("products", null, contentValues);
            contentValues.clear();
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("ALTER TABLE person ADD phone VARCHAR(12)");
    }

}
