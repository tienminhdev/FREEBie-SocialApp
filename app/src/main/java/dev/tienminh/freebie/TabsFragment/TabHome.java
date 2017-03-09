package dev.tienminh.freebie.TabsFragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import dev.tienminh.freebie.Blog.BlogObj;
import dev.tienminh.freebie.Blog.PostDetail;
import dev.tienminh.freebie.R;
import dev.tienminh.freebie.User.Login;

/**
 * Created by thang on 23/02/2017.
 */

public class TabHome extends Fragment{

    private boolean liked=false;
    private RecyclerView rec_blog;
    DatabaseReference dbRef;
    DatabaseReference dbLike;
    DatabaseReference dbUser;
    FirebaseAuth auth;
    FirebaseAuth.AuthStateListener stateListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab_home,container,false);

        auth = FirebaseAuth.getInstance();
        final FirebaseUser user = auth.getCurrentUser();
        dbRef = FirebaseDatabase.getInstance().getReference().child("Blog");
        dbUser =FirebaseDatabase.getInstance().getReference().child("Users");
        dbLike = FirebaseDatabase.getInstance().getReference().child("Likes");
        dbUser.keepSynced(true);
        dbLike.keepSynced(true);
        dbRef.keepSynced(true);

        stateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if (user ==null){
                    getActivity().startActivity(new Intent(getActivity(), Login.class));
                    getActivity().finish();
                }
            }
        };

        checkUserExits();
        rec_blog = (RecyclerView) view.findViewById(R.id.rec_timeline);
        rec_blog.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        rec_blog.setLayoutManager(layoutManager);
       // rec_blog.setLayoutManager(new LinearLayoutManager(getContext()));
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        auth.addAuthStateListener(stateListener);
        FirebaseRecyclerAdapter<BlogObj,BlogViewHolder> firebaseRecyclerAdapter =
                new FirebaseRecyclerAdapter<BlogObj, BlogViewHolder>(
                        BlogObj.class,
                        R.layout.blog_row,
                        BlogViewHolder.class,
                        dbRef
                ) {
            @Override
            protected void populateViewHolder(BlogViewHolder viewHolder, BlogObj model, int position) {

                final String postKey = getRef(position).getKey();
                viewHolder.setAvatar(getActivity(),model.getAvatar());
                viewHolder.setUsername(model.getUsername());
                viewHolder.setDesc(model.getDesc());
                viewHolder.setImage(getActivity(),model.getImage());
                viewHolder.setLike(postKey);
              //  viewHolder.setTimeStamp(model.getTimeStamp());
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent iDetail = new Intent(getActivity(), PostDetail.class);
                        iDetail.putExtra("blog_id",postKey);
                        getActivity().startActivity(iDetail);
                    }
                });
                viewHolder.ibLike.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        liked=true;
                        dbLike.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                if (liked){
                                    if (dataSnapshot.child(postKey).hasChild(auth.getCurrentUser().getUid())){

                                        dbLike.child(postKey).child(auth.getCurrentUser().getUid()).removeValue();
                                        liked=false;
                                    }else {
                                        dbLike.child(postKey).child(auth.getCurrentUser().getUid()).setValue("RandomVal");
                                        liked=false;
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                });

            }
        };
        rec_blog.setAdapter(firebaseRecyclerAdapter);

    }
    private void checkUserExits() {

        if (auth.getCurrentUser() != null) {
            final String user_id = auth.getCurrentUser().getUid();
            dbUser.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (!dataSnapshot.hasChild(user_id)) {
//                        Intent iSetup = new Intent(getActivity(), Login.class);
//                        iSetup.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                        getActivity().startActivity(iSetup);
                    } else {

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }


    public static class BlogViewHolder extends RecyclerView.ViewHolder {

        DatabaseReference dbRefLike;
        FirebaseAuth auth;
        ImageButton ibLike;

        public BlogViewHolder(View itemView) {
            super(itemView);

            auth = FirebaseAuth.getInstance();
            dbRefLike = FirebaseDatabase.getInstance().getReference().child("Likes");
            dbRefLike.keepSynced(true);
        }
        public void setUsername (String username){
            TextView txtUsername = (TextView) itemView.findViewById(R.id.txt_username);
            txtUsername.setText(username);
        }
//        public void setTimeStamp (String timeStamp){
//            TextView txt_time = (TextView) itemView.findViewById(R.id.timeStamp);
//            txt_time.setText(converteTimestamp(timeStamp));
   //     }
        public void setAvatar (Context c,String avatar){
            ImageView imgAvatar = (ImageView) itemView.findViewById(R.id.img_user);
            Picasso.with(c).load(avatar).into(imgAvatar);
        }
        public void setDesc (String desc){
            TextView txtDesc = (TextView) itemView.findViewById(R.id.txt_desc);
            txtDesc.setText(desc);
        }
        public void setImage (Context c,String image){
            ImageView imgPost = (ImageView) itemView.findViewById(R.id.img_post);
            Picasso.with(c).load(image).into(imgPost);
        }
        public void setLike (final String postKey){
            ibLike = (ImageButton) itemView.findViewById(R.id.like);

            dbRefLike.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot.child(postKey).hasChild(auth.getCurrentUser().getUid())){

                        ibLike.setImageResource(R.drawable.ic_favorite_black_24dp);
                    }else {
                        ibLike.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }
    }
//    private static CharSequence converteTimestamp(String mileSegundos){
//        return DateUtils.getRelativeTimeSpanString(Long.parseLong(mileSegundos),System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);
//    }
}
