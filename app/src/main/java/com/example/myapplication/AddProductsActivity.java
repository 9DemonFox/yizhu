package com.example.myapplication;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class AddProductsActivity extends Activity {
    private int boil_id;
    private Set<Integer> selected_products_id = new HashSet<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_products);
        set_boil_id();
        initBtn();
        loadAddProducts();//加载可以添加的产品
    }

    @Override
    protected void onResume() {
        super.onResume();
        onCreate(null);
    }

    private void set_boil_id() {
        boil_id = getIntent().getIntExtra("id", -1);
    }

    private void initBtn() {
        Button btn_this2boilProducts = (Button) findViewById(R.id.button_addProducts2boilsProducts);
        btn_this2boilProducts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddProductsActivity.this, BoilProductsActivity.class);
                //返回当前的id
                intent.putExtra("id", boil_id);
                startActivity(intent);
            }
        });

        Button btn_sure_selected = (Button) findViewById(R.id.button_sure_selected_products);
        btn_sure_selected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddProductsActivity.this, BoilProductsActivity.class);
                //返回当前的id
                intent.putExtra("id", boil_id);
                //把所选的id都加入到boil_products数据库中，并且将boil_products的count置为0
                Iterator iterator = selected_products_id.iterator();
                while (iterator.hasNext()) {
                    int product_id = (int) iterator.next();
                    double item_id = new Date().getTime();//存入时间作为唯一id
                    double count = 0.0;
                    MyDBOpenHelper myDBOpenHelper = new MyDBOpenHelper(getApplicationContext());
                    SQLiteDatabase db = myDBOpenHelper.getWritableDatabase();
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("id", item_id);
                    contentValues.put("product_id", product_id);
                    contentValues.put("boil_id", boil_id);
                    contentValues.put("count", count);
                    db.insert("boil_products", null, contentValues);
                    //将所选的加入数据库
                }
                startActivity(intent);
            }
        });
    }

    private void loadAddProducts() {
        Set<Integer> product_ids = new TreeSet<Integer>();//所有产品id
        Set<Integer> exist_product_ids = new TreeSet<Integer>();
        Set<Integer> can_add_ids = new TreeSet<Integer>();//差集
        Map<Integer, String> id_name = new HashMap<Integer, String>();//键-值映射
        //获取所有产品的名称/id
        MyDBOpenHelper myDBOpenHelper = new MyDBOpenHelper(getApplicationContext());
        SQLiteDatabase db = myDBOpenHelper.getWritableDatabase();
        String[] columns = new String[]{"id", "name"};
        Cursor cursor = db.query("products", columns, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                String name = cursor.getString(cursor.getColumnIndex("name"));
                id_name.put(id, name);
                product_ids.add(Integer.valueOf(id));
            } while (cursor.moveToNext());
        }
        cursor.close();//必须要关闭
        //获取boil_products中所有的boil_id的产品
        cursor = db.query("boil_products", new String[]{"product_id"}, "boil_id=?", new String[]{String.valueOf(boil_id)}, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex("product_id"));
                exist_product_ids.add(Integer.valueOf(id));
            } while (cursor.moveToNext());
        }
        cursor.close();
        //取两者的差集
        can_add_ids.clear();
        can_add_ids.addAll(product_ids);
        can_add_ids.removeAll(exist_product_ids);
        Iterator iterator = can_add_ids.iterator();
        while (iterator.hasNext()) {
            LinearLayout scrollView = (LinearLayout) findViewById(R.id.scroll_addproducts);
            TableRow tableRow = new TableRow(this);
            TextView textView = new TextView(this);
            CheckBox checkBox = new CheckBox(this);
            //为该check设置事件 将选中的加入选中集合中
            final int cur_id = (int) iterator.next();
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b) {//选中
                        selected_products_id.add(cur_id);
                    } else {
                        selected_products_id.remove(cur_id);
                    }
                }
            });
            String product_name = id_name.get(cur_id);
            textView.setText(product_name);
            tableRow.addView(textView);
            tableRow.addView(checkBox);
            textView.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT, 1.0f));
            checkBox.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT, 9.0f));
            scrollView.addView(tableRow);
        }

        Toast.makeText(getApplicationContext(), "加载成功", Toast.LENGTH_SHORT).show();
    }
}
