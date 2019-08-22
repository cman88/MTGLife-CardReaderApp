package com.example.mtglife;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class oneVone extends AppCompatActivity {

    Bitmap cardImage;
    private RequestQueue mQueue;
    String scryURL;
    JsonObjectRequest request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_vone);

        //removes status and name bar
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        mQueue = Volley.newRequestQueue(this);



        //forces the app into landscape
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        ImageButton scanButton = (ImageButton) findViewById(R.id.scanbutton);

        ImageButton leftUpButton = (ImageButton) findViewById(R.id.leftupbutton);
        ImageButton leftDownButton = (ImageButton) findViewById(R.id.leftdownbutton);

        final TextView leftLifeText = (TextView) findViewById(R.id.leftlife);

        ImageButton rightUpButton = (ImageButton) findViewById(R.id.rightupbutton);
        ImageButton rightDownButton = (ImageButton) findViewById(R.id.rightdownbutton);

        final TextView rightLifeText = (TextView) findViewById(R.id.rightlife);

        rightLifeText.setRotation(180);


        leftUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int life = Integer.parseInt(leftLifeText.getText().toString());
                life++;
                leftLifeText.setText(Integer.toString(life));
            }
        });

        leftDownButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int life = Integer.parseInt(leftLifeText.getText().toString());
                life--;
                leftLifeText.setText(Integer.toString(life));
            }
        });

        rightUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int life =  Integer.parseInt(rightLifeText.getText().toString());
                life++;
                rightLifeText.setText(Integer.toString(life));
            }
        });

        rightDownButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int life = Integer.parseInt(rightLifeText.getText().toString());
                life--;
                rightLifeText.setText(Integer.toString(life));
            }
        });

        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent scanCard = new Intent();
                scanCard.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(scanCard,1);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == RESULT_OK){
            cardImage = (Bitmap) data.getExtras().get("data");
            if(cardImage != null){
                System.out.println("got image");
            }
            getCardName(cardImage);
        }
        else if (requestCode == 1 && resultCode == RESULT_CANCELED){
            Toast.makeText(this,"Operation Cancelled by User",Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(this,"Failed to capture image",Toast.LENGTH_SHORT).show();
        }
    }

    private void getCardName(Bitmap cardImage) {
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(cardImage);
        FirebaseVisionTextRecognizer detector = FirebaseVision.getInstance().getOnDeviceTextRecognizer();
        detector.processImage(image).addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
            @Override
            public void onSuccess(FirebaseVisionText firebaseVisionText) {
                if(firebaseVisionText == null){
                    System.out.println("firebaseVisionText in getCardName is null");
                }
                processTextRecognitionResult(firebaseVisionText);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                System.out.println("cant read card");
            }
        });
    }

    private void processTextRecognitionResult(FirebaseVisionText firebaseVisionText) {
        List<FirebaseVisionText.TextBlock> blocks = firebaseVisionText.getTextBlocks();
        System.out.println("block size: "+ blocks.size());
        if(blocks.size()==0){
            System.out.println("no text found");
            return;
        }

        String cardNameFromCamera = String.valueOf(blocks.get(0).getText());
        System.out.println("card name from camera: "+ cardNameFromCamera);

        jsonParse(cardNameFromCamera);

    }

    private void jsonParse(String cardNameFromCamera) {

        System.out.println("in jsonParse method");

        String url = "https://api.scryfall.com/cards/named?fuzzy="+cardNameFromCamera;

        System.out.println("jsonParse method scry url: " +url);

        request = new JsonObjectRequest(Request.Method.GET, url,null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                System.out.println("in jasonParse onResponse");

                JSONObject cardInJson = response;
                try {
                    scryURL = response.getString("scryfall_uri");
                    openMyBrowser(scryURL);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        mQueue.add(request);
    }





    private void openMyBrowser(String scryURL) {

        System.out.println("in openMyBrowser");
        //mQueue.add(request);

        Intent openBrowser = new Intent(Intent.ACTION_VIEW, Uri.parse(scryURL));
        startActivity(openBrowser);
    }
}
