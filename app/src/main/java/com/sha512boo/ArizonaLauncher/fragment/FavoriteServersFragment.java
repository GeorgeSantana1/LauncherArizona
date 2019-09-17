package com.sha512boo.ArizonaLauncher.fragment;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.sha512boo.ArizonaLauncher.R;
import com.sha512boo.ArizonaLauncher.adapter.AdapterFavorite;
import com.sha512boo.ArizonaLauncher.model.Server;
import com.sha512boo.ArizonaLauncher.utils.PermissionUtil;
import com.sha512boo.ArizonaLauncher.utils.Tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FavoriteServersFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FavoriteServersFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FavoriteServersFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private boolean on_permission_result = false;

    private RecyclerView recyclerView;
    private AdapterFavorite adapterFavorite;
    public ArrayList<Server> server;

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private FloatingActionButton addServer;

    public FavoriteServersFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FavoriteServersFragment.
     */

    public static FavoriteServersFragment newInstance(String param1, String param2) {
        FavoriteServersFragment fragment = new FavoriteServersFragment();
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

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onResume() {
        super.onResume();

        // permission checker for android M or higher
        if (Tools.needRequestPermission() && !on_permission_result) {
            String[] permission = PermissionUtil.getDeniedPermission(getActivity());
            if (permission.length != 0) {
                requestPermissions(permission, 200);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorite_servers, container,false);

        final File sampfile = new File(getContext().getExternalCacheDir(),"samp_favorite.txt");

        addServer = (FloatingActionButton) view.findViewById(R.id.addServer);
        addServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity(), R.style.CustomAlertDialog);
                View mView = getLayoutInflater().inflate(R.layout.dialog_addserver, null);
                final EditText mIpSamp = (EditText) mView.findViewById(R.id.host);
                Button mTimerOk = (Button) mView.findViewById(R.id.ok);
                TextView mTimerCancel = (TextView) mView.findViewById(R.id.cancel);
                mBuilder.setView(mView);
                final AlertDialog sampDialog = mBuilder.create();

                ClipboardManager clipboardManager = (ClipboardManager)getLayoutInflater().getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clipData = clipboardManager.getPrimaryClip();
                ClipData.Item item = clipData.getItemAt(0);
                String text = item.getText().toString();
                mIpSamp.setText(text);

                mTimerOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick (View view) {

                        Pattern pattern = Pattern.compile("^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5]):((6553[0-5])|(655[0-2][0-9])|(65[0-4][0-9]{2})|(6[0-4][0-9]{3})|([1-5][0-9]{4})|([0-5]{0,5})|([0-9]{1,4}))$");
                        Matcher matcher = pattern.matcher(mIpSamp.getText().toString());
                        if(matcher.find()){
                            try {

                                BufferedReader in = new BufferedReader(new FileReader(sampfile));
                                int ex = 0;
                                String line;
                                while ((line = in.readLine()) != null){
                                    if (mIpSamp.getText().toString().equals(line)){
                                        Toast.makeText(getActivity(), R.string.server_listed, Toast.LENGTH_SHORT).show();
                                        ex = 1;
                                    }
                                }
                                if (ex == 0){
                                    sampDialog.dismiss();
                                    String[] part = mIpSamp.getText().toString().split(":");
                                    String host = part[part.length-2];
                                    int port = Integer.parseInt(part[part.length-1]);

                                    BufferedWriter writer = null;
                                    writer = new BufferedWriter(new FileWriter(sampfile, true));
                                    writer.write(mIpSamp.getText().toString() + System.getProperty("line.separator"));
                                    writer.close();

                                    server.add(new Server("0",host,port,"Retrieving info...","", "",0,"0","0"));
                                }
                                in.close();
                            } catch (MalformedURLException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }else {
                            Toast.makeText(getActivity(), R.string.server_valid, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                mTimerCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick (View view) { sampDialog.dismiss();
                    }
                });
                sampDialog.show();
            }
        });

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerFavorite);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        server = new ArrayList<Server>();
        adapterFavorite = new AdapterFavorite(getActivity(), getActivity(), recyclerView, server);
        recyclerView.setAdapter(adapterFavorite);
        recyclerView.setNestedScrollingEnabled(false);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    BufferedReader in = new BufferedReader(new FileReader(sampfile));
                    String line;
                    while ((line = in.readLine()) != null){
                        String[] part = line.split(":");
                        String host = part[part.length-2];
                        int port = Integer.parseInt(part[part.length-1]);
                        Log.i("server ",line);
                        server.add(new Server("0",host,port,"Retrieving info...","", "",0,"0","0"));
                    }
                    in.close();

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() { adapterFavorite.notifyDataSetChanged();
                        }
                    });
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();


        return view;
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
