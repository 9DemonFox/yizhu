package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.Toast;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import static android.widget.Toast.LENGTH_SHORT;

public class MainActivity extends AppCompatActivity {
    private Button button_boil;
    private Button button_itemManage;//产品管理

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bindViews();//设置事件监听

    }

    private void bindViews() {

        button_boil = (Button) findViewById(R.id.button_boil);
        button_boil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, BoilActivity.class);
                startActivity(intent);
            }
        });

        button_itemManage = (Button) findViewById(R.id.button_itemManage);
        button_itemManage.setOnClickListener(new View.OnClickListener() {//跳转到产品管理页面
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ItemManageActivity.class);
                startActivity(intent);
            }
        });
    }


    class BtnClickListener implements View.OnClickListener {
        /*
            从主界面跳转到煮菜页面
         */
        @Override
        public void onClick(View v) {
            Intent intent_main_boil = new Intent(MainActivity.this, BoilActivity.class);
            startActivity(intent_main_boil);
        }
    }

}
