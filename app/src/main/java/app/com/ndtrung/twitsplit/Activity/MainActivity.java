package app.com.ndtrung.twitsplit.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
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

import app.com.ndtrung.twitsplit.Utils.Constants;
import app.com.ndtrung.twitsplit.CustomTabLayout;
import app.com.ndtrung.twitsplit.Adapter.PagerAdapter;
import app.com.ndtrung.twitsplit.R;
import app.com.ndtrung.twitsplit.Fragment.TabFragment1;
import app.com.ndtrung.twitsplit.TweetMessage;
import app.com.ndtrung.twitsplit.Utils.Utils;


public class MainActivity extends AppCompatActivity {
    private static final int SIGN_IN_REQUEST_CODE = 1;
    private static final String STRING_WELCOME = "";
    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebasedatabase;

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private TextView tvTitle;

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
                openInputDialog();
            }
        });

        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        collapsingToolbarLayout.setTitle(getResources().getString(R.string.app_name));
        collapsingToolbarLayout.setExpandedTitleColor(Color.TRANSPARENT);
        collapsingToolbarLayout.setCollapsedTitleTextColor(getResources().getColor(R.color.textColorPrimary));

        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvTitle.setText(Constants.ONE_SPACE_CHARACTER);

        tabLayout = (CustomTabLayout) findViewById(R.id.tab_layout);
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
                    displayTweets();
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
            tvTitle.setText(Constants.ONE_SPACE_CHARACTER);
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setIsSmartLockEnabled(false)
                            .build(),
                    SIGN_IN_REQUEST_CODE
            );
        } else {
            // User is already signed in
            tvTitle.setText(STRING_WELCOME + mAuth.getCurrentUser().getDisplayName());
            // Load contents
            displayTweets();
        }
    }

    private void openInputDialog() {
        if (mAuth.getCurrentUser() != null && mFirebasedatabase != null) {

            LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
            View promptsView = inflater.inflate(R.layout.input_layout, null);

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this, R.style.InputDialog);
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
            alertDialog.show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SIGN_IN_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                tvTitle.setText(STRING_WELCOME + mAuth.getCurrentUser().getDisplayName());
                displayTweets();
            } else {
                tvTitle.setText(Constants.ONE_SPACE_CHARACTER);
                Toast.makeText(this,
                        R.string.sign_in_failed,
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
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            logout();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        tvTitle.setText(Constants.ONE_SPACE_CHARACTER);
        Intent intent = new Intent(MainActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
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
        if (!Utils.checkInvalidString(arr)) {
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
        if (mAuth.getCurrentUser() != null && mFirebasedatabase != null) {
            mFirebasedatabase
                    .getReference()
                    .push()
                    .setValue(new TweetMessage(str, mAuth.getCurrentUser().getDisplayName())
                    );
        }
    }
}