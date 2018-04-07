package app.com.ndtrung.twitsplit;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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


public class MainActivity extends AppCompatActivity {
    private static final int SIGN_IN_REQUEST_CODE = 1;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebasedatabase;

    private TabLayout tabLayout;
    private ViewPager viewPager;
    //Layout
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
                openInpputDialog();
            }
        });

        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        if (tabLayout != null) {
            tabLayout.addTab(tabLayout.newTab().setText("All Tweets"));
            tabLayout.addTab(tabLayout.newTab().setText("Home"));

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

    private void openInpputDialog() {
        if (mAuth.getCurrentUser() != null && mFirebasedatabase != null) {

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
            alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {

                @Override
                public void onShow(DialogInterface dialogInterface) {

                    Button button = ((AlertDialog) alertDialog).getButton(AlertDialog.BUTTON_POSITIVE);
                    button.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {
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


    private boolean postTweet(String text, TextView tvError) {
        if (text.isEmpty()) {
            tvError.setText(R.string.err_tweet_empty);
            return false;
        }
        if (text.length() <= Constants.MAX_CHARACTERS_NUMBER) {
            pushToServer(text);
            return true;
        }
        String arr[] = text.split(Constants.ONE_SPACE_CHARACTER);
        if (!checkInvalidString(arr)) {
            tvError.setText(R.string.err_tweet_invalid);
            return false;
        }
        String[] resultArr = Utils.splitMessage(arr);
        if (resultArr.length == 0) {
            tvError.setText(R.string.err_tweet_invalid);
        }

        for (int i = 0; i < resultArr.length; i++) {
            pushToServer(resultArr[i]);
        }
        return true;
    }

    private void pushToServer(String str) {
        Log.i("ndt", "Push string to server = " + str);
        if (mAuth.getCurrentUser() != null && mFirebasedatabase != null) {
            mFirebasedatabase
                    .getReference()
                    .push()
                    .setValue(new TweetMessage(str, mAuth.getCurrentUser().getDisplayName())
                    );
        }
    }
}