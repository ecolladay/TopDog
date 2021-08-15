package edu.neu.madcourse.topdog.DatabaseObjects;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class FetchDBAllUsersUtil {

    ArrayList<User> allUsers = new ArrayList<>();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public ArrayList<User> getAllUsers() {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("USERS");
        String url = mDatabase.toString() + ".json";

        //Make an async request to the Database
        Future<ArrayList<User>> futureUser = executor.submit(() -> {
            FetchDBAllUsersUtil.RunnableFetch fetchRequest = new FetchDBAllUsersUtil.RunnableFetch(url);
            fetchRequest.run();
            return allUsers;
        });

        //Wait until the user has been set (in the future, because its on a separate thread)
        while(!futureUser.isDone()) {
            System.out.println("WAITING ON FETCH...");
            try {
                //Try to minimize the sleep time or else UX will suffer:
                Thread.sleep(150);
            } catch(InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
        return this.allUsers;
    }

    //Runnable class to perform database access off the main UI thread
    public class RunnableFetch implements Runnable {

        String url;

        public RunnableFetch(String url) {
            this.url = url;
        }

        @Override
        public void run(){
            try {
                //Connect to the database at the endpoint of the data you need (the user)
                URL endpoint = new URL(url);
                HttpURLConnection conn = (HttpURLConnection) endpoint.openConnection();
                conn.setRequestMethod("GET");
                conn.connect();

                // Read response
                InputStream inputStream = conn.getInputStream();
                JSONObject allUsersAsJSON = new JSONObject(convertStreamToString(inputStream));
                Iterator<String> allUsernames = allUsersAsJSON.keys();

                while(allUsernames.hasNext()) {
                    try {
                        JSONObject aUserAsJson = allUsersAsJSON.getJSONObject(allUsernames.next());
                        User aUser = User.deserialize(aUserAsJson);
                        allUsers.add(aUser);
                    } catch (JSONException e) {
                        System.out.println("JSON ERROR while fetching info from DB: " + e.toString());
                    }
                }
            } catch (Exception e) {
                System.out.println("JSON ERROR while fetching info from DB: " + e.toString());
            }
        }

        private String convertStreamToString(InputStream is) {
            Scanner s = new Scanner(is).useDelimiter("\\A");
            return s.hasNext() ? s.next().replace(",", ",\n") : "";
        }
    }
}
