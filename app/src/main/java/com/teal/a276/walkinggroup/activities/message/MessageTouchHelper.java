package com.teal.a276.walkinggroup.activities.message;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

/**
 * Created by scott on 23/03/18.
 */

abstract class MessageTouchHelper extends ItemTouchHelper.SimpleCallback {
    private Drawable icon;

    MessageTouchHelper(int dragDirs, int swipeDirs, Drawable icon) {
        super(dragDirs, swipeDirs);
        this.icon = icon;
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onChildDraw(Canvas canvas, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                            float dX, float dY, int actionState, boolean isCurrentlyActive) {

        View itemView = viewHolder.itemView;
        if ((dX == 0.0 && !isCurrentlyActive)) {
            clearReadIcon(canvas, itemView, dX);
            super.onChildDraw(canvas, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            return;
        }

        ColorDrawable background = new ColorDrawable(Color.parseColor("#64DD17"));
        background.setBounds(itemView.getLeft() + (int)dX, itemView.getTop(),
                itemView.getLeft(), itemView.getBottom());
        background.draw(canvas);
        drawReadIcon(canvas, itemView);
        super.onChildDraw(canvas, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }

    private void drawReadIcon(Canvas canvas, View view) {
        int itemHeight = view.getBottom() - view.getTop();
        int margin = (itemHeight - icon.getIntrinsicHeight()) / 2;
        int iconTop = view.getTop() + margin;
        int iconBottom = iconTop + icon.getIntrinsicHeight();
        int iconLeft = view.getLeft() + margin + icon.getIntrinsicWidth();
        int iconRight = view.getLeft() - margin;

        icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
        icon.draw(canvas);
    }

    private void clearReadIcon(Canvas canvas, View view, float xOffset) {
        Paint clear = new Paint();
        clear.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.MULTIPLY));
        clear.setColor(Color.TRANSPARENT);
        canvas.drawRect(view.getLeft() + xOffset, (float)view.getTop(),
                (float)view.getLeft(), (float)view.getBottom(), clear);
    }
}
