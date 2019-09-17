package com.sha512boo.ArizonaLauncher.model;

public class Server {
    public String password;
    public String host;
    public String title;
    public String mode;
    public String language;
    public long ping;
    public String player;
    public String maxplayer;
    public int port;

    public Server() {
    }

    public Server(String password, String host, int port, String title, String mode, String language, long ping, String player, String maxplayer) {
        this.password = password;
        this.host = host;
        this.port = port;
        this.title = title;
        this.mode = mode;
        this.language = language;
        this.ping = ping;
        this.player = player;
        this.maxplayer = maxplayer;
    }
}
