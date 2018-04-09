package app.com.ndtrung.twitsplit.Adapter;

import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import app.com.ndtrung.twitsplit.R;
import app.com.ndtrung.twitsplit.TweetMessage;

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

    public void clear() {
        list.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<TweetMessage> list) {
        list.addAll(list);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }
}
