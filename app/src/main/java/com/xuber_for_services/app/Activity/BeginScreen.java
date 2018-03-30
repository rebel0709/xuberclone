package com.xuber_for_services.app.Activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.splunk.mint.Mint;
import com.xuber_for_services.app.R;


public class BeginScreen extends AppCompatActivity {

    TextView enter_ur_mailID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mint.setApplicationEnvironment(Mint.appEnvironmentStaging);
        Mint.initAndStartSession(this.getApplication(), "06fb8b21");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
            //getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        }
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_begin);

        /*enter_ur_mailID = (TextView)findViewById(R.id.enter_ur_mailID);
        enter_ur_mailID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {*/
                Intent mainIntent = new Intent(BeginScreen.this, SignIn.class);
             //   mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(mainIntent);
                finish();
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
               // BeginScreen.this.finish();
          /*  }
        });*/

    }


}
