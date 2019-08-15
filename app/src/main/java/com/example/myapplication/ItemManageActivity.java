package com.example.myapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import static android.widget.Toast.LENGTH_SHORT;

public class ItemManageActivity extends Activity {

    private Button btn_manage2main;
    private Button btn_addItem;
    private Button btn_delItem;
    public String product_name;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_itemmanager);
        initBtn();
        loadProducts();//加载产品
    }

    @Override
    protected void onResume() {
        super.onResume();
        onCreate(null);
    }

    private void initBtn() {//初始化按键
        btn_manage2main = (Button) findViewById(R.id.button_manage2main);
        btn_manage2main.setOnClickListener(new View.OnClickListener() {//返回主界面
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ItemManageActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        btn_addItem = (Button) findViewById(R.id.button_item_manage_add);
        btn_addItem.setOnClickListener(new View.OnClickListener() {//返回主界面
            @Override
            // TODO 修改为添加产品
            public void onClick(View view) {//添加产品,弹出对话框
                addItem();//添加产品
            }
        });

        btn_delItem = (Button) findViewById(R.id.button_item_manage_del);
        btn_delItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                delItem();//删除产品
            }
        });

    }

    private void delItem() {
        final EditText product_name_text = new EditText(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("输入删除产品名字").setView(product_name_text).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                product_name = product_name_text.getText().toString();
                MyDBOpenHelper myDBOpenHelper = new MyDBOpenHelper(getApplicationContext());
                SQLiteDatabase db = myDBOpenHelper.getWritableDatabase();
                db.delete("products", "name=?", new String[]{product_name});
                Toast.makeText(getApplicationContext(), "数据删除成功", Toast.LENGTH_SHORT).show();
                onResume();//刷新
            }
        });
        builder.show();
    }

    private void loadProducts() {
        MyDBOpenHelper myDBOpenHelper = new MyDBOpenHelper(getApplicationContext());
        SQLiteDatabase db = myDBOpenHelper.getWritableDatabase();
        String[] columns = new String[]{"id", "name"};
        Cursor cursor = db.query("products", columns, null, null, null, null, null);
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.products_layout);
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                String name = cursor.getString(cursor.getColumnIndex("name"));
                TextView textView = new TextView(this);
                textView.setText(String.valueOf(id) + "   " + name);
                linearLayout.addView(textView);
            } while (cursor.moveToNext());
        }
        cursor.close();//必须要关闭
    }

    private void addItem() {//添加数据
        final EditText product_name_text = new EditText(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("输入添加产品名字").setView(product_name_text).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                product_name = product_name_text.getText().toString();
                MyDBOpenHelper myDBOpenHelper = new MyDBOpenHelper(getApplicationContext());
                SQLiteDatabase db = myDBOpenHelper.getWritableDatabase();
                if(db.query("products", new String[]{"name"}, "name=?", new String[]{product_name}, null, null, null).getCount()==0) {
                    //取出最大id
                    Cursor cursor = db.rawQuery("select max(id) from products", null);
                    cursor.moveToFirst();
                    int maxid = cursor.getInt(cursor.getColumnIndex("max(id)"));//获取最大id
                    cursor.close();
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("id", String.valueOf(maxid + 1));
                    contentValues.put("name", product_name);
                    db.insert("products", null, contentValues);
                    Toast.makeText(getApplicationContext(), "数据插入成功", Toast.LENGTH_SHORT).show();
                    onResume();//刷新
                }else{
                    dialogInterface.dismiss();
                    Toast.makeText(getApplicationContext(), "产品已经存在", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.show();
    }
}
