package com.sha512boo.ArizonaLauncher.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import androidx.constraintlayout.solver.Cache;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.sha512boo.ArizonaLauncher.R;
import com.sha512boo.ArizonaLauncher.SampQuery;
import com.sha512boo.ArizonaLauncher.adapter.AdapterServer;
import com.sha512boo.ArizonaLauncher.model.Server;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HostedServersFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HostedServersFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HostedServersFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private RecyclerView recyclerView;
    private AdapterServer adapterServer;
    ArrayList<Server> server;

    private String mParam1;
    private String mParam2;

    private ProgressBar progress;

    private OnFragmentInteractionListener mListener;

    public HostedServersFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HostedServersFragment.
     */
    public static HostedServersFragment newInstance(String param1, String param2) {
        HostedServersFragment fragment = new HostedServersFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_hosted_servers, container,false);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        progress = (ProgressBar) view.findViewById(R.id.progressBar);

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerHosted);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        server = new ArrayList<Server>();

        adapterServer = new AdapterServer(getActivity(), getActivity(), recyclerView, server);
        recyclerView.setAdapter(adapterServer);
        recyclerView.setNestedScrollingEnabled(false);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //link txt file
                    URL url = new URL("http://serapp.msach.ru/samp/ser.txt");
                    BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));

                    String line;
                    while ((line = in.readLine()) != null){
                        String[] part = line.split(":");
                        String host = part[part.length-2];
                        int port = Integer.parseInt(part[part.length-1]);
                        Thread.sleep(1000);
                        new GetInfoServer(host, port).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    }
                    in.close();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();




        return view;
    }


    class GetInfoServer extends AsyncTask<String, Void, Void>  {

        String host;
        int port;
        final String[] serverInfo = new String[6];

        public GetInfoServer(String first, int second) {
            host = first;
            port = second;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... strings) {

            try {
                SampQuery query = new SampQuery(host,port);
                int ping = query.getPing();
                query.close();
                Document doc = Jsoup.connect("http://serapp.msach.ru/samp/getserverinfo.php?ip=" + host + "&port=" + port).timeout(10000).get();
                Element hostname = doc.getElementById("title");
                Element mod = doc.getElementById("gamemode");
                Element language = doc.getElementById("lang");
                Element players = doc.getElementById("players");
                Element maxplayers = doc.getElementById("maxplayers");
                Element password = doc.getElementById("password");

                serverInfo[0] = password.text(); //password
                serverInfo[1] = hostname.text(); //hostname
                serverInfo[2] = mod.text(); //game mod
                serverInfo[3] = players.text();//platers
                serverInfo[4] = maxplayers.text();//maxplayers
                serverInfo[5] = language.text(); //language
                if (!serverInfo[1].isEmpty()){
                    server.add(new Server(serverInfo[0],host,port,serverInfo[1],serverInfo[2],serverInfo[5],ping,serverInfo[3],serverInfo[4]));
                }
                Log.i("SAMP",serverInfo[1]);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progress.setVisibility(View.GONE);
            adapterServer.notifyDataSetChanged();
        }
    }

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
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
