package com.stringee.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.stringee.app.R.id;
import com.stringee.app.R.layout;
import com.stringee.app.adapter.StringAdapter.LogHolder;
import com.stringee.app.listener.ItemClickListener;

import java.util.List;

public class StringAdapter extends Adapter<LogHolder> {
    private final List<String> data;
    private final LayoutInflater inflater;
    private ItemClickListener listener;

    public StringAdapter(Context context, List<String> data) {
        this.data = data;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setOnItemClickListener(ItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public LogHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = inflater.inflate(layout.item_string, parent, false);
        return new LogHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull LogHolder holder, int position) {
        holder.tvValue.setText(data.get(position));
    }

    @Override
    public long getItemId(int pos) {
        return pos;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class LogHolder extends ViewHolder implements OnLongClickListener {
        TextView tvValue;

        public LogHolder(View itemView) {
            super(itemView);
            tvValue = itemView.findViewById(id.tv_value);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public boolean onLongClick(View view) {
            if (listener != null) {
                listener.onLongClick(view, this.getLayoutPosition());
            }
            return false;
        }
    }
}
