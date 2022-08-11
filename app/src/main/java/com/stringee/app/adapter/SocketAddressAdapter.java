package com.stringee.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.stringee.app.R.id;
import com.stringee.app.R.layout;
import com.stringee.app.adapter.SocketAddressAdapter.SocketAddressHolder;
import com.stringee.app.listener.ItemClickListener;
import com.stringee.app.model.ServerAddress;

import java.util.List;

public class SocketAddressAdapter extends Adapter<SocketAddressHolder> {
    private final List<ServerAddress> data;
    private final LayoutInflater inflater;
    private ItemClickListener listener;
    private boolean selectable;

    public SocketAddressAdapter(Context context, List<ServerAddress> data) {
        this.data = data;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setSelectable(boolean selectable) {
        this.selectable = selectable;
    }

    public void setOnItemClickListener(ItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public SocketAddressHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = inflater.inflate(layout.item_socket_address, parent, false);
        return new SocketAddressHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull SocketAddressHolder holder, int position) {
        ServerAddress serverAddress = data.get(position);

        holder.tvIp.setText(serverAddress.getSocketAddress().getIp());
        holder.tvPort.setText(String.valueOf(serverAddress.getSocketAddress().getPort()));
        holder.ivCheck.setVisibility(selectable && serverAddress.isSelected() ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public long getItemId(int pos) {
        return pos;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class SocketAddressHolder extends ViewHolder implements OnClickListener, OnLongClickListener {
        ImageView ivCheck;
        TextView tvIp;
        TextView tvPort;

        public SocketAddressHolder(View itemView) {
            super(itemView);
            ivCheck = itemView.findViewById(id.iv_check);
            tvIp = itemView.findViewById(id.tv_ip);
            tvPort = itemView.findViewById(id.tv_port);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (listener != null) {
                listener.onClick(view, this.getLayoutPosition());
            }
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
