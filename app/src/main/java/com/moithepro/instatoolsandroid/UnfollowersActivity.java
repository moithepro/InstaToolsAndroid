package com.moithepro.instatoolsandroid;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.PersistableBundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.moithepro.instatoolsandroid.jInstaloader.JException;
import com.moithepro.instatoolsandroid.jInstaloader.JInstaProfile;
import com.moithepro.instatoolsandroid.jInstaloader.JInstaloader;
import com.moithepro.instatoolsandroid.jInstaloader.JLoginRequiredException;
import com.moithepro.instatoolsandroid.jInstaloader.JPrivateProfileNotFollowedException;
import com.moithepro.instatoolsandroid.jInstaloader.JProfileNotExistsException;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

public class UnfollowersActivity extends AppCompatActivity {
    private LinearLayout ll;
    private ArrayList<Button> buttonsList = new ArrayList<>();
    private ArrayList<JInstaProfile> unfollowers = new ArrayList<>();
    private DrawerLayout dl;
    private ActionBarDrawerToggle abdt;
    private Toolbar tb;
    private int index;
    private JInstaloader loader;
    private JInstaProfile profile;
    private Thread loaderThread;
    private boolean loaderRunning = false;
    private TextView statusText;
    private AlertDialog AD;
    private SharedPreferences sp;

    private enum Mode {UNFOLLOWERS, WHITELIST}

    private Mode currentMode = Mode.UNFOLLOWERS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.unfollowers_activity);
        loader = InstaloaderSingleton.getInstance().getLoader();
        ll = findViewById(R.id.unfollowers_layout);
        dl = findViewById(R.id.unfollowers_activity);
        tb = findViewById(R.id.toolbar);
        statusText = findViewById(R.id.status_text);
        abdt = new ActionBarDrawerToggle(this, dl, tb, R.string.open, R.string.close);
        abdt.setDrawerIndicatorEnabled(true);
        dl.addDrawerListener(abdt);
        abdt.syncState();
        final NavigationView nav_view = findViewById(R.id.nv);
        nav_view.setNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.whitelist) {
                tb.setTitle("Whitelisted of @" + profile.getUsername());
                currentMode = Mode.WHITELIST;
                setUnfollowers(unfollowers);
                return true;
            }
            if (item.getItemId() == R.id.profile_menu_unfollowers) {
                tb.setTitle("Unfollowers of @" + profile.getUsername());
                currentMode = Mode.UNFOLLOWERS;
                setUnfollowers(unfollowers);
                return true;
            }
            return super.onOptionsItemSelected(item);
        });
        Bundle b = getIntent().getExtras();

        index = b.getInt(getString(R.string.index_key));
        profile = (JInstaProfile) b.getSerializable(getString(R.string.profile_key));
        boolean first = b.getBoolean(getString(R.string.first_key));
        tb.setTitle(getString(R.string.unfollowers_of) + profile.getUsername());
        tb.setOnMenuItemClickListener(item -> {
            if (!loaderRunning) {
                if (item.getItemId() == R.id.refresh) {
                    refreshUnfollowers();
                }
            }
            return true;
        });
        sp = getSharedPreferences(getString(R.string.unfollowers_activity_key) + index + "_" + profile.getUsername(), MODE_PRIVATE);
        String json = sp.getString(getString(R.string.unfollowers_list_key), "");
        if (json == "")
            refreshUnfollowers();
        else {
            Gson gson = new Gson();
            ArrayList<JInstaProfile> unfollowers = gson.fromJson(json, new TypeToken<ArrayList<JInstaProfile>>() {
            }.getType());
            setUnfollowers(unfollowers);
        }
    }

    private void setUnfollowers(ArrayList<JInstaProfile> unfollowers) {
        for (Button b :
                buttonsList) {
            ll.removeView(b);
        }
        buttonsList.clear();

        this.unfollowers = unfollowers;
        if (currentMode == Mode.UNFOLLOWERS) {
            reloadUnfollowers();
        } else if (currentMode == Mode.WHITELIST) {
            reloadWhitelist();
        }
        for (Button b :
                buttonsList) {
            b.setOnClickListener(view -> {
                if (view.getTag() instanceof JInstaProfile) {
                    Uri uri = Uri.parse("https://www.instagram.com/" + ((JInstaProfile) view.getTag()).getUsername()); // missing 'http://' will cause crash
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }
            });
            b.setOnLongClickListener(view -> {
                if (currentMode == Mode.UNFOLLOWERS) {
                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case DialogInterface.BUTTON_POSITIVE:
                                    List<JInstaProfile> whitelist = new ArrayList<>();
                                    String json = sp.getString(getString(R.string.whitelist_key) + index + "_" + profile.getUsername(), "");
                                    if (json != "") {
                                        Gson gson = new Gson();
                                        whitelist = gson.fromJson(json, new TypeToken<ArrayList<JInstaProfile>>() {
                                        }.getType());
                                    }
                                    whitelist.add((JInstaProfile) view.getTag());
                                    Gson gson = new Gson();
                                    String json2 = gson.toJson(whitelist);
                                    sp.edit().putString(getString(R.string.whitelist_key) + index + "_" + profile.getUsername(), json2).apply();
                                    setUnfollowers(unfollowers);
                                    break;

                                case DialogInterface.BUTTON_NEGATIVE:
                                    //No button clicked
                                    break;
                            }
                        }
                    };

                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("WhiteList?").setPositiveButton("Yes", dialogClickListener)
                            .setNegativeButton("No", dialogClickListener).show();
                } else if (currentMode == Mode.WHITELIST) {
                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case DialogInterface.BUTTON_POSITIVE:
                                    List<JInstaProfile> whitelist = new ArrayList<>();
                                    String json = sp.getString(getString(R.string.whitelist_key) + index + "_" + profile.getUsername(), "");
                                    if (json != "") {
                                        Gson gson = new Gson();
                                        whitelist = gson.fromJson(json, new TypeToken<ArrayList<JInstaProfile>>() {
                                        }.getType());
                                    }
                                    whitelist.remove((JInstaProfile) view.getTag());
                                    Gson gson = new Gson();
                                    String json2 = gson.toJson(whitelist);
                                    sp.edit().putString(getString(R.string.whitelist_key) + index + "_" + profile.getUsername(), json2).apply();
                                    setUnfollowers(unfollowers);
                                    break;

                                case DialogInterface.BUTTON_NEGATIVE:
                                    //No button clicked
                                    break;
                            }
                        }
                    };

                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("Remove from whitelist?").setPositiveButton("Yes", dialogClickListener)
                            .setNegativeButton("No", dialogClickListener).show();
                }
                return false;
            });
        }
    }

    private boolean isWhitelisted(JInstaProfile p) {
        String json = sp.getString(getString(R.string.whitelist_key) + index + "_" + profile.getUsername(), "");

        if (json == "")
            return false;
        Gson gson = new Gson();
        List<JInstaProfile> whitelist = gson.fromJson(json, new TypeToken<ArrayList<JInstaProfile>>() {
        }.getType());
        for (JInstaProfile profile :
                whitelist) {
            if (profile.getUsername().equals(p.getUsername()))
                return true;
        }
        return false;
    }

    private void refreshUnfollowers() {
        statusText.setText(R.string.refreshing);
        loaderRunning = true;
        Handler mainHandler = new Handler(Looper.getMainLooper());
        loaderThread = new Thread(() -> {
            try {
                mainHandler.post(() -> {
                    statusText.setText(R.string.retrieving_followers);
                });
                List<JInstaProfile> followers = loader.getFollowers(profile.getUsername());
                mainHandler.post(() -> {
                    statusText.setText(R.string.retrieving_following);
                });
                List<JInstaProfile> following = loader.getFollowing(profile.getUsername());
                mainHandler.post(() -> {
                    statusText.setText(R.string.refreshing);
                });
                ArrayList<JInstaProfile> unfollowers = new ArrayList<>();
                unfollowers.addAll(following);
                unfollowers.removeAll(followers);
                mainHandler.post(() -> {
                    Gson gson = new Gson();

                    sp.edit().putString(getString(R.string.unfollowers_list_key), gson.toJson(unfollowers)).commit();

                    setUnfollowers(unfollowers);

                    statusText.setText("");
                });
            } catch (JProfileNotExistsException e) {
                mainHandler.post(() -> {
                    errorUsernameNotExists();
                    statusText.setText("");
                });
            } catch (JLoginRequiredException | JPrivateProfileNotFollowedException e) {
                mainHandler.post(() -> {
                    errorUserNotAccessible();
                    statusText.setText("");
                });
            } catch (JException e) {
                mainHandler.post(() -> {
                    errorUnexpected();
                    statusText.setText("");
                });
            }
            mainHandler.post(() -> {
                statusText.setText("");
            });
            loaderRunning = false;
        });
        loaderThread.start();
    }

    private void errorUnexpected() {

        AD = new AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage("Unexpected Error. Check Internet connection or try to log in to Instagram using the App")

                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton(android.R.string.ok, null)

                // A null listener allows the button to dismiss the dialog and take no further action.
                .show();
    }

    private void errorUsernameNotExists() {

        AD = new AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage("Username Does not Exist, Please Check if Username has changed.")

                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton(android.R.string.ok, null)

                // A null listener allows the button to dismiss the dialog and take no further action.
                .show();
    }

    private void errorUserNotAccessible() {

        AD = new AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage("User is not Accessible (Is private and not followed.)")

                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton(android.R.string.ok, null)

                // A null listener allows the button to dismiss the dialog and take no further action.
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }
    // when navigation button is pressed


    private void reloadUnfollowers() {
        // get whitelist from shared preferences
        String json = sp.getString(getString(R.string.whitelist_key) + index + "_" + profile.getUsername(), "");
        List<JInstaProfile> whitelist = new ArrayList<>();
        if (json.isEmpty()) {

        } else {
            Gson gson = new Gson();
            whitelist = gson.fromJson(json, new TypeToken<List<JInstaProfile>>() {
            }.getType());
        }

        List<JInstaProfile> unfollowers = new ArrayList<>(this.unfollowers);

        for (JInstaProfile p :
                whitelist) {
            unfollowers.remove(p);
        }
        for (Button b :
                buttonsList) {
            ll.removeView(b);
        }
        buttonsList.clear();
        for (JInstaProfile p :
                unfollowers) {
            addButtonToLayout((p.isVerified() ? getString(R.string.verified) : "") + "@" + p.getUsername(), p);
        }
        currentMode = Mode.UNFOLLOWERS;

    }

    private void reloadWhitelist() {
        // get whitelist from shared preferences
        String json = sp.getString(getString(R.string.whitelist_key) + index + "_" + profile.getUsername(), "");
        List<JInstaProfile> whitelist = new ArrayList<>();
        if (json.isEmpty()) {

        } else {
            Gson gson = new Gson();
            whitelist = gson.fromJson(json, new TypeToken<List<JInstaProfile>>() {
            }.getType());
        }

        for (Button b :
                buttonsList) {
            ll.removeView(b);
        }
        buttonsList.clear();
        for (JInstaProfile p :
                whitelist) {
            addButtonToLayout("⭐" + (p.isVerified() ? getString(R.string.verified) : "") + "@" + p.getUsername(), p);
        }
        currentMode = Mode.WHITELIST;

    }

    public Button addButtonToLayout(String username, Object tag) {
        Button button = new Button(this);
        button.setTag(tag);
        ViewGroup.LayoutParams p = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        p.width = ViewGroup.LayoutParams.MATCH_PARENT;
        p.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        int dpValue = 10;
        float dpRatio = this.getResources().getDisplayMetrics().density;
        int pixelForDp = (int) (dpValue * dpRatio);
        ((ViewGroup.MarginLayoutParams) p).topMargin = pixelForDp;
        button.setLayoutParams(p);
        button.setText(username);
        button.setAllCaps(false);
        button.setBackground(ContextCompat.getDrawable(this, R.drawable.round));
        ll.addView(button);
        buttonsList.add(button);
        return button;
    }

    protected void onDestroy() {
        super.onDestroy();
        if (AD != null)
            AD.dismiss();
    }
}
