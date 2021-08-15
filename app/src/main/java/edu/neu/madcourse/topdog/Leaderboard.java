package edu.neu.madcourse.topdog;

import android.content.Intent;
import android.os.Bundle;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import edu.neu.madcourse.topdog.DatabaseObjects.FetchDBAllUsersUtil;
import edu.neu.madcourse.topdog.DatabaseObjects.LeaderboardEntry;
import edu.neu.madcourse.topdog.DatabaseObjects.User;

public class Leaderboard extends AppCompatActivity {

    private String username;
    ArrayList<LeaderboardEntry> displayLeaderboardEntries = new ArrayList<>();
    LeaderboardRecyclerAdapter recyclerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        username = getIntent().getStringExtra(MainActivity.USERKEY);

        ArrayList<User> allUsers = new FetchDBAllUsersUtil().getAllUsers();

        createRecyclerView();

        //creates and sorts the leaderboard entries
        ArrayList<LeaderboardEntry> sortedLeaders = createAndSortLeaderboardEntries(allUsers);

        //populate the display
        populateDisplayedLeaderboardEntries(sortedLeaders);

    }


    private ArrayList<LeaderboardEntry> createAndSortLeaderboardEntries(ArrayList<User> allUsers) {
        ArrayList<LeaderboardEntry> sortedEntries = new ArrayList<>();
        for (User user : allUsers) {
            LeaderboardEntry entry = new LeaderboardEntry(user.getUsername(), user.getDogName(),
                    user.getWalkList().size());
            sortedEntries.add(entry);
        }
        sortedEntries.sort((o1, o2) -> Long.compare(o2.getNumberWalks(), o1.getNumberWalks()));
        return sortedEntries;
    }

    private void populateDisplayedLeaderboardEntries(ArrayList<LeaderboardEntry> sortedLeaders) {
        for (LeaderboardEntry leader : sortedLeaders) {
            displayLeaderboardEntries.add(leader);
            recyclerAdapter.notifyDataSetChanged();
        }
    }

    private void createRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.leaderboard_recycler_view);
        RecyclerView.LayoutManager rLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(rLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerAdapter = new LeaderboardRecyclerAdapter(displayLeaderboardEntries, username, Leaderboard.this);
        recyclerView.setAdapter(recyclerAdapter);
    }


    //Methods for handling the go Home functionality in the menu bar
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_homepage, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.homepage) {
            Intent intent = new Intent(this, HomePage.class);
            intent.putExtra(MainActivity.USERKEY, username);
            startActivity(intent);
        }
        return true;
    }
    ////////////////////////////////////////////////////////////

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, HomePage.class);
        intent.putExtra(MainActivity.USERKEY, username);
        startActivity(intent);
    }

}
