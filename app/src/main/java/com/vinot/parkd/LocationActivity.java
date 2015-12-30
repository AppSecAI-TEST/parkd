package com.vinot.parkd;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class LocationActivity extends AppCompatActivity implements OnMapReadyCallback {
    public static final String EXTRA_PRICE = LocationActivity.class.getCanonicalName() + ".EXTRA_PRICE";
    public static final String EXTRA_LOCATION = LocationActivity.class.getCanonicalName() + ".EXTRA_LOCATION";
    public static final String ACTION_PAYMENT = LocationActivity.class.getCanonicalName() + ".ACTION_PAYMENT";

    private static final String TAG = LocationActivity.class.getSimpleName();
    private static final float ZOOM_LEVEL = 15f;

    private Location mLocation = null;
    private GoogleMap mMap = null;
    //private byte[] mTagId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(getIntent().getAction())) {
            Log.d(TAG, "Tag detected");
            NdefMessage messages[] = produceNdefMessages(getIntent().getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES));
            //mTagId = mIntent.getByteArrayExtra(NfcAdapter.EXTRA_ID); // todo use this instead of *any* identifying NDEF record
            if (messages != null && messages.length == 1) {
                (new DownloadLocationTask()).execute(messages[0].getRecords()[0].toUri().toString());
            } else {
                Log.wtf(TAG, "Malformed NDEF message on tag.");
            }
        }

        NumberPicker numberPicker = (NumberPicker) findViewById(R.id.numberpicker_park);
        numberPicker.setMaxValue(10); // todo obtain this from the Location object
        numberPicker.setMinValue(1); // todo obtain this from the Location object
        numberPicker.setFormatter(new NumberPicker.Formatter() {
            @Override
            public String format(int value) {
                return String.format("%02d", value);
            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setIndoorEnabled(false);
        if (mLocation != null) {
            updateMap(mMap, mLocation);
        }
    }

    private void updateMap(GoogleMap googleMap, Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        googleMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title(String.format(getString(R.string.activity_location_marker_text), 2, location.getCurrentPrice())) // todo set max time based on a Location property
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
        );
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, ZOOM_LEVEL));
    }

    /**
     * Produce an array of NdefMessages from a Parcelable array.
     * At this stage Android does not even support multiple messages, so this is overkill
     * http://stackoverflow.com/questions/17496811/android-putting-multiple-ndef-messages-in-one-nfc-tag
     *
     * @param rawMsgs
     * @return
     */
    private NdefMessage[] produceNdefMessages(final Parcelable[] rawMsgs) {
        NdefMessage messages[] = null;
        if (rawMsgs != null) {
            messages = new NdefMessage[rawMsgs.length];
            for (int k = 0; k < rawMsgs.length; k++) {
                messages[k] = (NdefMessage) rawMsgs[k];
            }
        }
        return messages;
    }

    private class DownloadLocationTask extends AsyncTask<String, Void, Location> {
        private final int READ_TIMEOUT = 10000; /* milliseconds */
        private final int CONNECT_TIMEOUT = 15000; /* milliseconds */
        private final String REQUEST_METHOD = "GET";
        private final boolean DO_INPUT = true;

        private ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        private NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        @Override
        protected Location doInBackground(String... urls) {
            if (networkInfo != null && networkInfo.isConnected()) {
                return downloadUrl(urls[0]);
            } else {
                Toast.makeText(LocationActivity.this, getString(R.string.no_network_connection), Toast.LENGTH_SHORT).show();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Location downloadedLocation) {
            super.onPostExecute(downloadedLocation);
            LocationActivity.this.mLocation = downloadedLocation;
            try {
                LocationActivity.this.setTitle(String.format(
                                getString(R.string.activity_location_title),
                                mLocation.getName(), mLocation.getSuburb(), mLocation.getState(), mLocation.getPostcode())
                );

                findViewById(R.id.activity_location_linearlayout).setVisibility(View.VISIBLE);

                final Button b = (Button) findViewById(R.id.button_payment);
                final TimePicker timePicker = (TimePicker) findViewById(R.id.timepicker);
                b.setText(String.format(getString(R.string.button_payment), 0f));
                b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int hourOfDay = timePicker.getHour();
                        int minute = timePicker.getMinute();
                        Intent paymentActivityIntent = new Intent(LocationActivity.this, PaymentActivity.class);
                        paymentActivityIntent.setAction(ACTION_PAYMENT);
                        paymentActivityIntent.putExtra(EXTRA_PRICE, hourOfDay * mLocation.getCurrentPrice() + (minute / 60f) * mLocation.getCurrentPrice());
                        paymentActivityIntent.putExtra(EXTRA_LOCATION, mLocation);
                        startActivity(paymentActivityIntent);
                    }
                });

                timePicker.setIs24HourView(true);
                timePicker.setHour(0);
                timePicker.setMinute(0); // todo set max time based on a Location property
                timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
                    @Override
                    public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                        float price = hourOfDay * mLocation.getCurrentPrice() + (minute / 60f) * mLocation.getCurrentPrice();
                        b.setText(String.format(getString(R.string.button_payment), price));
                    }
                });

                NumberPicker numberPicker = (NumberPicker) findViewById(R.id.numberpicker_park);
                numberPicker.setMaxValue(mLocation.getNumberOfParks());
                numberPicker.setMinValue(1);

                if (LocationActivity.this.mMap != null) {
                    LocationActivity.this.updateMap(mMap, mLocation);
                }
            } catch (NullPointerException e) {
                Log.wtf(TAG, e);
            }
        }

        private Location downloadUrl(String url) throws NullPointerException {
            try {
                HttpURLConnection conn = (HttpURLConnection) (new URL(url)).openConnection();
                conn.setReadTimeout(READ_TIMEOUT);
                conn.setConnectTimeout(CONNECT_TIMEOUT);
                conn.setRequestMethod(REQUEST_METHOD);
                conn.setDoInput(DO_INPUT);

                Log.d(TAG, String.format("The respone from %s is %d", url, conn.getResponseCode()));

                return readLocation(conn.getInputStream());
            } catch (MalformedURLException e) {
                Log.wtf(TAG, e);
            } catch (IOException e) {
                Log.wtf(TAG, e);
            }
            return null;
        }

        private Location readLocation(final InputStream inputStream) throws IOException, UnsupportedEncodingException {
            Location.Builder b = new Location.Builder();
            JsonReader jsonReader = new JsonReader(new InputStreamReader(inputStream, "UTF-8"));
            try {
                jsonReader.beginObject();
                while (jsonReader.hasNext()) {
                    switch (jsonReader.nextName()) {
                        case "id":
                            b.setId(jsonReader.nextInt());
                            break;
                        case "name":
                            b.setName(jsonReader.nextString());
                            break;
                        case "latitude":
                            b.setLatitude(jsonReader.nextDouble());
                            break;
                        case "longitude":
                            b.setLongitude(jsonReader.nextDouble());
                            break;
                        case "number_of_parks":
                            b.setNumberOfParks(jsonReader.nextInt());
                            break;
                        case "suburb":
                            b.setSuburb(jsonReader.nextString());
                            break;
                        case "state":
                            b.setState(jsonReader.nextString());
                            break;
                        case "postcode":
                            b.setPostcode(jsonReader.nextInt());
                            break;
                        default: // todo logic to read and parse current price
                            jsonReader.skipValue();
                            break;
                    }
                }
                jsonReader.endObject();
                return b.build();
            } finally {
                jsonReader.close();
            }
        }
    }
}
