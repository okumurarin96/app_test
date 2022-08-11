package com.stringee.app.listener;

import android.view.View;

public abstract class ItemClickListener {
    public ItemClickListener() {
    }

    public abstract void onClick(View view, int position);

    public void onLongClick(View view, int position) {
    }
}
