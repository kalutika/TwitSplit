package app.com.ndtrung.twitsplit;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
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
                    mFirebasedatabase
                            .getReference()
                            .push()
                            .setValue(new TweetMessage(str, mAuth.getCurrentUser().getDisplayName())
                            );
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
                    if ( tab.getPosition() == 0 ||  tab.getPosition() == 1) {
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
}
