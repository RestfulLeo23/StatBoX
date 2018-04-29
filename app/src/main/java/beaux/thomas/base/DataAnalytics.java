package beaux.thomas.base;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.ValueDependentColor;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class DataAnalytics extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        // Get the activity and stattype passed in through the intent
        Intent intent = getIntent();
        String activityName = intent.getStringExtra("Activity");
        String statTypeName = intent.getStringExtra("Stat");

        List<String>[] tupList = DatabaseHelper.getsInstance(getApplicationContext()).grabActivity_Stat_withDate(activityName, statTypeName);

        GraphView graph = (GraphView) findViewById(R.id.graph);
        // convert the list of data tuples into a map with key= each unique X axis element and value= sum of Y axis elements sharing X axis element
        Map<Date, Integer> map = new TreeMap<Date, Integer>();
        SimpleDateFormat dFormat = new SimpleDateFormat("yyyy-dd-MM");

        //this is for test purposes
        //tupList[1].set(0, "2018-03-05");
        //tupList[1].set(1, "2018-02-05");
        //tupList[1].set(2, "2018-07-05");
        for(int i = 0; i < tupList[0].size(); i++){
            String y_axis_el = tupList[0].get(i);
            try {
                Date x_axis_el = dFormat.parse(tupList[1].get(i));
                // If the map already has seen this x-axis element, increase the existing value in map
                if(map.containsKey(x_axis_el)){
                    map.put(x_axis_el, map.get(x_axis_el) + Integer.parseInt(y_axis_el));
                }
                // Otherwise, the x-axis element has been seen; make a new map entry
                else{
                    map.put(x_axis_el, Integer.parseInt(y_axis_el));
                }
            } catch(java.text.ParseException e) {
                e.printStackTrace();
            }
        }

        BarGraphSeries<DataPoint> series = new BarGraphSeries<>();
                                            //new DataPoint(4, 6)
        // For each entry in map, create an datapoint in the series
        for(Map.Entry<Date, Integer> entry : map.entrySet()) {
            // add 12 hours to each date to center them
            Date key = entry.getKey();
            Calendar cal = Calendar.getInstance();
            cal.setTime(key);
            cal.add(Calendar.HOUR_OF_DAY, 12);

            int value = entry.getValue();
            series.appendData(new DataPoint(cal.getTime(), value), true, map.size());
        }
        graph.addSeries(series);

        // styling
        series.setValueDependentColor(new ValueDependentColor<DataPoint>() {
            @Override
            public int get(DataPoint data) {
                return Color.rgb((int) data.getX()*255/4, (int) Math.abs(data.getY()*255/6), 100);
            }
        });

        // set date label formatter
        graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(this));
        //graph.getGridLabelRenderer().setNumHorizontalLabels(map.size());

        // set manual x bounds to have nice steps
        graph.getViewport().setMinX(series.getLowestValueX() - (series.getHighestValueX()-series.getLowestValueX())/5);
        graph.getViewport().setMaxX(series.getHighestValueX() + (series.getHighestValueX()-series.getLowestValueX())/5);
        graph.getViewport().setXAxisBoundsManual(true);

        // set manual y bounds
        graph.getViewport().setMinY(0);
        graph.getViewport().setMaxY(series.getHighestValueY());
        graph.getViewport().setYAxisBoundsManual(true);

        // as we use dates as labels, the human rounding to nice readable numbers
        // is not necessary
        graph.getGridLabelRenderer().setHumanRounding(false);

        series.setSpacing(50);

        // set axis labels for graph
        GridLabelRenderer gridLabel = graph.getGridLabelRenderer();
        gridLabel.setHorizontalAxisTitle("Date");
        gridLabel.setVerticalAxisTitle(statTypeName);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

}
