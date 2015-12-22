package com.vinot.parkd;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.JsonReader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LocationFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LocationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LocationFragment extends Fragment {

    private final static String TAG = LocationFragment.class.getSimpleName();
    
    private Activity mParentActivity;
    private Resources mResources;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialisation parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public LocationFragment() { /*Required empty public constructor*/ }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LocationFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LocationFragment newInstance(String param1, String param2) {
        LocationFragment fragment = new LocationFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mResources = getResources();
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mParentActivity = getActivity();
        ConnectivityManager connMgr = (ConnectivityManager) mParentActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            (new DownloadLocationTask()).execute(getString(R.string.url_location));
        } else {
            Toast.makeText(mParentActivity, getString(R.string.no_network_connection), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_location, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    /////////////////////////
    // Network Interaction //
    /////////////////////////

    private class DownloadLocationTask extends AsyncTask<String, Void, Location> {
        private ConnectivityManager connMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        private NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        @Override
        protected Location doInBackground(String... urls) {
            if (networkInfo != null && networkInfo.isConnected()) {
                return downloadUrl(urls[0]);
            } else {
                Toast.makeText(getActivity(), getString(R.string.no_network_connection), Toast.LENGTH_SHORT).show();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Location downloadedLocation) {
            super.onPostExecute(downloadedLocation);
            TextView t;
            try {
                t = (TextView) getActivity().findViewById(R.id.fragment_location_id_textview);
                t.setText(
                        String.format(mResources.getString(R.string.fragment_location_textview_id), downloadedLocation.getId())
                );
                t = (TextView) getActivity().findViewById(R.id.fragment_location_name_textview);
                t.setText(
                        String.format(mResources.getString(R.string.fragment_location_textview_name), downloadedLocation.getName())
                );
            } catch (NullPointerException e) {
                Log.wtf(TAG, e);
            }
        }

        private Location downloadUrl(String url) throws NullPointerException {
            InputStream is = null;
            try {
                HttpURLConnection conn = (HttpURLConnection) (new URL(url)).openConnection();

                conn.setReadTimeout(10000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);

                Log.d(TAG, String.format("The respone from %s is %d", url, conn.getResponseCode()));
                is = conn.getInputStream();

                return readLocation(is);
            } catch (MalformedURLException e) {
                Log.wtf(TAG, e);
            } catch (IOException e) {
                Log.wtf(TAG, e);
            } finally {
                try {
                    if (is != null) is.close();
                } catch (IOException e) {
                    Log.wtf(TAG, e);
                }
            }
            return null;
        }

        private Location readLocation(final InputStream inputStream) throws IOException, UnsupportedEncodingException {
            Location.Builder b = new Location.Builder();
            JsonReader jsonReader = new JsonReader(new InputStreamReader(inputStream, "UTF-8"));
            try {
                jsonReader.beginObject();
                while (jsonReader.hasNext()) {
                    switch(jsonReader.nextName()) {
                        case "id":
                            b.setId(jsonReader.nextInt());
                            break;
                        case "name":
                            b.setName(jsonReader.nextName());
                            break;
                        default:
                            jsonReader.skipValue();
                            break;
                    }
                }
                return b.build();
            } finally {
                jsonReader.close();
            }
            /*
            Reader reader = new InputStreamReader(inputStream, "UTF-8");
            try {
                char[] buffer = new char[10];
                reader.read(buffer);
                return new String(buffer);
            } finally {
                reader.close();
            }*/
        }
    }
}
