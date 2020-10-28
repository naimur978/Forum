package com.naimur978.forum.Adapters;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.naimur978.forum.AddPostActivity;
import com.naimur978.forum.ChatActivity;
import com.naimur978.forum.Models.ModelPost;
import com.naimur978.forum.PostDetailsActivity;
import com.naimur978.forum.R;
import com.naimur978.forum.TheirProfileActivity;


import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AdapterPosts extends RecyclerView.Adapter<AdapterPosts.MyHolder> {

    Context context;
    List<ModelPost> postList;
    String myUid;
    private DatabaseReference likesRef;
    private DatabaseReference postsRef;
    boolean mProcessLikes = false;

    public AdapterPosts(Context context, List<ModelPost> postList) {
        this.context = context;
        this.postList = postList;
        myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        likesRef = FirebaseDatabase.getInstance().getReference().child("Likes");
        postsRef = FirebaseDatabase.getInstance().getReference().child("Posts");

    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int position) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_posts, parent,false);

        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        final String uid = postList.get(position).getUid();
        final String uEmail = postList.get(position).getuEmail();
        final String uName = postList.get(position).getuName();
        final String uDp = postList.get(position).getuDp();
        final String pId = postList.get(position).getpId();
        final String pTitle = postList.get(position).getpTitle();
        final String pDescription = postList.get(position).getpDescr();
        final String pImage = postList.get(position).getpImage();
        final String pTimeStamp = postList.get(position).getpTime();
        final String pLikes = postList.get(position).getpLikes();
        final String pComments = postList.get(position).getpComments();




        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.setTimeInMillis(Long.parseLong(pTimeStamp));
        String pTime = DateFormat.format("dd.MM.yyyy hh:mm aa",calendar).toString();

        //set data
        holder.uNameTv.setText(uName);
        holder.pTimeTv.setText(pTime);
        holder.pTitleTv.setText(pTitle);
        holder.pDescriptionTv.setText(pDescription);
        holder.pLikesTv.setText(pLikes+" Likes");
        holder.pCommentsTv.setText(pComments+" Comments");

        //set likes for each post
        setLikes(holder,pId);


        //set user dp
        try{
            /*Picasso.get().load(uDp)
                    .placeholder(R.drawable.ic_default_img)
                    .into(holder.uPictureIv);*/
            Glide
                    .with(context)
                    .load(uDp)
                    .centerCrop()
                    .placeholder(R.drawable.ic_default_img)
                    .into(holder.uPictureIv);




        }catch (Exception e){

        }

        //set posted image
        //if there is no image i.e. pimage.equals("noImage") then hide ImageView
        if(pImage.equals("noImage")){
            //hide imageview
            holder.pImageIv.setVisibility(View.GONE);
        }else{
            //show image view
            holder.pImageIv.setVisibility(View.VISIBLE);

                try{
                    /*Picasso.get().load(pImage)
                            .into(holder.pImageIv);*/
                    Glide
                            .with(context)
                            .load(pImage)
                            .centerCrop()
                            .into(holder.pImageIv);
                }catch (Exception e){

                }
        }


        holder.moreBtn.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showMoreOptions(holder.moreBtn, uid, myUid, pId, pImage);
            }
        }));

        holder.likeBtn.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pLikes = Integer.parseInt(postList.get(position).getpLikes());
                mProcessLikes = true;
                //get id of the post clicked
                String postId = postList.get(position).getpId();
                likesRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(mProcessLikes){
                            if(dataSnapshot.child(postId).hasChild(myUid)){
                                //already liked, so ignore it
                                postsRef.child(postId).child("pLikes").setValue(""+(pLikes-1));
                                likesRef.child(postId).child(myUid).removeValue();
                                mProcessLikes = false;
                            }else{
                                //not liked yet, so like it
                                postsRef.child(postId).child("pLikes").setValue(""+(pLikes+1));
                                likesRef.child(postId).child(myUid).setValue("Liked");
                                mProcessLikes = false;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        }));

        holder.commentBtn.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, PostDetailsActivity.class);
                intent.putExtra("postId", pId);
                context.startActivity(intent);
            }
        }));

        holder.shareBtn.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //we have to handle both post with & without image
                BitmapDrawable bitmapDrawable = (BitmapDrawable)holder.pImageIv.getDrawable();
                if(bitmapDrawable == null){
                    //post without image
                    shareTextOnly(pTitle, pDescription);
                }
                else{
                    //post with image
                    //convert image to bitmap
                    Bitmap bitmap = bitmapDrawable.getBitmap();
                    shareImageAndText(pTitle, pDescription, bitmap);

                }
            }
        }));

        holder.profileLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //alert dialog to choose chat or post
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setItems(new String[]{"Profile", "Chat"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(i==0){
                            //profile clicked
                            Intent intent = new Intent(context, TheirProfileActivity.class);
                            intent.putExtra("uid",uid);
                            context.startActivity(intent);
                        }
                        if(i==1){
                            //chat clicked
                            Intent intent = new Intent(context, ChatActivity.class);
                            intent.putExtra("hisUid", uid);
                            context.startActivity(intent);
                        }
                    }
                });
                builder.create().show();
            }
        });

    }

    private void shareImageAndText(String pTitle, String pDescription, Bitmap bitmap) {
        //concatenate title and description to share
        String shareBody = pTitle +"\n" +pDescription;

        //first we will save this image in cache, get the saved image uri
        Uri uri = saveImageToShare(bitmap);

        //share intent
        Intent sIntent = new Intent(Intent.ACTION_SEND);
        sIntent.putExtra(Intent.EXTRA_STREAM,uri);
        sIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject Here");//in case you share via an email app
        sIntent.putExtra(Intent.EXTRA_TEXT, shareBody);//text to share
        sIntent.setType("image/png");
        context.startActivity(Intent.createChooser(sIntent,"Share Via"));//message to show in share dialog

    }

    private Uri saveImageToShare(Bitmap bitmap) {
        File imageFolder = new File(context.getCacheDir(), "images");
        Uri uri = null;
        try {
            imageFolder.mkdir();//create if not exists
            File file = new File(imageFolder, "shared_image.png");

            FileOutputStream stream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream);
            stream.flush();
            stream.close();
            uri = FileProvider.getUriForFile(context, "com.naimur978.forum.filesprovider", file);
        } catch (Exception e) {
            Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return uri;
    }

    private void shareTextOnly(String pTitle, String pDescription) {
        //concatenate title and description to share
        String shareBody = pTitle +"\n" +pDescription;

        //share intent
        Intent sIntent = new Intent(Intent.ACTION_SEND);
        sIntent.setType("text/plain");
        sIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject Here");//in case you share via an email app
        sIntent.putExtra(Intent.EXTRA_TEXT, shareBody);//text to share
        context.startActivity(Intent.createChooser(sIntent,"Share Via"));//message to show in share dialog
    }

    private void setLikes(MyHolder holder, String pId) {
        likesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(pId).hasChild(myUid)){
                    //user has liked this post, so notify it by showing
                    //"liked" instead of "like"
                    //holder.likeBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_like,0,0,0);
                    holder.likeBtn.setImageResource(R.drawable.liked);
                    //holder.likeBtn.setText("Liked");
                }else{
                    //user has not liked this post yet
                    holder.likeBtn.setImageResource(R.drawable.like);
                    //holder.likeBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_like_black,0,0,0);
                   // holder.likeBtn.setText("Like");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //to delete post------------------------------------------------------------------------------------
    private void showMoreOptions(ImageButton moreBtn, String uid, String myUid, String pId, String pImage) {
        PopupMenu popupMenu = new PopupMenu(context, moreBtn, Gravity.END);

        if(uid.equals(myUid)){
            popupMenu.getMenu().add(Menu.NONE, 0, 0, "Delete");
            popupMenu.getMenu().add(Menu.NONE, 1, 0, "Edit");
            popupMenu.getMenu().add(Menu.NONE, 2, 0, "View Detail");

        }

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                int id = menuItem.getItemId();
                if(id == 0){
                    //delete is clicked
                    beginDelete(pId,pImage);
                }
                else if(id == 1){
                    Intent intent = new Intent(context, AddPostActivity.class);
                    intent.putExtra("key","editPost");
                    intent.putExtra("editPostId",pId);
                    context.startActivity(intent);
                }
                else if(id==2){
                    Intent intent = new Intent(context, PostDetailsActivity.class);
                    intent.putExtra("postId", pId);
                    context.startActivity(intent);
                }
                return false;
            }
        });
        popupMenu.show();
    }

    private void beginDelete(String pId, String pImage) {
        //post can be with or without image
        if(pImage.equals("noImage")){
            //post is without image
            deleteWithoutImage(pId);
        }
        else{
            //post is with image
            deleteWithImage(pId, pImage);
        }
    }

    private void deleteWithImage(String pId, String pImage) {
        ProgressDialog pd = new ProgressDialog(context);
        pd.setMessage("Deleting...");
        //1. delete image using url
        //2. delete from database using post id
        StorageReference picRef = FirebaseStorage.getInstance().getReferenceFromUrl(pImage);
        picRef.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Query fquery = FirebaseDatabase.getInstance().getReference("Posts")
                                .orderByChild("pId").equalTo(pId);
                        fquery.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot ds: dataSnapshot.getChildren()){
                                    ds.getRef().removeValue();//remove values from firebase where pid matches
                                }
                                Toast.makeText(context, "Deleted Successfully", Toast.LENGTH_SHORT).show();
                                pd.dismiss();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
            })
                .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteWithoutImage(String pId) {
        ProgressDialog pd = new ProgressDialog(context);
        pd.setMessage("Deleting...");

        Query fquery = FirebaseDatabase.getInstance().getReference("Posts")
                .orderByChild("pId").equalTo(pId);
        fquery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds: dataSnapshot.getChildren()){
                    ds.getRef().removeValue();//remove values from firebase where pid matches
                }
                Toast.makeText(context, "Deleted Successfully", Toast.LENGTH_SHORT).show();
                pd.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    //to delete post-----------------------------------------------------------------------------

    @Override
    public int getItemCount() {
        return postList.size();
    }

    public static class MyHolder extends RecyclerView.ViewHolder {

        ImageView uPictureIv,pImageIv;
        TextView uNameTv, pTimeTv, pTitleTv, pDescriptionTv, pLikesTv, pCommentsTv;
        ImageButton moreBtn;
        ImageButton likeBtn, commentBtn, shareBtn;
        LinearLayout profileLayout;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            uPictureIv = itemView.findViewById(R.id.uPictureIv);
            pImageIv = itemView.findViewById(R.id.pImageIv);
            uNameTv = itemView.findViewById(R.id.uNameTv);
            pTimeTv = itemView.findViewById(R.id.pTimeTv);
            pTitleTv = itemView.findViewById(R.id.pTitleTv);
            pDescriptionTv = itemView.findViewById(R.id.pDescriptionTv);
            pLikesTv = itemView.findViewById(R.id.pLikesTv);
            pCommentsTv = itemView.findViewById(R.id.pCommentsTv);
            moreBtn = itemView.findViewById(R.id.moreBtn);
            likeBtn = itemView.findViewById(R.id.likeBtn);
            commentBtn = itemView.findViewById(R.id.commentBtn);
            shareBtn = itemView.findViewById(R.id.shareBtn);
            profileLayout = itemView.findViewById(R.id.profileLayout);

        }
    }
}
