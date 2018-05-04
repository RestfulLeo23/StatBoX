package beaux.thomas.base;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class GPS extends AppCompatActivity {
    public Button RecordButton;
    private Button b;
    private TextView t;
    private LocationManager locationManager;
    private LocationListener listener;
    double lat1,lon1,lat2,lon2;
    int ResultNum=0;
    float result;




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_gps);
        RecordButton = (Button)findViewById(R.id.RecordButton);
        t = (TextView) findViewById(R.id.textView);
        b = (Button) findViewById(R.id.button);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        final Intent resultIntent = new Intent(this, Input.class);

        RecordButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){

                result=Math.round(result*100)/100;
                String SResult = String.valueOf(result);
                resultIntent.putExtra("GPS", SResult);
                setResult(1, resultIntent);
                finish();
            }
        });


        listener = new LocationListener() {

            @Override
            public void onLocationChanged(Location location) {
                if(ResultNum==0){
                    lat1=location.getLatitude();
                    lon1=location.getLongitude();
                }
                lat2=location.getLatitude();
                lon2=location.getLongitude();

                Location loc1 = new Location("");
                loc1.setLatitude(lat1);
                loc1.setLongitude(lon1);

                Location loc2 = new Location("");
                loc2.setLatitude(lat2);
                loc2.setLongitude(lon2);

                float distanceInMeters = loc1.distanceTo(loc2);
                result+=distanceInMeters;
                result*=3.28;







                t.append("\n " + location.getLongitude() + " " + location.getLatitude()+"\n"+"Distance Traveled "+"\n"+Math.round(result*100)/100+"ft");
                ResultNum++;
                lat1=lat2;
                lon1=lon2;
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s ) {

            }

            @Override
            public void onProviderDisabled(String s) {

                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(i);
            }
        };

        configure_button();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 10:
                configure_button();
                break;
            default:
                break;
        }
    }

    void configure_button(){
        // first check for permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.INTERNET}
                        ,10);
            }
            return;
        }
        // this code won't execute IF permissions are not allowed, because in the line above there is return statement.
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //noinspection MissingPermission
                locationManager.requestLocationUpdates("gps", 10000, 0, listener);
            }
        });
    }
}

