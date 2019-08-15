package com.example.myapplication;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import java.util.HashMap;
import java.util.Map;

/**
 * 继承了FragmentActivity
 */
public class BoilProductsActivity extends Activity {

    private SearchView searchView;
    private Fragment searchFragment;
    private Button btn_this2boil;
    private Button btn_add;
    ActivityManager activityManager;
    private int boil_id;//煮的
    private int total_count = 0;

    /**
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boil_products);
        //获取boil_id
        boil_id = getIntent().getIntExtra("id", -1);
        initBtn();
        showDetail();
    }

    @Override
    protected void onResume() {
        super.onResume();
        onCreate(null);
    }


    /**
     * 初始化按钮 将boil_id传入下个页面
     */
    private void initBtn() {
        btn_this2boil = (Button) findViewById(R.id.button_boilproducts2boil);
        btn_this2boil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BoilProductsActivity.this, BoilActivity.class);
                startActivity(intent);
            }
        });

        btn_add = (Button) findViewById(R.id.button_boilproducts_add);
        btn_add.setOnClickListener(new View.OnClickListener() {
            /**
             * 将boil_id的数据传入下一个页面，根据数据加载出已经添加的数据，用products的数据减去添加的id即得到可添加的产品
             * @param view
             */
            @Override
            public void onClick(View view) {
                //将当前页面的数据传入过去
                Intent intent = new Intent(BoilProductsActivity.this, AddProductsActivity.class);
                intent.putExtra("id", boil_id);//将boil的id传过去
                startActivity(intent);
            }
        });

    }

    /**
     * 将该日所有货物的目录显示于下
     */
    private void showDetail() {
        // TODO 考虑添加搜索功能
        //读取表products中的数据
        //读取表boil中id为穿过来的数据count每添加物品就+1
        //读取表boil_produccts中boil_id=id的数据
        //相减去即为搜索栏的数据

        // TODO 加载数据库中boil_id的数据
        MyDBOpenHelper myDBOpenHelper = new MyDBOpenHelper(getApplicationContext());
        SQLiteDatabase db = myDBOpenHelper.getWritableDatabase();
        Map<Integer, String> id_name = new HashMap<Integer, String>();//键-值映射
        //获取所有产品的名称/id
        final String[] columns = new String[]{"id", "name"};
        Cursor cursor = db.query("products", columns, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                String name = cursor.getString(cursor.getColumnIndex("name"));
                id_name.put(id, name);
            } while (cursor.moveToNext());
        }
        cursor.close();//必须要关闭
        cursor = db.query("boil_products", new String[]{"product_id", "count"}, "boil_id=?", new String[]{String.valueOf(boil_id)}, null, null, null);
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.layout_show_detail_selected);
        total_count = cursor.getCount();
        //将产品显示到界面上
        if (cursor.moveToFirst()) {
            do {
                TableRow tableRow = new TableRow(this);
                final Button del_btn = new Button(this);
                del_btn.setText("删除");
                del_btn.setTextSize(12);
                del_btn.setMinHeight(18);
                del_btn.setHeight(18);
                del_btn.setContentDescription(String.valueOf(cursor.getInt(cursor.getColumnIndex("product_id"))));
                double weight = cursor.getDouble(cursor.getColumnIndex("count"));
                // TODO 为每个按钮添加删除事件
                del_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        MyDBOpenHelper myDBOpenHelper = new MyDBOpenHelper(getApplicationContext());
                        SQLiteDatabase db = myDBOpenHelper.getWritableDatabase();
                        db.delete("boil_products", "product_id=? and boil_id=?", new String[]{(String) del_btn.getContentDescription(), String.valueOf(boil_id)});
                        // 删除数据
                        onResume();//刷新
                    }
                });
                TextView textView = new TextView(this);
                textView.setTextSize(14);
                textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                final EditText editText = new EditText(this);
                editText.setTextSize(14);
                tableRow.addView(del_btn);
                tableRow.addView(textView);
                tableRow.addView(editText);
                editText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);//设置输入为小数
                if(weight!=0) editText.setText(String.valueOf(weight));
                editText.setContentDescription(String.valueOf(cursor.getInt(cursor.getColumnIndex("product_id"))));
                editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View view, boolean b) {
                        if (!b) {//失去焦点
                            //需要把产品id传入进来
                            String pid = (String) editText.getContentDescription();
                            double count = Double.parseDouble(editText.getText().toString());
                            MyDBOpenHelper myDBOpenHelper = new MyDBOpenHelper(getApplicationContext());
                            SQLiteDatabase db = myDBOpenHelper.getWritableDatabase();
                            ContentValues contentValues = new ContentValues();
                            contentValues.put("count", count);
                            db.update("boil_products", contentValues, "product_id=? and boil_id=?",new String[]{pid,String.valueOf(boil_id)});
                            onResume();
                        }
                    }
                });
                //将edit的digit属性更改
                del_btn.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT, 1.3f));
                textView.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT, 1.0f));
                editText.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT, 1.0f));
                String name = id_name.get(cursor.getInt(cursor.getColumnIndex("product_id")));//获取产品id-键值
                textView.setText(name);
                linearLayout.addView(tableRow);
            } while (cursor.moveToNext());
        }
        cursor.close();//必须要关闭
        TextView textView_show_total_count = (TextView) findViewById(R.id.text_show_products_count);
        textView_show_total_count.setText("总数: " + String.valueOf(total_count));
        textView_show_total_count.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        textView_show_total_count.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
//        Toast.makeText(getApplicationContext(), String.valueOf(boil_id), Toast.LENGTH_SHORT).show();

    }

}
