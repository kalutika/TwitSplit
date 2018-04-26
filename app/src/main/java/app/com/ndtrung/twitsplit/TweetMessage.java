package app.com.ndtrung.twitsplit;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class TweetMessage implements Parcelable {
    private String tweetText;
    private String tweetUser;
    private long tweetTime;

    public TweetMessage(String messageText, String messageUser) {
        this.tweetText = messageText;
        this.tweetUser = messageUser;

        // Initialize to current time
        tweetTime = new Date().getTime();
    }

    public TweetMessage() {

    }

    public TweetMessage(Parcel in) {
        super();
        readFromParcel(in);
    }

    public String getTweetText() {
        return tweetText;
    }

    public void setTweetText(String messageText) {
        this.tweetText = messageText;
    }

    public String getTweetUser() {
        return tweetUser;
    }

    public void seTweetUser(String messageUser) {
        this.tweetUser = messageUser;
    }

    public long getTweetTime() {
        return tweetTime;
    }

    public void setTweetTime(long messageTime) {
        this.tweetTime = messageTime;
    }

    public static final Parcelable.Creator<TweetMessage> CREATOR = new Parcelable.Creator<TweetMessage>() {
        public TweetMessage createFromParcel(Parcel in) {
            return new TweetMessage(in);
        }

        public TweetMessage[] newArray(int size) {

            return new TweetMessage[size];
        }

    };

    public void readFromParcel(Parcel in) {
        this.tweetText = in.readString();
        this.tweetTime = in.readLong();
        this.tweetUser = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.tweetText);
        parcel.writeLong(this.tweetTime);
        parcel.writeString(this.tweetUser);
    }
}
