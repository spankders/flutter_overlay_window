package flutter.overlay.window.flutter_overlay_window;

import android.view.Gravity;
import android.view.WindowManager;

import io.flutter.plugin.common.BasicMessageChannel;

public abstract class WindowSetup {

    static int height = WindowManager.LayoutParams.MATCH_PARENT;
    static int width = WindowManager.LayoutParams.MATCH_PARENT;
    static int gravity = Gravity.CENTER;
    static BasicMessageChannel messenger = null;


    static void setGravityFromAlignment(String alignment) {
        if (alignment.toLowerCase() == "topLeft".toLowerCase()) {
            gravity = Gravity.TOP | Gravity.LEFT;
            return;
        }
        if (alignment.toLowerCase() == "topCenter".toLowerCase()) {
            gravity = Gravity.TOP;
        }
        if (alignment.toLowerCase() == "topRight".toLowerCase()) {
            gravity = Gravity.TOP | Gravity.RIGHT;
            return;
        }

        if (alignment.toLowerCase() == "centerLeft".toLowerCase()) {
            gravity = Gravity.CENTER | Gravity.LEFT;
            return;
        }
        if (alignment.toLowerCase() == "center".toLowerCase()) {
            gravity = Gravity.CENTER;
        }
        if (alignment.toLowerCase() == "centerRight".toLowerCase()) {
            gravity = Gravity.CENTER | Gravity.RIGHT;
            return;
        }

        if (alignment.toLowerCase() == "bottomLeft".toLowerCase()) {
            gravity = Gravity.BOTTOM | Gravity.LEFT;
            return;
        }
        if (alignment.toLowerCase() == "bottomCenter".toLowerCase()) {
            gravity = Gravity.BOTTOM;
        }
        if (alignment.toLowerCase() == "bottomRight".toLowerCase()) {
            gravity = Gravity.BOTTOM | Gravity.RIGHT;
            return;
        }

    }
}
