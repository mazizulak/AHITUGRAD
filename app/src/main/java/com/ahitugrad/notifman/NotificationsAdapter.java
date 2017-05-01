package com.ahitugrad.notifman;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by huseyin on 01/05/2017.
 */

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.ViewHolder> {

    ArrayList notifications = new ArrayList<Notification>();
    Context context;
    private final NotificationsAdapter.OnItemClickListener listener;

    public NotificationsAdapter(ArrayList notifications, Context context, OnItemClickListener listener) {
        this.notifications = notifications;
        this.context = context;
        this.listener = listener;
    }

    @Override
    public NotificationsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_notification, parent, false);
        NotificationsAdapter.ViewHolder vh = new NotificationsAdapter.ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(NotificationsAdapter.ViewHolder holder, int position) {
        Notification notif = (Notification) notifications.get(position);
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        holder.tvTitle.setText(notif.title);
        holder.tvContent.setText(notif.content);
        holder.tvTime.setText(dateFormat.format(date));
        holder.ivLogo.setImageDrawable(context.getResources().getDrawable(R.drawable.logo));


        holder.bind(notif,listener);
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    public interface OnItemClickListener {
        void onItemClick(Notification notification);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;
        TextView tvContent;
        TextView tvTime;
        ImageView ivLogo;
        public ViewHolder(View itemView) {
            super(itemView);
            tvTitle = (TextView) itemView.findViewById(R.id.tvNotificationTitle);
            tvContent = (TextView) itemView.findViewById(R.id.tvNotificationContent);
            tvTime = (TextView) itemView.findViewById(R.id.tvTime);
            ivLogo = (ImageView) itemView.findViewById(R.id.ivNotificationLogo);
        }
        public void bind(final Notification notification, final NotificationsAdapter.OnItemClickListener listener) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClick(notification);
                }
            });
        }
    }
}
