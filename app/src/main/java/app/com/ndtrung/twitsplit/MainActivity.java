package app.com.ndtrung.twitsplit;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.SyncStateContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final int SIGN_IN_REQUEST_CODE = 1;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebasedatabase;

    private TabLayout tabLayout;
    private ViewPager viewPager;
    //Layout
    public static int[] resourceIds = {R.layout.tab_fragment_1, R.layout.tab_fragment_2, R.layout.tab_fragment_3};
    private static boolean isCalledAlready = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String str = "this is a test";//input.getText().toString()
                if (mAuth.getCurrentUser() != null && mFirebasedatabase != null) {
                    Log.i("ndt", "i'm in");
//                    mFirebasedatabase
//                            .getReference()
//                            .push()
//                            .setValue(new TweetMessage(str, mAuth.getCurrentUser().getDisplayName())
//                            );
                    LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
                    View promptsView = inflater.inflate(R.layout.input_layout, null);

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this, R.style.InputDialog);

                    // set prompts.xml to alertdialog builder
                    alertDialogBuilder.setView(promptsView);

                    final TextView tvError = (TextView) promptsView.findViewById(R.id.tvError);
                    final EditText userInput = (EditText) promptsView.findViewById(R.id.editTextDialogUserInput);

                    // set dialog message
                    alertDialogBuilder
                            .setCancelable(false)
                            .setPositiveButton("OK", null)
                            .setNegativeButton("Cancel",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    });

                    // create alert dialog
                    final AlertDialog alertDialog = alertDialogBuilder.create();
//                    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener()
//                    {
//                        @Override
//                        public void onClick(View v)
//                        {
//                            Toast.makeText(getBaseContext(), userInput.getText(), Toast.LENGTH_LONG).show();
//                            Boolean wantToCloseDialog = false;
//                            //Do stuff, possibly set wantToCloseDialog to true then...
//                            if(wantToCloseDialog)
//                                alertDialog.dismiss();
//                            //else dialog stays open. Make sure you have an obvious way to close the dialog especially if you set cancellable to false.
//                        }
//                    });
                    alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {

                        @Override
                        public void onShow(DialogInterface dialogInterface) {

                            Button button = ((AlertDialog) alertDialog).getButton(AlertDialog.BUTTON_POSITIVE);
                            button.setOnClickListener(new View.OnClickListener() {

                                @Override
                                public void onClick(View view) {
                                    String arr[] = {"1/2 I can't believe Tweeter now supports chunking", "2/2 my messages, so I don't have to do it myself."};
                                    Toast.makeText(MainActivity.this, arr[0].length() + " " + arr[1].length()/*userInput.getText()*/, Toast.LENGTH_LONG).show();
                                    //Dismiss once everything is OK.
                                    String str = userInput.getText().toString().trim();
                                    if (postTweet(str, tvError)) {
                                        userInput.setText(str);
                                        alertDialog.dismiss();
                                    }
                                }
                            });
                        }
                    });
                    // show it
                    alertDialog.show();
                }
            }
        });

        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        if (tabLayout != null) {
            tabLayout.addTab(tabLayout.newTab().setText("Tab 1"));
            tabLayout.addTab(tabLayout.newTab().setText("Tab 2"));
            tabLayout.addTab(tabLayout.newTab().setText("Tab 3"));

            viewPager = (ViewPager) findViewById(R.id.view_pager);
            final PagerAdapter pagerAdapter = new PagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
            viewPager.setAdapter(pagerAdapter);
            viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
            tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    viewPager.setCurrentItem(tab.getPosition());
                    Log.i("ndt", "onTabSelected pos = " + tab.getPosition());
                    if (tab.getPosition() == 0 || tab.getPosition() == 1) {
                        TabFragment1 frag1 = (TabFragment1) viewPager.getAdapter().instantiateItem(viewPager, tab.getPosition());
                        frag1.displayTweet(tab.getPosition() == 1 ? true : false);
                    }
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {

                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            });
        }

        mFirebasedatabase = FirebaseDatabase.getInstance();
        if (!isCalledAlready) {
            mFirebasedatabase.setPersistenceEnabled(true);
            isCalledAlready = true;
        }

        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() == null) {
            // Start sign in/sign up activity
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setIsSmartLockEnabled(false)
                            .build(),
                    SIGN_IN_REQUEST_CODE
            );
        } else {
            // User is already signed in
            Toast.makeText(this,
                    "Welcome " + mAuth.getCurrentUser().getDisplayName(),
                    Toast.LENGTH_LONG)
                    .show();
            // Load contents
            displayTweets();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SIGN_IN_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this,
                        "Successfully signed in. Welcome!",
                        Toast.LENGTH_LONG)
                        .show();
                displayTweets();
            } else {
                Toast.makeText(this,
                        "We couldn't sign you in. Please try again later.",
                        Toast.LENGTH_LONG)
                        .show();

                // Close the app
                finish();
            }
        }
    }

    private void displayTweets() {
        if (viewPager.getCurrentItem() == 0 || viewPager.getCurrentItem() == 1) {
            TabFragment1 frag1 = (TabFragment1) viewPager.getAdapter().instantiateItem(viewPager, viewPager.getCurrentItem());
            frag1.displayTweet(viewPager.getCurrentItem() == 0 ? false : true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            logout();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(MainActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private boolean checkInvalidString(String arr[]) {
        for (String item : arr) {
            if (item.length() > Constants.MAX_CHARACTERS_NUMBER) {
                return false;
            }
        }
        return true;
    }

    private String checkAddingNextString(String nextString, String currentTweet) {
        return currentTweet.isEmpty() ? nextString : (currentTweet + Constants.ONE_SPACE_CHARACTER + nextString);
    }

    private long calculateTotalTweet(String arr[]) {
        long numberOfCharacter = 0;
        for (int i = 0; i < arr.length; i++) {
            numberOfCharacter += arr[i].length() + ((i < (arr.length - 1)) ? 1 : 0);
        }
        Log.i("ndt", "numberOfCharacter = " + numberOfCharacter);
        Log.i("ndt", "calculateTotalTweet = " + (numberOfCharacter / Constants.MAX_CHARACTERS_NUMBER) + 1);
        return (numberOfCharacter / Constants.MAX_CHARACTERS_NUMBER) + 1;
    }

    private boolean postTweet(String text, TextView tvError) {
        if (text.isEmpty()) {
            tvError.setText(R.string.err_tweet_empty);
            return false;
        }
        if (text.length() <= Constants.MAX_CHARACTERS_NUMBER) {
            // TODO: post it
            pushToServer(text);
            return true;
        }
        String arr[] = text.split(" ");
        if (!checkInvalidString(arr)) {
            tvError.setText(R.string.err_tweet_invalid);
            return false;
        }
        List<String> tweetList = new ArrayList<>();
        long totalTweets = calculateTotalTweet(arr);
        int numberOfTweet = 1;
        String tweet = numberOfTweet + "/" + totalTweets + Constants.ONE_SPACE_CHARACTER;
        int i = 0;
        while (i < arr.length) {
            String tmpTweet = checkAddingNextString(arr[i], tweet);
            if (tmpTweet.length() <= Constants.MAX_CHARACTERS_NUMBER) {
                tweet = tmpTweet;
            }
            if (!((i + 1) < arr.length && checkAddingNextString(arr[i + 1], tweet).length() <= Constants.MAX_CHARACTERS_NUMBER)) {
//                totalTweets++;
                tweetList.add(tweet);
                numberOfTweet++;
                tweet = numberOfTweet + "/" + totalTweets + Constants.ONE_SPACE_CHARACTER;
            }
            i++;
        }
        // TODO: post it ---> pushToServer(text);
        for (i = 0; i < tweetList.size(); i++) {
            pushToServer(tweetList.get(i));
        }
        return true;
    }

    private void pushToServer(String str) {
        Log.i("ndt", "Push string to server = " + str);
        if (mAuth.getCurrentUser() != null && mFirebasedatabase != null) {
            Log.i("ndt", "i'm in");
            mFirebasedatabase
                    .getReference()
                    .push()
                    .setValue(new TweetMessage(str, mAuth.getCurrentUser().getDisplayName())
                    );
        }
    }
}