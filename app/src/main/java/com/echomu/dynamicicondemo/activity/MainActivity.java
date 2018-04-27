package com.echomu.dynamicicondemo.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.echomu.dynamicicon.Colors;
import com.echomu.dynamicicon.Icons;
import com.echomu.dynamicicon.DynamicIcon;
import com.echomu.dynamicicondemo.R;

public class MainActivity extends AppCompatActivity {
    private ImageView ivMain01,ivMain02,ivMain03;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ivMain01 = findViewById(R.id.iv_main_01);
        ivMain02 = findViewById(R.id.iv_main_02);
        ivMain03 = findViewById(R.id.iv_main_03);

        ivMain01.setTag(Icons.HELP);
        ivMain02.setTag(Icons.OUTPUT);
        ivMain03.setTag(Icons.SAVE);

        //加载配置数据
        String jsonStr = "[{\"tag\":\"help\",\"resId\":\"ic_help\"},{\"tag\":\"output\",\"resId\":\"ic_output\"},{\"tag\":\"save\",\"resId\":\"ic_save\"}]";

        DynamicIcon.getInstance().init(this,jsonStr);
        ivMain01.setImageBitmap(DynamicIcon.getInstance().setIconWithBitmap((String) ivMain01.getTag()));
        ivMain02.setImageBitmap(DynamicIcon.getInstance().setIconWithBitmap((String) ivMain02.getTag()));
        ivMain03.setImageBitmap(DynamicIcon.getInstance().setIconWithBitmap((String) ivMain03.getTag()));
    }

    public void onClick(View view) {
        String jsonStr2 = "[{\"tag\":\"help\",\"resId\":\"ic_save\"},{\"tag\":\"output\",\"resId\":\"ic_output\"},{\"tag\":\"save\",\"resId\":\"ic_help\"}]";

        DynamicIcon.getInstance().init(MainActivity.this,jsonStr2,Colors.DodgerBlue);
        ivMain01.setImageBitmap(DynamicIcon.getInstance().setIconWithBitmap((String) ivMain01.getTag(), Colors.Blue));
        ivMain02.setImageBitmap(DynamicIcon.getInstance().setIconWithBitmap((String) ivMain02.getTag()));
        ivMain03.setImageBitmap(DynamicIcon.getInstance().setIconWithBitmap((String) ivMain03.getTag(),Colors.DeepSkyBlue));
    }

}
