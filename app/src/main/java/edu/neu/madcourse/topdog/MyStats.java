package edu.neu.madcourse.topdog;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import edu.neu.madcourse.topdog.DatabaseObjects.FetchDBUserUtil;
import edu.neu.madcourse.topdog.DatabaseObjects.User;
import edu.neu.madcourse.topdog.DatabaseObjects.Walk;

public class MyStats extends AppCompatActivity {

    private static final String KEY_OF_INSTANCE = "KEY_OF_INSTANCE";
    private static final String NUMBER_OF_ITEMS = "NUMBER_OF_ITEMS";

    private String username;
    private ArrayList<Walk> displayedWalkList = new ArrayList<>();
    MyStatsRecyclerAdapter recyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_stats);

        username = getIntent().getStringExtra(MainActivity.USERKEY);
        User user = new FetchDBUserUtil().getUser(username);
        ArrayList<Walk> walkHistory = user.getWalkList();

        init(savedInstanceState);

        if (walkHistory.size()==0){
            Toast.makeText(MyStats.this, "No walks yet!", Toast.LENGTH_SHORT).show();
        } else {
            populateDisplayedWalkList(walkHistory);
        }
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

    private void populateDisplayedWalkList(ArrayList<Walk> walkHistory) {
        for (Walk walk : walkHistory) {
            Walk w = new Walk(walk.finalDistance, walk.walkDuration, walk.logDate);
            displayedWalkList.add(w);
            recyclerAdapter.notifyDataSetChanged();
        }
    }

    private void init(Bundle savedInstanceState) {
        initialItemData(savedInstanceState);
        createRecyclerView();
    }


    private void createRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.recycler_view_walks);
        RecyclerView.LayoutManager rLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(rLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerAdapter = new MyStatsRecyclerAdapter(displayedWalkList);
        recyclerView.setAdapter(recyclerAdapter);
    }

    private void initialItemData(Bundle savedInstanceState) {
        if (savedInstanceState != null && savedInstanceState.containsKey(NUMBER_OF_ITEMS)) {
            if (displayedWalkList == null || displayedWalkList.size() == 0) {
                int size = savedInstanceState.getInt(NUMBER_OF_ITEMS);
                for (int i = 0; i < size; i++) {
                    long walkDistance = Long.parseLong(savedInstanceState.getString(KEY_OF_INSTANCE + i + "0"));
                    double walkDuration = Double.parseDouble(savedInstanceState.getString(KEY_OF_INSTANCE + i + "1"));
                    String walkDate = savedInstanceState.getString(KEY_OF_INSTANCE + i + "2");
                    Walk walkCard = new Walk(walkDistance, walkDuration, walkDate);
                    displayedWalkList.add(walkCard);
                }
            }
        }
    }

    protected void onSaveInstanceState(@NonNull Bundle outState) {
        int size = displayedWalkList == null ? 0 : displayedWalkList.size();
        outState.putInt(NUMBER_OF_ITEMS, size);

        for (int i = 0; i < size; i++) {
            outState.putString(KEY_OF_INSTANCE + i + "0", String.valueOf(displayedWalkList.get(i).getFinalDistance()));
            outState.putString(KEY_OF_INSTANCE + i + "1", String.valueOf(displayedWalkList.get(i).getWalkDuration()));
            outState.putString(KEY_OF_INSTANCE + i + "2", displayedWalkList.get(i).getLogDate());
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, HomePage.class);
        intent.putExtra(MainActivity.USERKEY,username);
        startActivity(intent);
    }
}
