package dev.oneuiproject.oneui.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;

import dev.oneuiproject.oneui.popover.PopOverOptions;

@Deprecated
public class ActivityUtils {

    public static final int POP_OVER_POSITION_CENTER = 68;
    public static final int POP_OVER_POSITION_CENTER_HORIZONTAL = 64;
    public static final int POP_OVER_POSITION_LEFT = 16;
    public static final int POP_OVER_POSITION_RIGHT = 32;
    public static final int POP_OVER_POSITION_CENTER_VERTICAL = 4;
    public static final int POP_OVER_POSITION_TOP = 1;
    public static final int POP_OVER_POSITION_BOTTOM = 2;

    @Deprecated
    public static class PopOverConfig {
        int widthDp, heightDp, position;
        Point margins;

        public PopOverConfig(int widthDp, int heightDp, int position, Point margins) {
            this.widthDp = widthDp;
            this.heightDp = heightDp;
            this.position = position;
            this.margins = margins;
        }
    }

    /**
     * @deprecated See {@link dev.oneuiproject.oneui.ktx.ContextKt#startPopOverActivity(Context, Intent, PopOverOptions, Bundle)}
     */
    @Deprecated
    public static void startPopOverActivity(Context context, Intent intent, Bundle options, int position) {
        PopOverConfig configPortrait = new PopOverConfig(360, 731, position, new Point());
        PopOverConfig configLandScape = new PopOverConfig(360,
                DeviceLayoutUtil.isTabletCategoryOrBuild(context) ? 731 : 570, position, new Point());
        startPopOverActivity(context, intent, options, configPortrait, configLandScape);
    }

    /**
     * @deprecated See {@link dev.oneuiproject.oneui.ktx.ContextKt#startPopOverActivity(Context, Intent, PopOverOptions, Bundle)}
     */
    @Deprecated
    public static void startPopOverActivity(Context context, Intent intent, Bundle options, PopOverConfig portrait, PopOverConfig landscape) {
        startPopOverActivity(context, intent, options, false, false, portrait, landscape);
    }


    /**
     * @deprecated See {@link dev.oneuiproject.oneui.ktx.ContextKt#startPopOverActivity(Context, Intent, PopOverOptions, Bundle)}
     */
    @Deprecated
    public static void startPopOverActivity(Context context, Intent intent, Bundle options, boolean allowOutsideTouch, boolean removeOutline, PopOverConfig portrait, PopOverConfig landscape) {
        if (options == null) options = new Bundle();
        options.putBoolean("android:activity.popOver", true);
        options.putBoolean("android:activity.popOverAllowOutsideTouch", allowOutsideTouch);
        options.putBoolean("android:activity.popOverRemoveOutlineEffect", removeOutline);
        options.putBoolean("android:activity.popOverRemoveDefaultMargin", false);
        options.putBoolean("android:activity.popOverInheritOptions", true);
        options.putParcelableArray("android:activity.popOverAnchor", new Point[]{landscape.margins, portrait.margins});
        options.putIntArray("android:activity.popOverHeight", new int[]{landscape.heightDp, portrait.heightDp});
        options.putIntArray("android:activity.popOverWidth", new int[]{landscape.widthDp, portrait.widthDp});
        options.putIntArray("android:activity.popOverAnchorPosition", new int[]{landscape.position, portrait.position});
        context.startActivity(intent, options);
    }

}
