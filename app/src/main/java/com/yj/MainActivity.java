package com.yj;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import com.yj.views.Rotate3dLayout;


public class MainActivity extends ActionBarActivity {

    private Rotate3dLayout rotate3dLayout;
    private TextView textView1;
    private TextView textView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rotate3dLayout = (Rotate3dLayout) findViewById(R.id.layout);
        textView1 = (TextView) findViewById(R.id.text1);
        textView2 = (TextView) findViewById(R.id.text2);
        rotate3dLayout.setRecycle(true);
        rotate3dLayout.setRotate(true);
        rotate3dLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                textView2.setText("angle==" + rotate3dLayout.getAngle());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case R.id.setting1:
                rotate3dLayout.setRecycle(true);
                rotate3dLayout.setRotate(true);
                textView1.setText(R.string.action_setting1);
                break;
            case R.id.setting2:
                rotate3dLayout.setRecycle(false);
                rotate3dLayout.setRotate(true);
                textView1.setText(R.string.action_setting2);
                break;
            case R.id.setting3:
                rotate3dLayout.setRecycle(true);
                rotate3dLayout.setRotate(false);
                textView1.setText(R.string.action_setting3);
                break;
            case R.id.setting4:
                rotate3dLayout.setRecycle(false);
                rotate3dLayout.setRotate(false);
                textView1.setText(R.string.action_setting4);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
