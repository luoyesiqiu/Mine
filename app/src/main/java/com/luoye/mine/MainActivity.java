package com.luoye.mine;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.DisplayMetrics;

/**
 * Created by zyw on 2016/7/29.
 */
public class MainActivity extends Activity {
    public  static  int W;
    public  static  int H;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        W = dm.widthPixels;//屏幕宽度
        H = dm.heightPixels ;//屏幕高度

        setContentView(new MainView(this));
        new AlertDialog.Builder(this)
                .setCancelable(false)
                .setTitle("游戏规则")
                .setMessage("把你认为不是雷的位置全部点开，只留着有雷的位置，每局游戏有10个雷。\n\n--落叶")
                .setPositiveButton("我知道了",null)
                .create()
                .show();
    }
}