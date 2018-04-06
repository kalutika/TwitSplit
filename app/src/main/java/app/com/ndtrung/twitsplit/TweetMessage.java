package app.com.ndtrung.twitsplit;

import java.util.Date;

public class TweetMessage {
    private String tweetText;
    private String tweetUser;
    private long tweetTime;

    public TweetMessage(String messageText, String messageUser) {
        this.tweetText = messageText;
        this.tweetUser = messageUser;

        // Initialize to current time
        tweetTime = new Date().getTime();
    }

    public TweetMessage(){

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
}
