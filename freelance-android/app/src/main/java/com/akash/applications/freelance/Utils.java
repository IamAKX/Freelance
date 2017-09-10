package com.akash.applications.freelance;

/**
 * Created by akash on 23/4/17.
 */


import java.util.ArrayList;
import java.util.List;

/**
 * @author Yalantis
 */
public class Utils {
    public static final List<Friend> friends = new ArrayList<>();

    static {
        friends.add(new Friend(R.drawable.piyali, "DR. PIYALI CHATTERJEE", R.color.sienna, "Project Mentor", "CSE", "Ideas", "Leader", "Guide"));
        friends.add(new Friend(R.drawable.akash, "AKASH GIRI", R.color.saffron, "Android", "Client Side", "App Structure", "Validation", "Design"));
        friends.add(new Friend(R.drawable.ghani, "ABDULLAH GHANI", R.color.green, "Server Handling", "Backend", "Website", "Authorization", "Authentication"));
        friends.add(new Friend(R.drawable.bj, "BISHAL JAISWAL", R.color.pink, "Design", "Website", "Layouts", "Android", "Framework"));
        friends.add(new Friend(R.drawable.yash, "YASH KEDIA", R.color.green, "Website", "Application Flow", "Design", "Layouts", "Management"));
        friends.add(new Friend(R.drawable.nsec_logo, "N S E C", R.color.purple, "CSE", "Final Year", "Project", "Batch 2013-17", "Second Home"));
    }
}