package com.sha512boo.ArizonaLauncher.adapter;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sha512boo.ArizonaLauncher.R;
import com.sha512boo.ArizonaLauncher.SampQuery;
import com.sha512boo.ArizonaLauncher.generalData;
import com.sha512boo.ArizonaLauncher.model.Server;
import com.sha512boo.ArizonaLauncher.utils.Tools;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

public class AdapterFavorite extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;

    private List<Server> items = new ArrayList<>();

    private boolean loading;
    private AdapterFavorite.OnLoadMoreListener onLoadMoreListener;

    private Activity act;
    private Context ctx;
    private AdapterFavorite.OnItemClickListener mOnItemClickListener;

    private TextView pingtext;

    public interface OnItemClickListener {
        void onItemClick(View view, Server obj, int position);
    }

    public void setOnItemClickListener(final AdapterFavorite.OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    public AdapterFavorite(Activity activity, Context context, RecyclerView view, List<Server> items) {
        this.items = items;
        act = activity;
        ctx = context;
        lastItemViewDetector(view);
    }

    public class OriginalViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public LinearLayout serItem;
        public ImageView serPassword;
        public TextView serTitle;
        public TextView serAdress;
        public TextView serMode;
        public TextView serPing;
        public TextView serPlayer;

        public OriginalViewHolder(View v) {
            super(v);
            serItem = (LinearLayout) v.findViewById(R.id.linearContainerServerInfo);
            serPassword = (ImageView) v.findViewById(R.id.statusView);
            serTitle = (TextView) v.findViewById(R.id.nameServer);
            serAdress = (TextView) v.findViewById(R.id.addressServer);
            serMode = (TextView) v.findViewById(R.id.modeServer);
            serPing = (TextView) v.findViewById(R.id.pingServer);
            serPlayer = (TextView) v.findViewById(R.id.countPlayers);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_server, parent, false);
        vh = new AdapterFavorite.OriginalViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof AdapterFavorite.OriginalViewHolder) {
            final Server o = items.get(position);
            final AdapterFavorite.OriginalViewHolder vItem = (AdapterFavorite.OriginalViewHolder) holder;
            final String[] serverInfo = new String[7];

            final File sampfile = new File(act.getExternalCacheDir(),"samp_favorite.txt");
            final File tempFile = new File(act.getExternalCacheDir(),"samp_favoriteTEMP.txt");

            final long[] ping = new long[1];

            vItem.serAdress.setText("Adress: " + o.host + ":" + o.port);
            pingtext = vItem.serPing;

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        SampQuery query = new SampQuery(o.host, o.port);
                        ping[0] = query.getPing();
                        query.close();
                        Document doc = Jsoup.connect("http://serapp.msach.ru/samp/getserverinfo.php?ip=" + o.host + "&port=" + o.port).timeout(50000).get();
                        Element hostname = doc.getElementById("title");
                        Element mod = doc.getElementById("gamemode");
                        Element players = doc.getElementById("players");
                        Element maxplayers = doc.getElementById("maxplayers");
                        Element password = doc.getElementById("password");
                        Element language = doc.getElementById("lang");

                        serverInfo[0] = password.text(); //password
                        serverInfo[1] = hostname.text(); //hostname
                        serverInfo[2] = mod.text(); //game mod
                        serverInfo[3] = players.text();//platers
                        serverInfo[4] = maxplayers.text();//maxplayers
                        serverInfo[5] = language.text();//language
                        Log.i("SAMP",serverInfo[1]);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                    act.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            if (serverInfo[0] != null){
                                if (serverInfo[0].equals("0")){
                                    vItem.serPassword.setImageDrawable(ctx.getResources().getDrawable(R.drawable.ic_lock_open_server));
                                } if (serverInfo[0].equals("1")){
                                    vItem.serPassword.setImageDrawable(ctx.getResources().getDrawable(R.drawable.ic_lock_close_server_outline));

                                }
                            }
                            vItem.serTitle.setText(serverInfo[1]);
                            vItem.serMode.setText("Mode: " + serverInfo[2]);
                            vItem.serPing.setText("Ping: " + ping[0]);
                            vItem.serPlayer.setText(serverInfo[3] + "/" + serverInfo[4]);
                        }
                    });
                }
            }).start();

            vItem.serItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final AlertDialog.Builder mBuilder = new AlertDialog.Builder(act, R.style.CustomAlertDialog);
                    View mView = act.getLayoutInflater().inflate(R.layout.dialog_server, null);
                    TextView host = (TextView) mView.findViewById(R.id.host);
                    TextView address = (TextView) mView.findViewById(R.id.address);
                    TextView players = (TextView) mView.findViewById(R.id.players);
                    TextView mode = (TextView) mView.findViewById(R.id.mode);
                    TextView language = (TextView) mView.findViewById(R.id.language);
                    final EditText password = (EditText) mView.findViewById(R.id.password);
                    Button save = (Button) mView.findViewById(R.id.save);
                    Button mTimerOk = (Button) mView.findViewById(R.id.ok);
                    TextView mTimerCancel = (TextView) mView.findViewById(R.id.cancel);
                    mBuilder.setView(mView);
                    final AlertDialog sampDialog = mBuilder.create();

                    if (serverInfo[0] != null){
                        if (serverInfo[0].equals("0")){
                            vItem.serPassword.setImageDrawable(ctx.getResources().getDrawable(R.drawable.ic_lock_open_server));
                        } if (serverInfo[0].equals("1")){
                            vItem.serPassword.setImageDrawable(ctx.getResources().getDrawable(R.drawable.ic_lock_close_server_outline));
                        }
                    }

                    password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                        @Override
                        public void onFocusChange(View v, boolean hasFocus) {
                            if(!hasFocus) {
                                generalData.hideKeyboard(act);
                            }
                        }
                    });

                    host.setText(serverInfo[1]);
                    address.setText(o.host + ":" + o.port);
                    players.setText(serverInfo[3] + "/" + serverInfo[4]);
                    mode.setText(serverInfo[2]);
                    language.setText(serverInfo[5]);

                    if (serverInfo[0] != null){ if (serverInfo[0].equals("1")){ password.setVisibility(View.VISIBLE); } }

                    try {
                        BufferedReader in = new BufferedReader(new FileReader(sampfile));
                        String line;
                        while ((line = in.readLine()) != null){
                            if ((o.host + ":" + o.port).equals(line)){
                                save.setBackground(act.getResources().getDrawable(R.drawable.rounded_del));
                                save.setTextColor(Color.WHITE);
                                save.setText("Delete");
                            }
                        }
                        in.close();
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    save.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            try {
                                BufferedReader reader = new BufferedReader(new FileReader(sampfile));
                                BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));
                                String line;
                                while ((line = reader.readLine()) != null){
                                    if ((o.host + ":" + o.port).equals(line)){
                                        continue;
                                    }
                                    writer.write(line + System.getProperty("line.separator"));
                                }
                                writer.close();
                                reader.close();
                                sampfile.delete();
                                tempFile.renameTo(sampfile);
                                sampDialog.dismiss();

                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                    mTimerOk.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick (View view) {
                            sampDialog.dismiss();
                            if (!Tools.needInstallApk(act)){
                                Tools.setHostSetting(act, Integer.parseInt(o.password),password.getText().toString(), o.host, String.valueOf(o.port));
                            }
                            Intent launchIntent = act.getPackageManager().getLaunchIntentForPackage("com.x4soft.sampclient");
                            if (launchIntent != null) {
                                act.startActivity(launchIntent);
                            }else {
                                Tools.needAppDialog(act);
                            }
                        }
                    });

                    mTimerCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick (View view) {
                            sampDialog.dismiss();
                        }
                    });
                    sampDialog.show();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public int getItemViewType(int position) {
        return this.items.get(position) != null ? VIEW_ITEM : VIEW_PROG;
    }

    public void resetListData() {
        this.items = new ArrayList<>();
        notifyDataSetChanged();
    }

    private void lastItemViewDetector(RecyclerView recyclerView) {
        if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
            final LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
            recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    int lastPos = layoutManager.findLastVisibleItemPosition();
                    if (!loading && lastPos == getItemCount() - 1 && onLoadMoreListener != null) {
                        if (onLoadMoreListener != null) {
                            int current_page = getItemCount() / 10;
                            onLoadMoreListener.onLoadMore(current_page);
                        }
                        loading = true;
                    }
                }
            });
        }
    }

    public interface OnLoadMoreListener {
        void onLoadMore(int current_page);
    }

}
