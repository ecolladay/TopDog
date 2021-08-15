package edu.neu.madcourse.topdog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import edu.neu.madcourse.topdog.DatabaseObjects.FetchDBUserUtil;
import edu.neu.madcourse.topdog.DatabaseObjects.LeaderboardEntry;
import edu.neu.madcourse.topdog.DatabaseObjects.PutDBInfoUtil;
import edu.neu.madcourse.topdog.DatabaseObjects.User;

public class LeaderboardRecyclerAdapter extends RecyclerView.Adapter<LeaderboardRecyclerAdapter.RecyclerHolder> {

    private DatabaseReference mDatabase;
    private String currentUsername;
    private final ArrayList<LeaderboardEntry> leaderboardList;
    private Context displayContext;

    public LeaderboardRecyclerAdapter(ArrayList<LeaderboardEntry> leaderboard, String currentUsername, Context displayContext) {
        mDatabase = FirebaseDatabase.getInstance().getReference().child("USERS");
        this.currentUsername = currentUsername;
        this.leaderboardList = leaderboard;
        this.displayContext = displayContext;
    }

    @Override
    public LeaderboardRecyclerAdapter.RecyclerHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_leaderboard_card, parent, false);
        return new LeaderboardRecyclerAdapter.RecyclerHolder(view, parent);
    }

    @Override
    public void onBindViewHolder(LeaderboardRecyclerAdapter.RecyclerHolder holder, int position) {
        LeaderboardEntry currentItem = leaderboardList.get(position);

        String dogName = currentItem.getDogName();
        long numWalks = currentItem.getNumberWalks();

        holder.dogName.setText(dogName);
        String numWalksText = numWalks + " walks";
        holder.numWalks.setText(numWalksText);

        //Performs the pats!
        holder.sendPatBtn.setOnClickListener((v) -> {
            User userToGetPat = new FetchDBUserUtil().getUser(currentItem.getUsername());

            if(userToGetPat.getUsername().equals(currentUsername)) {
                Toast.makeText(displayContext, "Oops, Can't pat yourself!", Toast.LENGTH_SHORT).show();
                return;
            }

            int displayPats = userToGetPat.getNumPats();
            displayPats++;

            new PutDBInfoUtil().setValue(mDatabase.child(userToGetPat.getUsername()).child("numPats"), displayPats);
            Toast.makeText(displayContext, "You sent " + userToGetPat.getDogName() + " a pat!", Toast.LENGTH_SHORT).show();
        });

    }

    @Override
    public int getItemCount() {
        return leaderboardList.size();
    }

    public class RecyclerHolder extends RecyclerView.ViewHolder {

        public TextView dogName;
        public TextView numWalks;
        public ViewGroup parent;
        public Button sendPatBtn;

        public RecyclerHolder(View itemView, ViewGroup parent) {
            super(itemView);
            this.parent = parent;
            this.dogName = itemView.findViewById(R.id.dogName_id);
            this.numWalks = itemView.findViewById(R.id.num_walks_id);
            this.sendPatBtn = itemView.findViewById(R.id.pats_btn_id);
        }
    }

}
