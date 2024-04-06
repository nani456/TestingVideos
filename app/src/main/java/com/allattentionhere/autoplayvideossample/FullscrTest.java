package com.allattentionhere.autoplayvideossample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.allattentionhere.autoplayvideossample.Activity.MainActivity;

public class FullscrTest extends Activity {
    TextView withorwthoutinternt,offlinedownloadedonly;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fullscreenmode);

        withorwthoutinternt = findViewById(R.id.v1);
        offlinedownloadedonly = findViewById(R.id.v2);

        withorwthoutinternt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               /* Intent i = new Intent(FullscrTest.this,MainActivityNew.class);
                startActivity(i);
                finish();
         */   }
        });

        offlinedownloadedonly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(FullscrTest.this, MainActivity.class);
                startActivity(i);
                finish();

            }
        });

    }
}
