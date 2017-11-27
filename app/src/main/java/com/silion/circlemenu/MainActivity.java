package com.silion.circlemenu;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.silion.circlemenu.widget.CircleMenuLayout;

/**
 * Android源码设计模式-适配器模式 P393 圆形菜单布局视图
 */
public class MainActivity extends Activity {
    private CircleMenuLayout mCircleMenuLayout;
    // 菜单标题
    private String[] mItemTexts = new String[] {
            "Chair", "Baloons", "Baby", "Swingset", "Autumn", "Gun"
    };
    // 菜单图标
    private int[] mItemImgs = new int[] {
            R.drawable.circle_menu_01,
            R.drawable.circle_menu_02,
            R.drawable.circle_menu_03,
            R.drawable.circle_menu_04,
            R.drawable.circle_menu_05,
            R.drawable.circle_menu_06
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 初始化圆形菜单
        mCircleMenuLayout = (CircleMenuLayout) findViewById(R.id.cml);
        // 设置菜单数据项
        mCircleMenuLayout.setMenuItemIconsAndTexts(mItemImgs, mItemTexts);
        // 设置菜单项点击事件
        mCircleMenuLayout.setOnMenuClickListener(new CircleMenuLayout.OnMenuClickListener() {
            @Override
            public void onClick(View v, int position) {
                Toast.makeText(MainActivity.this, mItemTexts[position], Toast.LENGTH_SHORT).show();
            }
        });
    }
}
