package app.com.ndtrung.twitsplit;

import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class TweetListAdapter extends RecyclerView.Adapter<TweetViewHolder> {
    private List<TweetMessage> list;

    public TweetListAdapter() {
    }

    public void setAdapterData(List<TweetMessage> list) {
        this.list = list;
    }

    @Override
    public TweetViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView = inflater.inflate(R.layout.tweet, parent, false);
        TweetViewHolder viewHolder = new TweetViewHolder(itemView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(TweetViewHolder holder, int position) {
        TweetMessage tweet = list.get(position);
        holder.tweetText.setText(tweet.getTweetText()); // content
        holder.tweetUser.setText(tweet.getTweetUser()); // user name
        holder.tweetTime.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)", tweet.getTweetTime())); // format time
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }
}
