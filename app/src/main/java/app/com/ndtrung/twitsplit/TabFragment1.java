package app.com.ndtrung.twitsplit;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class TabFragment1 extends Fragment {
    private List<TweetMessage> tweets;
    private RecyclerView listOfTweets;
    private TweetListAdapter tweetsAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_fragment_1, container, false);

        tweetsAdapter = new TweetListAdapter();
        tweets = new ArrayList<>();
        listOfTweets = (RecyclerView) view.findViewById(R.id.list_of_tweets);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        listOfTweets.setLayoutManager(layoutManager);
        listOfTweets.setAdapter(tweetsAdapter);

        return view;
    }

    public void displayTweet(final boolean isPrivateTweetScreen) {
        Log.i("ndt", "displayTweet");
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                tweets.clear();
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    TweetMessage tweet = postSnapshot.getValue(TweetMessage.class);
                    Log.i("ndt", "isPrivateTweetScreen = " + isPrivateTweetScreen);
                    if (user != null)
                        Log.i("ndt", "user.getDisplayName() = " + user.getDisplayName());
                    if (isPrivateTweetScreen){
                        if(user != null && tweet.getTweetUser().equals(user.getDisplayName())) {
                            tweets.add(tweet);
                        }
                    } else {
                        tweets.add(tweet);
                    }
                }
                Collections.sort(tweets, new ObjectComparator());
                tweetsAdapter.setAdapterData(tweets);
                tweetsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    public class ObjectComparator implements Comparator<TweetMessage> {
        @Override
        public int compare(TweetMessage o, TweetMessage o2) {
            try {
                String d1 = DateFormat.format(Contains.DATE_TIME_PATTER, o.getTweetTime()).toString();
                String d2 = DateFormat.format(Contains.DATE_TIME_PATTER, o2.getTweetTime()).toString();
                return new SimpleDateFormat(Contains.DATE_TIME_PATTER).parse(d2).compareTo(new SimpleDateFormat(Contains.DATE_TIME_PATTER).parse(d1));
            } catch (Exception e) {
                return 0;
            }
        }
    }
}
