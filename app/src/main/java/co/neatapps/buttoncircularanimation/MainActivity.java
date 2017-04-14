package co.neatapps.buttoncircularanimation;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import co.neatapps.buttoncircularanimation.view.ProgressIndicator;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ProgressIndicator btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.text).setOnClickListener(this);
        btn = (ProgressIndicator) findViewById(R.id.dynamicArcView);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_start_forward:
//                btn.animateGrowing(true);
                btn.startIndeterminantAnimation();
                break;

            case R.id.btn_start_back:
                btn.animateGrowing(false);
                break;

            case R.id.btn_stop:
                btn.cancelAnimation();
                break;

            case R.id.text:
                // todo
                break;
        }
    }

}