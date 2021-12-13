package com.production.vsn;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.VpnService;
import android.os.ParcelFileDescriptor;

public class SecureService extends VpnService {

    private Connection connection;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        notification();
        connect();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        connection.stopConnection();
        connection.disconnect();
    }

    @Override
    public void onRevoke() {
        super.onRevoke();
        connection.stopConnection();
        connection.disconnect();
    }

    private void notification() {
        NotificationChannel channel = new NotificationChannel("CHANNEL_ID", "CHANNEL", NotificationManager.IMPORTANCE_DEFAULT);
        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(channel);
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        Notification notification = new Notification.Builder(this, "CHANNEL_ID").setContentIntent(pendingIntent).build();
        int NOTIFICATION_ID = 1;
        startForeground(NOTIFICATION_ID, notification);
    }

    private void connect() {
        String serverAddress = "10.0.0.182";
        int port = 80;
        connection = new Connection(this, serverAddress, port);
        connection.protect();
        VpnService.Builder builder = new VpnService.Builder();
        ParcelFileDescriptor fileDescriptor = builder.addAddress("192.168.2.2", 24).addRoute("0.0.0.0", 0).addDnsServer("75.75.75.75").setBlocking(true).establish();
        connection.build(fileDescriptor);
        connection.start();
    }
}
