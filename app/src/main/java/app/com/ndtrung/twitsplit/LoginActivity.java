package app.com.ndtrung.twitsplit;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.DefaultLogger;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;
import com.twitter.sdk.android.core.models.User;
import com.twitter.sdk.android.core.services.StatusesService;
import com.twitter.sdk.android.tweetcomposer.ComposerActivity;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;

import cz.msebera.android.httpclient.Header;

public class LoginActivity extends AppCompatActivity {
    final String URL_TWITTER_SIGN_IN = "http://androidsmile.com/lab/twitter/sign_in.php";
    final String URL_TWITTER_GET_USER_TIMELINE = "http://androidsmile.com/lab/twitter/get_user_timeline.php";

    final int TWEET_COMPOSER_RESULT = 1000;

    private TwitterLoginButton loginButton;
    private Button btnTweet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);
        btnTweet = (Button) findViewById(R.id.btn_tweet);

        Twitter.initialize(this);
        TwitterConfig config = new TwitterConfig.Builder(this)
                .logger(new DefaultLogger(Log.DEBUG))
                .twitterAuthConfig(new TwitterAuthConfig("CONSUMER_KEY", "CONSUMER_SECRET"))
                .debug(true)
                .build();
        Twitter.initialize(config);

        loginButton = (TwitterLoginButton) findViewById(R.id.login_butotn);
        loginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                // Do something with result, which provides a TwitterSession for making API calls
                Toast.makeText(getApplication(), "Login successful!", Toast.LENGTH_LONG).show();
                TwitterSession session = TwitterCore.getInstance().getSessionManager().getActiveSession();
                TwitterAuthToken authToken = session.getAuthToken();
                String token = authToken.token;
                String secret = authToken.secret;
                Log.i("ndt", "Twitte token = " + token);
                Log.i("ndt", "Twitte secret = " + secret);
            }

            @Override
            public void failure(TwitterException exception) {
                // Do something on failure
                Toast.makeText(getApplication(), "Login failure!!! Please try again.", Toast.LENGTH_LONG).show();
            }
        });

        btnTweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final TwitterSession session = TwitterCore.getInstance().getSessionManager().getActiveSession();
                TwitterAuthToken authToken = session.getAuthToken();
//
//        Log.i("ndt", "Twitte auth isExpired() = " + authToken.isExpired());
//        if(!authToken.isExpired()) {
//            TweetComposer.Builder builder = new TweetComposer.Builder(this)
//                    .text("This is the tweet test.");
//            builder.show();
//        }
//                Intent intent = new TweetComposer.Builder(getApplication())
//                        .text("Tweet direct NDT!")
//                        .createIntent();
//                startActivityForResult(intent, TWEET_COMPOSER_RESULT);
//                final TwitterSession session = TwitterCore.getInstance().getSessionManager()
//                        .getActiveSession();
//                final Intent intent = new ComposerActivity.Builder(LoginActivity.this)
//                        .session(session)
//                        .text("Love where you work")
//                        .hashtags("#twitter")
//                        .createIntent();
//                startActivity(intent);

                StatusesService statusesService = TwitterCore.getInstance().getApiClient(session).getStatusesService();
                statusesService.update("this is a test status", null, null, null, null, null, null, null, null );
//                TwitterCore.getInstance().getApiClient(session).getAccountService().verifyCredentials(false, true, false).enqueue(new Callback<User>() {
//                    @Override
//                    public void success(Result<User> result) {
//                        Log.i("ndt", "OK");
//                        User user = result.data;
//                        Log.i("ndt", "aaaaaa = " + user.name);
////                        user.status.
//                    }
//
//                    @Override
//                    public void failure(TwitterException exception) {
//                        Log.i("ndt", "FAIL");
//                    }
//                });
//                StatusesService statusesService = TwitterCore.getInstance().getApiClient(session).getAccountService().verifyCredentials(false,true,false).enqueue(new Callback<User>() {
//
//                    @Override
//                    public void success(Result<User> result) {
//                        Log.i("ndt", "OK");
//                    }
//
//                    @Override
//                    public void failure(TwitterException exception) {
//                        Log.i("ndt", "FAIL");
//                    }
//                });
            }
        });
//        signIn();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Pass the activity result to the login button.
        loginButton.onActivityResult(requestCode, resultCode, data);

        if (requestCode == TWEET_COMPOSER_RESULT) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this,
                        "Successfully posted",
                        Toast.LENGTH_LONG)
                        .show();
            } else {
                Toast.makeText(this,
                        "FAIL POST",
                        Toast.LENGTH_LONG)
                        .show();
            }
        }
    }
    private void signIn() {

        final Dialog authDialog = new Dialog(this);

        WebView webview = new WebView(this);
        authDialog.setContentView(webview);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.loadUrl(URL_TWITTER_SIGN_IN);

        webview.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (url.contains("callback.php")) {
                    view.loadUrl("javascript:JsonViewer.onJsonReceived(document.getElementsByTagName('body')[0].innerHTML);");
                    authDialog.dismiss();
                }
            }
        });
        webview.addJavascriptInterface(new MyJavaScriptInterface(getApplicationContext()), "JsonViewer");
        authDialog.setCancelable(false);
        authDialog.show();
    }

    /*
        this interface is used to get json from webview
    */
    class MyJavaScriptInterface {
        private Context ctx;

        MyJavaScriptInterface(Context ctx) {
            this.ctx = ctx;
        }

        @JavascriptInterface
        public void onJsonReceived(String json) {
            Gson gson = new GsonBuilder().create();
            final OauthResult oauthResult = gson.fromJson(json, OauthResult.class);
            if (oauthResult != null && oauthResult.getOauthToken() != null && oauthResult.getOauthTokenSecret() != null) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        getTweets(oauthResult.getOauthToken(), oauthResult.getOauthTokenSecret(), oauthResult.getScreenName());
                        postTweets();
                    }
                });
            }
        }
    }

    private void postTweets() {
    }

    //requests user timeline if successful, show response json in toast

    private void getTweets(String oAuthToken, String oAuthTokenSecret, String screenName) {
        RequestParams params = new RequestParams();
        params.add("oauth_token", oAuthToken);
        params.add("oauth_token_secret", oAuthTokenSecret);
        params.add("screen_name", screenName);

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(this, URL_TWITTER_GET_USER_TIMELINE, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String json = new String(responseBody);
                Toast.makeText(getApplicationContext(), json, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }

}
