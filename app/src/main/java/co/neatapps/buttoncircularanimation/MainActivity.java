package co.neatapps.buttoncircularanimation;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import co.neatapps.buttoncircularanimation.view.ProgressButtonView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ProgressButtonView btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.text).setOnClickListener(this);
        btn = (ProgressButtonView) findViewById(R.id.dynamicArcView);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_start_forward:
                btn.startCircleSpinnerForward(ProgressButtonView.ACTIVATION_DELAY);
                break;

            case R.id.btn_start_back:
                btn.startCircleSpinnerBack(ProgressButtonView.ACTIVATION_DELAY);
                break;

            case R.id.btn_stop:
                btn.stopCircleSpinner();
                break;

            case R.id.text:
                // todo
                break;
        }
    }

}