package at.co.shaman.smarthome12c;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity {

    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    protected void onActivityResult( int requestCode, int resultCode, Intent data  ) {
        super.onActivityResult( requestCode, resultCode, data );

        if( resultCode == requestCode ) {
            loadSensorData();
            loadSensorData();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipelayout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshContent();
            }
        });
    }

    public void openSwitchBoiler() {
        Switch sw = findViewById(R.id.chkBoilerState);
        boolean isChecked = sw.isChecked();
        int visibility = TextView.VISIBLE;
        if( isChecked ) {
            Intent intent = new Intent(this, SwitchBoiler.class);
            startActivityForResult(intent, 3);
        }
        else {
            final SmarthomeApp app = (SmarthomeApp) getApplication();
            app.getExecutors().networkIO().execute(() -> {
                String baseUrl = "http://nas8055.synology.me:50111/boiler_off?name=admin&password=citroen2020!";
                try {
                    URL filterUrl = new URL(baseUrl);
                    HttpURLConnection connection = (HttpURLConnection) filterUrl.openConnection();
                    connection.setReadTimeout(10000); // 10 sec
                    connection.setConnectTimeout(10000); // 10 sec
                    connection.setRequestMethod("GET");
                    connection.setRequestProperty("Accept", "application/json");
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
                    loadSensorData();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    public void openRollerRules() {
        Intent intent = new Intent(this, RollerRules.class);
        startActivity(intent);
    }

    public void openPlot( String type ) {
        Intent intent = new Intent( this, AnyPlot.class );
        intent.putExtra( "name", type );
        startActivity( intent );
    }

    @Override
    protected void onStart() {
        super.onStart();

        loadSensorData();

        EditText edtBoilerTitle = findViewById( R.id.edtBoilerTitle );
        edtBoilerTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPlot( "boiler" );
            }
        });

        EditText edtEnergyTitle = findViewById( R.id.edtEnergyTitle );
        edtEnergyTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPlot( "energy" );
            }
        });

        EditText edtTemperatureTitle = findViewById( R.id.edtTemperatureTitle );
        edtTemperatureTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPlot( "temperature" );
            }
        });

        EditText edtRollers = findViewById( R.id.edtRollersTitle );
        edtRollers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openRollerRules();
            }
        });

        Switch btn = findViewById(R.id.chkBoilerState);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSwitchBoiler();
            }
        });

        Button eg1_up = findViewById( R.id.btnEG1Up );
        eg1_up.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                controlRoller( "EG1", "up" );
            }
        });

        Button eg1_stop = findViewById( R.id.btnEG1Stop );
        eg1_stop.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                controlRoller( "EG1", "stop" );
            }
        });

        Button eg1_down = findViewById( R.id.btnEG1Down );
        eg1_down.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                controlRoller( "EG1", "down" );
            }
        });

        Button eg2_up = findViewById( R.id.btnEG2Up );
        eg2_up.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                controlRoller( "EG2", "up" );
            }
        });

        Button eg2_stop = findViewById( R.id.btnEG2Stop );
        eg2_stop.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                controlRoller( "EG2", "stop" );
            }
        });

        Button eg2_down = findViewById( R.id.btnEG2Down );
        eg2_down.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                controlRoller( "EG2", "down" );
            }
        });

        Button bedroom_up = findViewById( R.id.btnBedroomUp);
        bedroom_up.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                controlRoller( "Bedroom", "up" );
            }
        });

        Button bedroom_stop = findViewById( R.id.btnBedroomStop );
        bedroom_stop.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                controlRoller( "Bedroom", "stop" );
            }
        });

        Button bedroom_down = findViewById( R.id.btnBedroomDown );
        bedroom_down.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                controlRoller( "Bedroom", "down" );
            }
        });

        Button polina_up = findViewById( R.id.btnPolinaUp );
        polina_up.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                controlRoller( "Polina", "up" );
            }
        });

        Button polina_stop = findViewById( R.id.btnPolinaStop );
        polina_stop.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                controlRoller( "Polina", "stop" );
            }
        });

        Button polina_down = findViewById( R.id.btnPolinaDown );
        polina_down.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                controlRoller( "Polina", "down" );
            }
        });

    }

    private void refreshContent() {
        runOnUiThread( new Runnable() {
            @Override
            public void run() {
                loadSensorData();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void controlRoller( String name, String mode ) {
        final SmarthomeApp app = (SmarthomeApp) getApplication();
        Context context = getApplicationContext();

        app.getExecutors().networkIO().execute(() -> {
            String baseUrl = "http://nas8055.synology.me:50111/roller/" + mode + "/" + name + "?name=admin&password=citroen2020!";
            int statusCode = -1;
            try {
                URL filterUrl = new URL(baseUrl);
                HttpURLConnection connection = (HttpURLConnection) filterUrl.openConnection();
                connection.setReadTimeout(10000); // 10 sec
                connection.setConnectTimeout(10000); // 10 sec
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Accept", "application/json");
                connection.connect();
                statusCode = connection.getResponseCode();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            boolean query = ( statusCode == 200 );
            while( query ) {
                StringBuilder content = new StringBuilder();
                try {
                    URL filterUrl = new URL("http://nas8055.synology.me:50111/rest/roller/" + name);
                    HttpURLConnection connection = (HttpURLConnection) filterUrl.openConnection();
                    connection.setReadTimeout(2000); // 2 sec
                    connection.setConnectTimeout(2000); // 2 sec
                    connection.setRequestMethod("GET");
                    connection.setRequestProperty("Accept", "application/json");
                    connection.connect();
                    statusCode = connection.getResponseCode();
                    if (statusCode != 200) {
                        query = false;
                        break;
                    }

                    InputStream is = connection.getInputStream();
                    BufferedReader reader = new BufferedReader((new InputStreamReader(is)));
                    content = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        content.append(line);
                    }
                    reader.close();
                    is.close();
                    connection.disconnect();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                int pos = 0;
                JSONObject jObject = new JSONObject();
                try {
                    jObject = new JSONObject(content.toString());
                    String position = jObject.getString("position");
                    pos = Integer.parseInt(position);
                } catch (JSONException e) {
                    query = false;
                    // Oops
                }
                final JSONObject jObject2 = jObject;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setRolloLabels( jObject2, name );
                    }
                });
                if (mode.equals("stop") || (pos == 0) || (pos == 100) )
                {
                    query = false;
                }
            }
        });
    }

    private void setDefaultValues() {
        TextView lbl;
        runOnUiThread( new Runnable() {
            @Override
            public void run() {
                TextView lbl;
                lbl = findViewById(R.id.lblBasementTemp);
                lbl.setText("-");
                lbl = findViewById(R.id.lblBasementPressure);
                lbl.setText("");
                lbl = findViewById(R.id.lblBasementDate);
                lbl.setText("");
                lbl = findViewById(R.id.lblLivingTemp);
                lbl.setText("-");
                lbl = findViewById(R.id.lblLivingPressure);
                lbl.setText("");
                lbl = findViewById(R.id.lblLivingDate);
                lbl.setText("");
                lbl = findViewById(R.id.lblBedroomTemp);
                lbl.setText("-");
                lbl = findViewById(R.id.lblBedroomPressure);
                lbl.setText("");
                lbl = findViewById(R.id.lblBedroomDate);
                lbl.setText("");
                lbl = findViewById(R.id.lblDianaTemp);
                lbl.setText("-");
                lbl = findViewById(R.id.lblDianaPressure);
                lbl.setText("");
                lbl = findViewById(R.id.lblDianaDate);
                lbl.setText("");
                lbl = findViewById(R.id.lblPolinaTemp);
                lbl.setText("-");
                lbl = findViewById(R.id.lblPolinaPressure);
                lbl.setText("");
                lbl = findViewById(R.id.lblPolinaDate);
                lbl.setText("");
                lbl = findViewById(R.id.lblOutsideTemp);
                lbl.setText("-");
                lbl = findViewById(R.id.lblOutsidePressure);
                lbl.setText("");
                lbl = findViewById(R.id.lblOutsideDate);
                lbl.setText("");
                lbl = findViewById(R.id.lblOutsideDescr);
                lbl.setText("");
                lbl = findViewById(R.id.lblBoilerTemp);
                lbl.setText("-");
                lbl = findViewById(R.id.lblBoilerDate);
                lbl.setText("-");
                lbl = findViewById(R.id.lblBoilerEnergy);
                lbl.setText("");
                lbl = findViewById(R.id.lblBoilerSensorTemp);
                lbl.setText("");
                lbl = findViewById(R.id.lblBoilerOnTime);
                lbl.setText("");
                lbl = findViewById(R.id.lblBoilerOffTime);
                lbl.setText("");
                lbl = findViewById(R.id.lblProductionNow);
                lbl.setText("-");
                lbl = findViewById(R.id.lblConsumptionNow);
                lbl.setText("-");
                lbl = findViewById(R.id.lblProduction30);
                lbl.setText("-");
                lbl = findViewById(R.id.lblConsumption30);
                lbl.setText("-");
                lbl = findViewById(R.id.lblSelfConsumption30);
                lbl.setText("-");
                lbl = findViewById(R.id.lblProductionToday);
                lbl.setText("-");
                lbl = findViewById(R.id.lblConsumptionToday);
                lbl.setText("-");
                lbl = findViewById(R.id.lblSelfConsumptionToday);
                lbl.setText("-");
                Switch btn = findViewById(R.id.chkBoilerState);
                btn.setChecked(false);
                lbl = findViewById(R.id.lblRolloEG1);
                lbl.setTextColor(0xff000000);
                lbl = findViewById(R.id.lblRolloEG2);
                lbl.setTextColor(0xff000000);
                lbl = findViewById(R.id.lblRolloBedroom);
                lbl.setTextColor(0xff000000);
                lbl = findViewById(R.id.lblRolloPolina);
                lbl.setTextColor(0xff000000);
                lbl = findViewById(R.id.lblRolloEG1Proc);
                lbl.setText("-");
                lbl = findViewById(R.id.lblRolloEG2Proc);
                lbl.setText("-");
                lbl = findViewById(R.id.lblRolloBedroomProc);
                lbl.setText("-");
                lbl = findViewById(R.id.lblRolloPolinaProc);
                lbl.setText("-");
            }
        });
    }

    private void loadSensorData() {
        final SmarthomeApp app = (SmarthomeApp)getApplication();
        Context context = getApplicationContext();
        setDefaultValues();

        app.getExecutors().networkIO().execute(() -> {
            String baseUrl = "http://nas8055.synology.me:50111/rest/general";
            try {
                URL filterUrl = new URL(baseUrl);
                HttpURLConnection connection = (HttpURLConnection) filterUrl.openConnection();
                connection.setReadTimeout(10000); // 10 sec
                connection.setConnectTimeout(10000); // 10 sec
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Accept", "application/json");
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
                            JSONObject jObject = new JSONObject(content.toString());
                            JSONObject jBoiler = jObject.getJSONObject("Boiler");
                            JSONObject jConsumption = jObject.getJSONObject("Consumption");
                            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy, hh:mm:ss");
                            SimpleDateFormat format_outside = new SimpleDateFormat("dd/MM, hh:mm:ss");
                            SimpleDateFormat format_out_s = new SimpleDateFormat("dd/MM HH:mm:ss");

                            // parse boiler data
                            TextView lbl = findViewById(R.id.lblBoilerTemp);
                            if (jBoiler.getString("energy_color").equals("green")) {
                                lbl.setTextColor(0xff00a000);
                            } else {
                                lbl.setTextColor(0xffff0000);
                            }
                            lbl.setText(jBoiler.getString("temp") + "℃");

                            String state = jBoiler.getString("energy_state");
                            Switch sw = findViewById(R.id.chkBoilerState);
                            sw.setChecked( state.equals("ON") );

                            lbl = findViewById(R.id.lblBoilerDate);
                            try {
                                Date date = format.parse(jBoiler.getString("time"));
                                lbl.setText(format_out_s.format(date));
                            } catch (ParseException e) {
                            }

                            if( jBoiler.has("hour_on") ) {
                                Integer h_in = Integer.parseInt( jBoiler.getString("hour_on" ) );
                                Integer m_in = Integer.parseInt( jBoiler.getString("minute_on" ) );
                                lbl = findViewById( R.id.lblBoilerOnTime );
                                lbl.setText( String.format( "ON: %02d:%02d", h_in, m_in ) );
                                Integer h_out = Integer.parseInt( jBoiler.getString("hour_off" ) );
                                Integer m_out = Integer.parseInt( jBoiler.getString("minute_off" ) );
                                lbl = findViewById( R.id.lblBoilerOffTime );
                                lbl.setText( String.format( "OFF: %02d:%02d", h_out, m_out ) );
                            }


                            // read temperatures and humidities
                            JSONArray jTemp = jObject.getJSONArray("Temperature");
                            for (int i = 0; i < 3; i++) {
                                Iterator<String> iter = jTemp.getJSONObject(i).keys();
                                while (iter.hasNext()) {
                                    String key = iter.next();
                                    JSONObject jNextTemp = jTemp.getJSONObject(i).getJSONObject(key);
                                    String location = jNextTemp.getString("location");
                                    TextView lblTemp;
                                    TextView lblHum;
                                    TextView lblDate;
                                    boolean found = true;
                                    if (location.equals("HomeOffice")) {
                                        lblTemp = findViewById(R.id.lblBasementTemp);
                                        lblHum = findViewById(R.id.lblBasementPressure);
                                        lblDate = findViewById(R.id.lblBasementDate);
                                    } else if (location.equals("LivingRoom")) {
                                        lblTemp = findViewById(R.id.lblLivingTemp);
                                        lblHum = findViewById(R.id.lblLivingPressure);
                                        lblDate = findViewById(R.id.lblLivingDate);
                                    } else if (location.equals("Polina")) {
                                        lblTemp = findViewById(R.id.lblPolinaTemp);
                                        lblHum = findViewById(R.id.lblPolinaPressure);
                                        lblDate = findViewById(R.id.lblPolinaDate);
                                    } else if (location.equals("Diana")) {
                                        lblTemp = findViewById(R.id.lblDianaTemp);
                                        lblHum = findViewById(R.id.lblDianaPressure);
                                        lblDate = findViewById(R.id.lblDianaDate);
                                    } else if (location.equals("Bedroom")) {
                                        lblTemp = findViewById(R.id.lblBedroomTemp);
                                        lblHum = findViewById(R.id.lblBedroomPressure);
                                        lblDate = findViewById(R.id.lblBedroomDate);
                                    } else {
                                        found = false;
                                        lblTemp = findViewById(R.id.lblBasementTemp);
                                        lblHum = findViewById(R.id.lblBasementPressure);
                                        lblDate = findViewById(R.id.lblBasementDate);
                                    }
                                    if (found) {
                                        int color = 0xff00a000;
                                        String col = jNextTemp.getString("color");
                                        if (col.equals("red")) {
                                            color = 0xffff0000;
                                        } else if (col.equals("blue")) {
                                            color = 0xff0000f0;
                                        } else if (col.equals("gray")) {
                                            color = 0xff808080;
                                        }
                                        lblTemp.setTextColor(color);
                                        lblHum.setTextColor(color);
                                        lblDate.setTextColor(color);
                                        lblTemp.setText(jNextTemp.getString("temperature") + "℃");
                                        lblHum.setText(jNextTemp.getString("humidity") + "%");
                                        try {
                                            Date date = format.parse(jNextTemp.getString("time"));
                                            lblDate.setText(format_out_s.format(date));
                                        } catch (ParseException e) {
                                        }
                                    }
                                }
                            }

                            // read weather
                            JSONObject jWeather = jObject.getJSONObject("Weather");
                            lbl = findViewById(R.id.lblOutsideTemp);
                            lbl.setTextColor(0xff000000);
                            lbl.setText(jWeather.getString("temp") + "℃");
                            lbl = findViewById(R.id.lblOutsidePressure);
                            lbl.setTextColor(0xff000000);
                            lbl.setText(jWeather.getString("humidity").toString() + "%");
                            lbl = findViewById(R.id.lblOutsideDate);
                            try {
                                Date date = format_outside.parse(jWeather.getString("time"));
                                lbl.setText(format_out_s.format(date));
                            } catch (ParseException e) {
                            }
                            lbl.setTextColor(0xff000000);
                            lbl = findViewById(R.id.lblOutsideDescr);
                            String descr = jWeather.getString("description") + ", " + jWeather.getString("wind_speed").toString() + " m/s, Sunrise: " +
                                    jWeather.getString("sunrise") + ", Sunset: " + jWeather.getString("sunset");
                            lbl.setText(descr);
                            lbl.setTextColor(0xff000000);
                        } catch (JSONException e) {
                            // Oops
                        }
                    }
                });
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            baseUrl = "http://nas8055.synology.me:50111/pv/current";
            try {
                URL filterUrl = new URL(baseUrl);
                HttpURLConnection connection = (HttpURLConnection) filterUrl.openConnection();
                connection.setReadTimeout(10000); // 10 sec
                connection.setConnectTimeout(10000); // 10 sec
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Accept", "application/json");
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
                            JSONArray jArray = new JSONArray(( content.toString() ));
                            JSONObject jObject = jArray.getJSONObject(0);
                            Double p = jObject.getDouble("p") * 0.001;
                            Double p_load = jObject.getDouble("p_load") * 0.001;
                            // parse energy consumtion
                            TextView lbl = findViewById(R.id.lblProductionNow);
                            String str = String.format("%.2f kWh", p);
                            lbl.setText( str );

                            TextView lbl2 = findViewById(R.id.lblConsumptionNow);
                            str = String.format("%.2f kWh", p_load);
                            lbl2.setText( str );

                            if( p > p_load )
                            {
                                lbl.setTextColor(0xff00a000);
                                lbl2.setTextColor(0xff00a000);
                            } else {
                                lbl.setTextColor(0xffff0000);
                                lbl2.setTextColor(0xffff0000);
                            }

                            p = jObject.getDouble("produce_30min") * 0.001;
                            p_load = jObject.getDouble("consume_30min") * 0.001;
                            Double self_cons30 = jObject.getDouble("self_consume_30min") * 0.001;
                            lbl = findViewById(R.id.lblProduction30);
                            str = String.format("%.2f kWh", p);
                            lbl.setText( str );

                            lbl2 = findViewById(R.id.lblConsumption30);
                            str = String.format("%.2f kWh", p_load);
                            lbl2.setText( str );

                            TextView lbl3 = findViewById(R.id.lblSelfConsumption30);
                            str = String.format("%.2f kWh", self_cons30 );
                            lbl3.setText( str );

                            if( p > p_load )
                            {
                                lbl.setTextColor(0xff00a000);
                                lbl2.setTextColor(0xff00a000);
                            } else {
                                lbl.setTextColor(0xffff0000);
                                lbl2.setTextColor(0xffff0000);
                            }
                            lbl3.setTextColor(0xff00a000);

                            p = jObject.getDouble("e_total") * 0.001;
                            p_load = jObject.getDouble("consume") * 0.001;
                            Double tarif_prod = jObject.getDouble("tarif_produce");
                            Double tarif_cons = jObject.getDouble("tarif_consume");
                            Double self_cons = jObject.getDouble("self_consume") * 0.001;
                            lbl = findViewById(R.id.lblProductionToday);
                            str = String.format("%.2f kWh (%.2f E)", p, tarif_prod * ( p - self_cons ) );
                            lbl.setText( str );

                            lbl2 = findViewById(R.id.lblConsumptionToday);
                            str = String.format("%.2f kWh", p_load );
                            lbl2.setText( str );

                            lbl3 = findViewById(R.id.lblSelfConsumptionToday);
                            str = String.format("%.2f kWh (%.2f E)", self_cons, self_cons * tarif_cons );
                            lbl3.setText( str );

                            if( p > p_load )
                            {
                                lbl.setTextColor(0xff00a000);
                                lbl2.setTextColor(0xff00a000);
                            } else {
                                lbl.setTextColor(0xffff0000);
                                lbl2.setTextColor(0xffff0000);
                            }
                            lbl3.setTextColor(0xff00a000);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            baseUrl = "http://nas8055.synology.me:50111/rest/rollers";
            try {
                URL filterUrl = new URL(baseUrl);
                HttpURLConnection connection = (HttpURLConnection) filterUrl.openConnection();
                connection.setReadTimeout(10000); // 10 sec
                connection.setConnectTimeout(10000); // 10 sec
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Accept", "application/json");
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
                            JSONObject jObject = new JSONObject(content.toString());
                            JSONArray jRollos = jObject.getJSONArray("Rollers");
                            for( int iRollo = 0; iRollo < jRollos.length(); iRollo++ )
                            {
                                JSONObject next = jRollos.getJSONObject(iRollo);
                                String name = next.getString("name");
                                setRolloLabels( next, name );
                            }
                        }
                        catch( JSONException e ) {
                        }
                    }
                });
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Switch sw = findViewById(R.id.chkBoilerState);
            if( sw.isChecked() ) {
                baseUrl = "http://nas8055.synology.me:50111/rest/boiler_info";
                try {
                    URL filterUrl = new URL(baseUrl);
                    HttpURLConnection connection = (HttpURLConnection) filterUrl.openConnection();
                    connection.setReadTimeout(10000); // 10 sec
                    connection.setConnectTimeout(10000); // 10 sec
                    connection.setRequestMethod("GET");
                    connection.setRequestProperty("Accept", "application/json");
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
                                JSONObject jObject = new JSONObject(content.toString());
                                String strPower = jObject.getString("power");
                                Float fltPower = Float.parseFloat( strPower );
                                TextView lblPower = findViewById( R.id.lblBoilerEnergy );
                                Integer intPower = Math.round(fltPower);
                                lblPower.setText( "Energy: " + intPower.toString() + " Wh" );
                                String strSensorTemp = jObject.getString("sensor_temperature");
                                TextView lblSensorTemp = findViewById( R.id.lblBoilerSensorTemp );
                                lblSensorTemp.setText( "Sensor t: " + strSensorTemp + "℃" );
                            } catch (JSONException e) {
                            }
                        }
                    });
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void setRolloLabels( JSONObject next, String name ) {
        try {
            TextView lblName = findViewById(R.id.lblRolloEG1);
            TextView lblProc = findViewById(R.id.lblRolloEG1Proc);
            if (name.equals("EG1")) {
                lblName = findViewById(R.id.lblRolloEG1);
                lblProc = findViewById(R.id.lblRolloEG1Proc);
            } else if (name.equals("EG2")) {
                lblName = findViewById(R.id.lblRolloEG2);
                lblProc = findViewById(R.id.lblRolloEG2Proc);
            } else if (name.equals("Bedroom")) {
                lblName = findViewById(R.id.lblRolloBedroom);
                lblProc = findViewById(R.id.lblRolloBedroomProc);
            } else if (name.equals("Polina")) {
                lblName = findViewById(R.id.lblRolloPolina);
                lblProc = findViewById(R.id.lblRolloPolinaProc);
            }
            int col = 0xff0000ff;
            if (next.getString("direction").equals("open")) {
                col = 0xff00a000;
            }
            lblName.setTextColor(col);
            lblProc.setTextColor(col);
            lblProc.setText(next.getString("position") + "%");
            lblName.invalidate();
            lblProc.invalidate();
        }
        catch( JSONException e ) {
        }
    }
}
