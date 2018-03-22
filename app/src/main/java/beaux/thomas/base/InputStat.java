package beaux.thomas.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

public class InputStat extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stat);

    }
     public void Input_Stat(View view){
         EditText StatV = (EditText) findViewById(R.id.Svalue);
         EditText StatT = (EditText) findViewById(R.id.Stype);

        String[] New_Stat = new String[2];

        New_Stat[0]= StatT.getText().toString();
        New_Stat[1]= StatV.getText().toString();
        Intent resultIntent = new Intent(this, ViewActivity.class);

        resultIntent.putExtra("STAT",New_Stat);
        setResult(1,resultIntent);
        finish();

     }



}
