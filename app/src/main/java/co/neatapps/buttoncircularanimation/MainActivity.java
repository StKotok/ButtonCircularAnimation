package co.neatapps.buttoncircularanimation;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        findViewById(R.id.btn).setOnClickListener(this);
        findViewById(R.id.text).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
//            case R.id.btn:
//                // todo
//                break;
            case R.id.text:
                // todo
                break;
        }
    }

}