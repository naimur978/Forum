package com.naimur978.forum.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.naimur978.forum.ChatActivity;
import com.naimur978.forum.Models.ModelUsers;
import com.naimur978.forum.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AdapterUsers extends RecyclerView.Adapter<AdapterUsers.MyHolder> {

    Context context;
    List<ModelUsers> usersList;

    public AdapterUsers(Context context, List<ModelUsers> usersList) {
        this.context = context;
        this.usersList = usersList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_users, parent,false);

        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        final String hisUID = usersList.get(position).getUid();
        String userImage = usersList.get(position).getImage();
        String userName = usersList.get(position).getName();
        final String userEmail = usersList.get(position).getEmail();

        holder.mNameTv.setText(userName);
        holder.mEmailTv.setText(userEmail);

        try{
            Picasso.get().load(userImage)
                    .placeholder(R.drawable.ic_default_img)
                    .into(holder.mAvatarIv);
        }catch (Exception e){

        }

        holder.itemView.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("hisUid", hisUID);
                context.startActivity(intent);
            }
        }));
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    public static class MyHolder extends RecyclerView.ViewHolder {

        ImageView mAvatarIv;
        TextView mNameTv, mEmailTv;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            mAvatarIv = itemView.findViewById(R.id.avatarIv);
            mNameTv = itemView.findViewById(R.id.nameTv);
            mEmailTv = itemView.findViewById(R.id.emailTv);
        }
    }
}
