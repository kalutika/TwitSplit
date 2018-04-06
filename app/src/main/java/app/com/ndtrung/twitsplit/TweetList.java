package app.com.ndtrung.twitsplit;

import android.app.Activity;
import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class TweetList extends ArrayAdapter<TweetMessage> {
    private Context context;
    List<TweetMessage> tweets;

    public TweetList(Context context, List<TweetMessage> tracks) {
        super(context, R.layout.tweet, tracks);
        this.context = context;
        this.tweets = tracks;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.tweet, null, true);

        TextView tweetText = (TextView) v.findViewById(R.id.tweet_text);
        TextView tweetUser = (TextView) v.findViewById(R.id.tweet_user);
        TextView tweetTime = (TextView) v.findViewById(R.id.tweet_time);

        TweetMessage model = tweets.get(position);
        tweetText.setText(model.getTweetText()); // content
        tweetUser.setText(model.getTweetUser()); // user name
        tweetTime.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)", model.getTweetTime())); // format time

        return v;
    }
}
