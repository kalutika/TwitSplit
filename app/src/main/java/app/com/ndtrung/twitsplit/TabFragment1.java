package app.com.ndtrung.twitsplit;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class TabFragment1 extends Fragment {
    private FirebaseListAdapter<TweetMessage> adapter;
    private List<TweetMessage> tweets;
    private ListView listOfTweet;
//    private RecyclerView listOfTweets;
    private TweetListAdapter tweetsAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_fragment_1, container, false);

        listOfTweet = (ListView) view.findViewById(R.id.list_of_tweets);
//        adapter = new FirebaseListAdapter<TweetMessage>(getActivity(), TweetMessage.class, R.layout.tweet, FirebaseDatabase.getInstance().getReference()) {
//            @Override
//            protected void populateView(View v, TweetMessage model, int position) {
//                TextView tweetText = (TextView) v.findViewById(R.id.tweet_text);
//                TextView tweetUser = (TextView) v.findViewById(R.id.tweet_user);
//                TextView tweetTime = (TextView) v.findViewById(R.id.tweet_time);
//
//                tweetText.setText(model.getTweetText()); // content
//                tweetUser.setText(model.getTweetUser()); // user name
//                tweetTime.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)", model.getTweetTime())); // format time
//            }
//        };
//        listOfTweet.setAdapter(adapter);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        tweets = new ArrayList<>();
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                tweets.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    TweetMessage track = postSnapshot.getValue(TweetMessage.class);
                    tweets.add(0, track);
                }
                if (tweets != null || !tweets.isEmpty() || getContext() != null) {
                    TweetList tweetListAdapter = new TweetList(getContext(), tweets);
                    listOfTweet.setAdapter(tweetListAdapter);
                } else {
                    listOfTweet.setAdapter(null);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

//        listOfTweets = (RecyclerView) view.findViewById(R.id.list_of_tweets);
//        tweetsAdapter = new TweetListAdapter();
//        listOfTweets.setAdapter(tweetsAdapter);
        return view;
    }
}
