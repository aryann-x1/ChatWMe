package com.example.firstapp;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.List;

public class ForumAdapter extends ArrayAdapter<ForumPost> {
    public ForumAdapter(Context context, List<ForumPost> posts) {
        super(context, 0, posts);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ForumPost post = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.forum_post, parent, false);
        }

        TextView usernameText = convertView.findViewById(R.id.usernameText);
        TextView messageText = convertView.findViewById(R.id.messageText);
        TextView timestampText = convertView.findViewById(R.id.timestampText);

        usernameText.setText(post.username);
        messageText.setText(post.message);
        timestampText.setText(DateFormat.format("MMM dd, hh:mm a", post.timestamp));

        return convertView;
    }
}
