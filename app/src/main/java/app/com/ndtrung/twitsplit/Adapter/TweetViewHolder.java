package app.com.ndtrung.twitsplit.Adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import app.com.ndtrung.twitsplit.R;

public class TweetViewHolder extends RecyclerView.ViewHolder {
    TextView tweetText;
    TextView tweetUser;
    TextView tweetTime;

    public TweetViewHolder(View itemView) {
        super(itemView);
        tweetText = (TextView) itemView.findViewById(R.id.tweet_text);
        tweetUser = (TextView) itemView.findViewById(R.id.tweet_user);
        tweetTime = (TextView) itemView.findViewById(R.id.tweet_time);
    }
}
