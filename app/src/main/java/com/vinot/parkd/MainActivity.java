package com.vinot.parkd;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements LocationFragment.OnFragmentInteractionListener {

    private final static String TAG = MainActivity.class.getSimpleName();

    private HttpService mHttpService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
    }

    @Override
    protected void onStart() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            bindService(new Intent(this, HttpService.class), mServiceConnection, BIND_AUTO_CREATE);
        } else {
            Toast.makeText(MainActivity.this, getString(R.string.no_network_connection), Toast.LENGTH_SHORT).show();
        }
        super.onStart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // binding
    private boolean mBound = false;
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            if (service instanceof HttpService.HttpServiceBinder) {
                mHttpService = ((HttpService.HttpServiceBinder) service).getBoundService();
                Log.d(TAG, "Successfully bound to HttpService");
                mBound = true;
            } else {
                Log.wtf(TAG, new ClassCastException("service IBinder is not an instance of HttpService.HttpServiceBinder"));
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBound = false;
            Log.wtf(TAG, "Unexpected disconnection from HttpService");
        }
    };

    ///////////////
    // Fragments //
    ///////////////

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
