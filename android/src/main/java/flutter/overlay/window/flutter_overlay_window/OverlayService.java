package flutter.overlay.window.flutter_overlay_window;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.app.PendingIntent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.example.flutter_overlay_window.R;

import io.flutter.embedding.android.FlutterTextureView;
import io.flutter.embedding.android.FlutterView;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.embedding.engine.FlutterEngineCache;
import io.flutter.plugin.common.BasicMessageChannel;
import io.flutter.plugin.common.JSONMessageCodec;
import io.flutter.plugin.common.MethodChannel;

public class OverlayService extends Service {

    private WindowManager windowManager = null;
    private FlutterView flutterView;
    private MethodChannel flutterChannel = new MethodChannel(FlutterEngineCache.getInstance().get("my_engine_id").getDartExecutor(), OverlayConstants.OVERLAY_TAG);
    private BasicMessageChannel overlayMessageChannel = new BasicMessageChannel(FlutterEngineCache.getInstance().get("my_engine_id").getDartExecutor(), OverlayConstants.MESSENGER_TAG, JSONMessageCodec.INSTANCE);

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onDestroy() {
        Log.d("OverLay", "Destroying the overlay window service");
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        assert notificationManager != null;
        notificationManager.cancel(OverlayConstants.NOTIFICATION_ID);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (windowManager != null) {
            closeOverlay();
        }
        Log.d("CMD", "Service started");
        FlutterEngine engine = FlutterEngineCache.getInstance().get("my_engine_id");
        engine.getLifecycleChannel().appIsResumed();
        flutterView = new FlutterView(getApplicationContext(), new FlutterTextureView(getApplicationContext()));
        flutterView.attachToFlutterEngine(FlutterEngineCache.getInstance().get("my_engine_id"));
        flutterView.setFitsSystemWindows(true);
        flutterView.setBackgroundColor(Color.TRANSPARENT);
        flutterChannel.setMethodCallHandler((call, result) -> {
            if (call.method.equals("close")) {
                closeOverlay();
                result.success(true);
            }
        });

        overlayMessageChannel.setMessageHandler((message, reply) -> {
            WindowSetup.messenger.send(message);
        });

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowSetup.width,
                WindowSetup.height,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowSetup.flag,
                PixelFormat.TRANSPARENT
        );
        params.gravity = WindowSetup.gravity;
        windowManager.addView(flutterView, params);
        return START_STICKY;
    }

    private void closeOverlay() {
        if (windowManager != null) {
            windowManager.removeView(flutterView);
            windowManager = null;
            stopSelf();
        }
    }

    @Override
    public void onCreate() {
        createNotificationChannel();
        Intent notificationIntent = new Intent(this, FlutterOverlayWindowPlugin.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);
        Notification notification = new NotificationCompat.Builder(this, OverlayConstants.CHANNEL_ID)
                .setContentTitle("")
                .setSmallIcon(R.drawable.notification_icon)
                .setContentIntent(pendingIntent)
                .build();
        startForeground(OverlayConstants.NOTIFICATION_ID, notification);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    OverlayConstants.CHANNEL_ID,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            assert manager != null;
            manager.createNotificationChannel(serviceChannel);
        }
    }
}
