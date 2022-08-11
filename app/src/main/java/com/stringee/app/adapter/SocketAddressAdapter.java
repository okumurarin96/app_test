package com.stringee.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.stringee.app.R.id;
import com.stringee.app.R.layout;
import com.stringee.app.listener.ItemClickListener;
import com.stringee.app.model.ServerAddress;

import java.util.List;

public class SocketAddressAdapter extends Adapter {
    private List<ServerAddress> data;
    private Context context;
    private LayoutInflater inflater;
    private ItemClickListener listener;

    public SocketAddressAdapter(Context context, List<ServerAddress> data) {
        this.context = context;
        this.data = data;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setOnItemClickListener(ItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = inflater.inflate(layout.item_socket_address, parent, false);
        return new SocketAddressHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SocketAddressHolder socketAddressHolder = (SocketAddressHolder) holder;
        ServerAddress serverAddress = data.get(position);

        socketAddressHolder.tvIp.setText(serverAddress.getSocketAddress().getIp());
        socketAddressHolder.tvPort.setText(String.valueOf(serverAddress.getSocketAddress().getPort()));
        socketAddressHolder.ivCheck.setVisibility(serverAddress.isSelected() ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    public long getItemId(int pos) {
        return pos;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    private class SocketAddressHolder extends ViewHolder implements View.OnClickListener {
        ImageView ivCheck;
        TextView tvIp;
        TextView tvPort;

        public SocketAddressHolder(View itemView) {
            super(itemView);
            ivCheck = itemView.findViewById(id.iv_check);
            tvIp = itemView.findViewById(id.tv_ip);
            tvPort = itemView.findViewById(id.tv_port);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (listener != null) {
                listener.onClick(view, this.getLayoutPosition());
            }
        }
    }
}
