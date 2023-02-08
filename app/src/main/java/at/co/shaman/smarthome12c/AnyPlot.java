package at.co.shaman.smarthome12c;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class AnyPlot extends AppCompatActivity {

    private String _baseUrl;
    private String _period;

    private GraphView _graph;

    TextView edtNow, edt24h, edt48h, edt72h, edt7d, edt14d, edt30d, edtAlld, edt3m, edt6m, edt12m, edtAll;
    String _title;
    String _mode; // hourly, daily, monthly

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.any_plot);

        edtNow = findViewById( R.id.edtPlotNow );
        edt24h = findViewById( R.id.edtPlot24h );
        edt48h = findViewById( R.id.edtPlot48h );
        edt72h = findViewById( R.id.edtPlot72h );
        edt7d = findViewById( R.id.edtPlot7d );
        edt14d = findViewById( R.id.edtPlot14d );
        edt30d = findViewById( R.id.edtPlot30d );
        edtAlld = findViewById( R.id.edtPlotAlld );
        edt3m = findViewById( R.id.edtPlot3m );
        edt6m = findViewById( R.id.edtPlot6m );
        edt12m = findViewById( R.id.edtPlot12m );
        edtAll = findViewById( R.id.edtPlotAll );

        _graph = findViewById( R.id.pltAny);

        _title = getIntent().getStringExtra( "name" );
        _baseUrl = "http://nas8055.synology.me:50111/rest/getplot?type=" + _title + "&period=";
        _period = "now";
        if( _title.equals("temperature")) {
            edt24h.setTypeface(Typeface.DEFAULT_BOLD);
            _period = "24h";
            edtNow.setVisibility(TextView.INVISIBLE);
        }
        else {
            _period = "now";
            edtNow.setTypeface(Typeface.DEFAULT_BOLD);
        }
        _mode = "hourly";
        String title2 = _title.substring(0, 1).toUpperCase() + _title.substring(1);
        EditText edtTitle = findViewById( R.id.edtPlotTitle );
        edtTitle.setText( title2 );

        SwipeRefreshLayout swp = findViewById( R.id.swipePlot );
        swp.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshContent();
            }
        });

        updatePlot();

        edtNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetBold();
                edtNow.setTypeface( Typeface.DEFAULT_BOLD );
                _period = "now";
                _mode = "hourly";
                updatePlot();
            }
        });

        edt24h.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetBold();
                edt24h.setTypeface( Typeface.DEFAULT_BOLD );
                _period=  "24h";
                _mode = "hourly";
                updatePlot();
            }
        });

        edt48h.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetBold();
                edt48h.setTypeface( Typeface.DEFAULT_BOLD );
                _period=  "48h";
                _mode = "hourly";
                updatePlot();
            }
        });

        edt72h.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetBold();
                edt72h.setTypeface( Typeface.DEFAULT_BOLD );
                _period = "72h";
                _mode = "hourly";
                updatePlot();
            }
        });

        edt7d.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetBold();
                edt7d.setTypeface( Typeface.DEFAULT_BOLD );
                _period = "7d";
                _mode = "daily";
                updatePlot();
            }
        });

        edt14d.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetBold();
                edt14d.setTypeface( Typeface.DEFAULT_BOLD );
                _period = "14d";
                _mode = "daily";
                updatePlot();
            }
        });

        edt30d.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetBold();
                edt30d.setTypeface( Typeface.DEFAULT_BOLD );
                _period = "30d";
                _mode = "daily";
                updatePlot();
            }
        });

        edtAlld.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetBold();
                edtAlld.setTypeface( Typeface.DEFAULT_BOLD );
                _period = "99999d";
                _mode = "daily";
                updatePlot();
            }
        });

        edt3m.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetBold();
                edt3m.setTypeface( Typeface.DEFAULT_BOLD );
                _period = "3m";
                _mode = "monthly";
                updatePlot();
            }
        });

        edt6m.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetBold();
                edt6m.setTypeface( Typeface.DEFAULT_BOLD );
                _period = "6m";
                _mode = "monthly";
                updatePlot();
            }
        });

        edt12m.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetBold();
                edt12m.setTypeface( Typeface.DEFAULT_BOLD );
                _period = "12m";
                _mode = "monthly";
                updatePlot();
            }
        });

        edtAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetBold();
                edtAll.setTypeface( Typeface.DEFAULT_BOLD );
                _period = "9999m";
                _mode = "monthly";
                updatePlot();
            }
        });
    }

    private void updatePlot() {
        String strUrl = _baseUrl + _period;
        final SmarthomeApp app = (SmarthomeApp) getApplication();

        app.getExecutors().networkIO().execute(() -> {
            try {
                URL filterUrl = new URL(strUrl);
                HttpURLConnection connection = (HttpURLConnection) filterUrl.openConnection();
                connection.setReadTimeout(20000); // 20 sec
                connection.setConnectTimeout(20000); // 20 sec
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Accept", "application/json");
                connection.setRequestProperty("Connection", "close");
                connection.setRequestProperty("retryConnectionFailure", "true");
                connection.connect();
                int statusCode = connection.getResponseCode();
                if (statusCode != 200) {
                }

                InputStream is = connection.getInputStream();
                BufferedReader reader = new BufferedReader((new InputStreamReader(is)));
                StringBuilder content = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line);
                }
                reader.close();
                is.close();
                connection.disconnect();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            _graph.removeAllSeries();
                            _graph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
                                @Override
                                public String formatLabel(double value, boolean isValueX) {
                                    // TODO Auto-generated method stub
                                    if (isValueX) {
                                        Format formatter;
                                        if( _period.equals( "72h")) {
                                            formatter = new SimpleDateFormat("dd+HH");
                                        } else if( _mode.equals("monthly") || _period.equals("99999d") ) {
                                            formatter = new SimpleDateFormat( "dd.MM" );
                                        }
                                        else
                                        {
                                            formatter = new SimpleDateFormat("HH:mm");
                                        }
                                        return formatter.format(value);
                                    }
                                    return super.formatLabel( value, isValueX );
                                }
                            });
                            SimpleDateFormat format = new SimpleDateFormat( "dd/MM/yyyy, hh:mm:ss" );
                            JSONObject jObject = new JSONObject(content.toString());
                            JSONArray jPlots = jObject.getJSONArray("graphs");
                            double min_x = 0;
                            double max_x = 0;
                            double min_y = jObject.getDouble( "min_y" );
                            double max_y = jObject.getDouble( "max_y" );
                            String info = jObject.getString("info");
                            List<Integer> colors = new ArrayList<>();
                            colors.add( Color.rgb( 0, 0, 255 ) );
                            colors.add( Color.rgb( 0, 255, 0 ) );
                            colors.add( Color.rgb( 255, 0, 0 ) );
                            colors.add( Color.rgb( 128, 0, 200 ) );
                            colors.add( Color.rgb( 255, 0, 255 ) );
                            colors.add( Color.rgb( 0, 128, 200 ) );
                            String tempLabel = new String();
                            List<Integer> labelPos = new ArrayList<>();
                            List<Integer> labelLength = new ArrayList<>();
                            List<Integer> labelColors = new ArrayList<>();
                            for( int iPlot = 0; iPlot < jPlots.length(); iPlot++ ) {
                                JSONObject jPlot = jPlots.getJSONObject( iPlot );
                                JSONArray jX = jPlot.getJSONArray("x");
                                JSONArray jY = jPlot.getJSONArray("y");
                                DataPoint[] points = new DataPoint[jX.length()];
                                for( int i = 0; i < jX.length(); i++ ) {
                                    String strTime = jX.get(i).toString();
                                    Date date = format.parse( strTime );
                                    Calendar cal = Calendar.getInstance();
                                    cal.setTime( date );
                                    double nextY = Double.parseDouble( jY.get(i).toString() );
                                    double nextX = cal.getTimeInMillis();
                                    if( min_x == 0 ) {
                                        min_x = nextX;
                                        max_x = nextX;
                                    } else {
                                        min_x = min( min_x, nextX);
                                        max_x = max( max_x, nextX);
                                    }
                                    points[ i ] = new DataPoint( nextX, nextY );
                                }
                                if( !_period.equals("now")) {
                                    DataPoint[] points2 = new DataPoint[jX.length()];
                                    for( int i = 0; i < jX.length(); i++ ) {
                                        points2[i] = points[ jX.length() - 1 - i ];
                                    }
                                    points = points2.clone();
                                    for( int i = 1; i < jX.length(); i++ ) {
                                        if( points[i].getX() < points[i-1].getX()) {
                                            points[i] = new DataPoint( points[i-1].getX(), points[i].getY() );
                                            //Calendar cal1 = Calendar.getInstance();
                                            //cal1.setTimeInMillis( new Double( points[i-1].getX() ).longValue() );
                                            //Calendar cal2 = Calendar.getInstance();
                                            //cal2.setTimeInMillis( new Double( points[i].getX() ).longValue() );
                                            //Toast toast = Toast.makeText(app.getApplicationContext(), "test", Toast.LENGTH_LONG);
                                            //toast.show();
                                        }
                                    }
                                }
                                LineGraphSeries< DataPoint > series = new LineGraphSeries<>( points );
                                if( jPlots.length() > 1 ) {
                                    String nm = jPlot.getString("name");
                                    labelPos.add( tempLabel.length() );
                                    tempLabel += nm + "\n";
                                    labelLength.add( tempLabel.length() - 1 );
                                    int col = colors.get( iPlot % colors.size() );
                                    labelColors.add( col );
                                    series.setTitle( jPlot.getString("name") );
                                    series.setColor( col );
                                }
                                else if( _title.equals("energy")) {
                                    series.setColor( Color.rgb( 50, 200, 50 ) );
                                }
                                _graph.addSeries( series );
                            }
                            _graph.getViewport().setMinX(min_x);
                            _graph.getViewport().setMaxX(max_x);
                            _graph.getViewport().setMinY(min_y);
                            _graph.getViewport().setMaxY(max_y);

                            _graph.getViewport().setYAxisBoundsManual(true);
                            _graph.getViewport().setXAxisBoundsManual(true);
                            TextView txtInfo = findViewById( R.id.txtPlotInfo );
                            if( _title.equals("temperature") ) {
                                if( tempLabel.length() > 0 ) {
                                    tempLabel = tempLabel.substring( 0, tempLabel.length() - 1 );
                                }
                                Spannable spn = new SpannableString( tempLabel );
                                for( int i = 0; i < labelColors.size(); i++ ) {
                                    spn.setSpan( new ForegroundColorSpan( labelColors.get(i)), labelPos.get(i), labelLength.get(i), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE );
                                }
                                txtInfo.setText( spn );
                            } else {
                                txtInfo.setText(info);
                                txtInfo.setTextColor(0xff00a000);
                            }
                        } catch (Exception e) {
                            Toast toast = Toast.makeText(app.getApplicationContext(), e.toString(), Toast.LENGTH_LONG);
                            toast.show();
                        }
                    }
                });
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void resetBold() {
        edtNow.setTypeface( Typeface.DEFAULT );
        edt24h.setTypeface( Typeface.DEFAULT );
        edt48h.setTypeface( Typeface.DEFAULT );
        edt72h.setTypeface( Typeface.DEFAULT );
        edt7d.setTypeface( Typeface.DEFAULT );
        edt14d.setTypeface( Typeface.DEFAULT );
        edt30d.setTypeface( Typeface.DEFAULT );
        edtAlld.setTypeface( Typeface.DEFAULT );
        edt3m.setTypeface( Typeface.DEFAULT );
        edt6m.setTypeface( Typeface.DEFAULT );
        edt12m.setTypeface( Typeface.DEFAULT );
        edtAll.setTypeface( Typeface.DEFAULT );
    }

    private void refreshContent() {
        runOnUiThread( new Runnable() {
            @Override
            public void run() {
                SwipeRefreshLayout swp = findViewById( R.id.swipePlot );
                updatePlot();
                swp.setRefreshing(false);
            }
        });
    }
}
