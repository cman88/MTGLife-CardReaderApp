package com.example.mtglife;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Point;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private Point ptSize = new Point();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //removes status and name bar
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        //forces portrait mode
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        addTouchListener();

    }

    private void addTouchListener(){
        ImageView image = (ImageView) findViewById(R.id.mainactivityimage);
        image.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                float x = motionEvent.getX();
                float y = motionEvent.getY();

                getWindowManager().getDefaultDisplay().getSize(ptSize);
                int screenHeight = ptSize.y;

                System.out.println("screen height size"+ screenHeight);

                if(y >= 0 && y <= (screenHeight)/3){
                    System.out.println("x coordinate: "+ x +"and y coordinate: " +y);
                    System.out.println("going to 1v1 Activity");

                    Intent open = new Intent(getApplicationContext(),oneVone.class);
                    startActivity(open);
                }
                else if( y > (screenHeight)/3 && y <= 2*((screenHeight)/3) ){
                    System.out.println("x coordinate: "+ x +"and y coordinate: " +y);
                    System.out.println("going to 4players Activity");
                }
                else{
                    System.out.println("x coordinate: "+ x +"and y coordinate: " +y);
                    System.out.println("going to 6players Activity");
                }



                return false;
            }
        });
    }
}
