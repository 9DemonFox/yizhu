package com.example.myapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.strictmode.SqliteObjectLeakedViolation;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;
import android.widget.ScrollView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.SimpleTimeZone;

public class BoilActivity extends Activity {

    private Button button_boil2main;
    private Button button_new_boil;
    private ScrollView scrollView;
    private Button button;
    private String date_time = "";
    private String DATETIME;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boil);
        initBtn();
        loadBoil();
    }

    @Override
    protected void onResume() {
        super.onResume();
        onCreate(null);
    }

    public void initBtn() {
        button_boil2main = (Button) findViewById(R.id.button_boil2main);
        button_boil2main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent_main_boil = new Intent(BoilActivity.this, MainActivity.class);
                startActivity(intent_main_boil);
            }
        });

        button_new_boil = (Button) findViewById(R.id.button_newboil);
        button_new_boil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addBoil();
            }
        });
    }

    private void addBoil() {
        MyDBOpenHelper myDBOpenHelper = new MyDBOpenHelper(getApplicationContext());
        SQLiteDatabase db = myDBOpenHelper.getWritableDatabase();
        //取出最大id
        Cursor cursor = db.rawQuery("select max(id) from boil", null);
        cursor.moveToFirst();
        int maxid = cursor.getInt(cursor.getColumnIndex("max(id)"));//获取最大id
        cursor.close();
        ContentValues contentValues = new ContentValues();
        contentValues.put("id", maxid + 1);
//        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        java.util.Date utilDate = new Date();
        java.sql.Date sqlDate = new java.sql.Date(utilDate.getTime());
        java.sql.Time sqlTime = new java.sql.Time(utilDate.getTime());
        String DateTime = sqlDate.toString() + "  " + sqlTime.toString();
        String[] columns = new String[]{"date_time"};
        String[] args = new String[]{DateTime};
        if (db.query("boil", columns, "date_time=?", args, "", "", "").getCount() == 0) {
            contentValues.put("date_time", DateTime);
            contentValues.put("count", 0);
            db.insert("boil", null, contentValues);
            //Toast.makeText(getApplicationContext(), DateTime, Toast.LENGTH_SHORT).show();
            onResume();//刷新
        } else {
            Toast.makeText(getApplicationContext(), "请稍后", Toast.LENGTH_SHORT).show();
        }
    }

    private void alert(int cur_btn_id) {
        // TODO 无法使得Alert的确认键成功对应到每一个事件
        //转换btn id 为boil id
        int boil_id = cur_btn_id - 526;
        MyDBOpenHelper myDBOpenHelper = new MyDBOpenHelper(getApplicationContext());
        SQLiteDatabase db = myDBOpenHelper.getWritableDatabase();
        Cursor testCursor = db.rawQuery("select * from boil", null);
        Cursor cursor = db.rawQuery("select date_time from boil where id=? ", new String[]{String.valueOf(boil_id)});
        Cursor c = db.query("boil", new String[]{"id"}, "id=?", new String[]{String.valueOf(boil_id)}, null, null, null);
        cursor.moveToFirst();
        DATETIME = cursor.getString(cursor.getColumnIndex("date_time"));
        cursor.close();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("警告！").setMessage("确认删除当前事件？\n" + DATETIME).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteBoil(DATETIME);
            }

        });
        builder.show();
    }

    private void deleteBoil(String datetime) {
        MyDBOpenHelper myDBOpenHelper = new MyDBOpenHelper(getApplicationContext());
        SQLiteDatabase db = myDBOpenHelper.getWritableDatabase();
        // TODO 可以删除掉boil_products中的数据,也可以保留做分析 -->为了防止重复出现最好全部删除
        Cursor cursor = db.rawQuery("select id from boil where date_time = ? ", new String[]{datetime});
        int boil_id = -1;
        if (cursor.moveToFirst()) {
            do {
                boil_id = cursor.getInt(cursor.getColumnIndex("id"));
            } while (cursor.moveToNext());
        }
        cursor.close();//必须要关闭
        db.delete("boil_products", "boil_id=?", new String[]{String.valueOf(boil_id)});
        db.delete("boil", "date_time=?", new String[]{datetime});
        Toast.makeText(getApplicationContext(), "数据删除成功", Toast.LENGTH_SHORT).show();
        onResume();//刷新
    }

    public void loadBoil() {//加载烹煮事件
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.boil_scroller_lay);
        MyDBOpenHelper myDBOpenHelper = new MyDBOpenHelper(getApplicationContext());
        SQLiteDatabase db = myDBOpenHelper.getWritableDatabase();
        String[] columns = new String[]{"id", "date_time", "count"};
        Cursor cursor = db.query("boil", columns, null, null, null, null, "id desc", null);
        if (cursor.moveToFirst()) {//加载button
            do {
                final int id = cursor.getInt(cursor.getColumnIndex("id"));
                date_time = cursor.getString(cursor.getColumnIndex("date_time"));
                int count = cursor.getInt(cursor.getColumnIndex("count"));
                TableRow tableRow = new TableRow(this);
                Button boil_Button = new Button(this);
                boil_Button.setText(date_time + "  " + String.valueOf(count));//煮东西的点击事件
                // TODO 在此处添加建立煮货事件
                boil_Button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(BoilActivity.this, BoilProductsActivity.class);
                        intent.putExtra("id", id);//将boil的id传过去
                        startActivity(intent);
                    }
                });
                //使用id传值删除数据 button id 和 boil 表的id转化方法为 526+i
                tableRow.addView(boil_Button);
                boil_Button.setTextSize(18);
                final Button button = new Button(this);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {//添加弹出按钮确认删除
//                        AlertDialog.Builder builder = new AlertDialog.Builder(getBaseContext());//当前的builder所依赖的组件已经被删除
                        int current_btn_id = button.getId();//获取id
                        alert(current_btn_id);
                    }
                });
                button.setText("删除");
                button.setId(526 + id);
                tableRow.addView(button);
                linearLayout.addView(tableRow);
                boil_Button.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT, 1.0f));
                button.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT, 4.0f));
            } while (cursor.moveToNext());
        }
        cursor.close();//必须要关闭
    }
}
