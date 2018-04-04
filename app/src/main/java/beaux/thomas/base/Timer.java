package beaux.thomas.base;


import android.content.Context;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Timer extends AppCompatActivity {

   Button StartButton,PauseButton,LapButton;
    TextView txtTimer;
    Handler customHandler = new Handler();
    LinearLayout container;

    long startTime=0L,timeInMilliseconds=0L,timeSwapBuff=0L,updateTime=0L;

    Runnable updateTimerThread = new Runnable(){
        @Override
        public void run()   {
            timeInMilliseconds = SystemClock.uptimeMillis()-startTime;
            updateTime=timeSwapBuff+timeInMilliseconds;
            int secs=(int)(updateTime/1000);
            int mins=secs/60;
            int hours=mins/60;
            secs%=60;
            int milliseconds=(int)(updateTime%1000);
            txtTimer.setText(""+hours+":"+mins+":"+String.format("%2d",secs)+":"
                    +String.format("%3d",milliseconds));
            customHandler.postDelayed(this,0);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.timer);

        StartButton = (Button)findViewById(R.id.StartButton);
        PauseButton = (Button)findViewById(R.id.PauseButton);
        LapButton = (Button)findViewById(R.id.LapButton);
        txtTimer= (TextView) findViewById(R.id.timer);
        container = (LinearLayout)findViewById(R.id.container);

        StartButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                startTime=SystemClock.uptimeMillis();
                customHandler.postDelayed(updateTimerThread,0);


            }
        });
        PauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                customHandler.postDelayed(updateTimerThread,0);
                customHandler.removeCallbacks(updateTimerThread);
                LayoutInflater inflater = (LayoutInflater)getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View addView = inflater.inflate(R.layout.row,null);
                TextView txtValue = (TextView)addView.findViewById(R.id.txtContent);
                txtValue.setText(txtTimer.getText());
                container.addView(addView);



            }
        });

        LapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater inflater = (LayoutInflater)getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View addView = inflater.inflate(R.layout.row,null);
                TextView txtValue = (TextView)addView.findViewById(R.id.txtContent);
                txtValue.setText(txtTimer.getText());
                container.addView(addView);
            }
        });
    }
}
