package com.sha512boo.ArizonaLauncher.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sha512boo.ArizonaLauncher.R;
import com.sha512boo.ArizonaLauncher.generalData;
import com.sha512boo.ArizonaLauncher.model.Server;
import com.sha512boo.ArizonaLauncher.utils.Tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

public class AdapterServer extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private final int VIEW_ITEM = 1;
    private final int VIEW_PROG = 0;

    private List<Server> items = new ArrayList<>();

    private boolean loading;
    private AdapterServer.OnLoadMoreListener onLoadMoreListener;

    private Activity act;
    private Context ctx;
    private AdapterServer.OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(View view, Server obj, int position);
    }

    public void setOnItemClickListener(final AdapterServer.OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public AdapterServer(Activity activity, Context context, RecyclerView view, List<Server> items) {
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
        vh = new AdapterServer.OriginalViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof AdapterServer.OriginalViewHolder) {
            final Server o = items.get(position);
            final AdapterServer.OriginalViewHolder vItem = (AdapterServer.OriginalViewHolder) holder;

            final File sampfile = new File(act.getExternalCacheDir(),"samp_favorite.txt");
            final File tempFile = new File(act.getExternalCacheDir(),"samp_favoriteTEMP.txt");

            if (o.password != null){
                if (o.password.equals("0")){
                    vItem.serPassword.setImageDrawable(ctx.getResources().getDrawable(R.drawable.ic_lock_open_server));
                } if (o.password.equals("1")){
                    vItem.serPassword.setImageDrawable(ctx.getResources().getDrawable(R.drawable.ic_lock_close_server_outline));
                }
            }

            vItem.serTitle.setText(o.title);
            vItem.serMode.setText("Mode: " + o.mode);
            vItem.serAdress.setText("Adress: " + o.host + ":" + o.port);
            vItem.serPing.setText("Ping: "+ String.valueOf(o.ping));
            vItem.serPlayer.setText(o.player + "/" + o.maxplayer);

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
                    final Button save = (Button) mView.findViewById(R.id.save);
                    Button mTimerOk = (Button) mView.findViewById(R.id.ok);
                    TextView mTimerCancel = (TextView) mView.findViewById(R.id.cancel);
                    mBuilder.setView(mView);
                    final AlertDialog sampDialog = mBuilder.create();

                    password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                        @Override
                        public void onFocusChange(View v, boolean hasFocus) {
                            if(!hasFocus) {
                                generalData.hideKeyboard(act);
                            }
                        }
                    });

                    if (o.password != null){
                        if (o.password.equals("0")){
                            vItem.serPassword.setImageDrawable(ctx.getResources().getDrawable(R.drawable.ic_lock_open_server));
                        } if (o.password.equals("1")){
                            vItem.serPassword.setImageDrawable(ctx.getResources().getDrawable(R.drawable.ic_lock_close_server_outline));
                        }
                    }

                    host.setText(o.title);
                    address.setText(o.host + ":" + o.port);
                    players.setText(o.player + "/" + o.maxplayer);
                    mode.setText(o.mode);
                    language.setText(o.language);

                    if (o.password != null){ if (o.password.equals("1")){ password.setVisibility(View.VISIBLE); } }

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
                                BufferedWriter temp = new BufferedWriter(new FileWriter(tempFile));
                                int ex = 0;
                                String line;
                                while ((line = reader.readLine()) != null){
                                    if ((o.host + ":" + o.port).equals(line)){
                                        ex = 1;
                                        continue;
                                    }
                                    temp.write(line + System.getProperty("line.separator"));
                                }
                                temp.close();
                                reader.close();
                                sampfile.delete();
                                tempFile.renameTo(sampfile);
                                save.setBackground(act.getResources().getDrawable(R.drawable.rounded_server));
                                save.setTextColor(act.getResources().getColor(R.color.colorPrimary));
                                save.setText("Save");

                                if (ex == 0){
                                    save.setBackground(act.getResources().getDrawable(R.drawable.rounded_del));
                                    save.setTextColor(Color.WHITE);
                                    save.setText("Delete");
                                    BufferedWriter add = null;
                                    add = new BufferedWriter(new FileWriter(sampfile, true));
                                    add.write(  o.host + ":" + o.port + System.getProperty("line.separator"));
                                    add.close();
                                }
                            } catch (MalformedURLException e) {
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

    public void insertData(List<Server> items) {
        setLoaded();
        int positionStart = getItemCount();
        int itemCount = items.size();
        this.items.addAll(items);
        notifyItemRangeInserted(positionStart, itemCount);
    }

    public void setLoaded() {
        loading = false;
        for (int i = 0; i < getItemCount(); i++) {
            if (items.get(i) == null) {
                items.remove(i);
                notifyItemRemoved(i);
            }
        }
    }

    public void setLoading() {
        if (getItemCount() != 0) {
            this.items.add(null);
            notifyItemInserted(getItemCount() - 1);
            loading = true;
        }
    }

    public void resetListData() {
        this.items = new ArrayList<>();
        notifyDataSetChanged();
    }

    public void setOnLoadMoreListener(AdapterServer.OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
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
