package com.moithepro.instatoolsandroid;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.moithepro.instatoolsandroid.jInstaloader.JException;
import com.moithepro.instatoolsandroid.jInstaloader.JInstaProfile;
import com.moithepro.instatoolsandroid.jInstaloader.JInstaloader;
import com.moithepro.instatoolsandroid.jInstaloader.JProfileNotExistsException;

public class UsersActivity extends AppCompatActivity {
    private TableRow firstUser;
    private TableRow secondUser;
    private TableRow thirdUser;
    private JInstaProfile firstUserProfile = null;
    private JInstaProfile secondUserProfile = null;
    private JInstaProfile thirdUserProfile = null;
    private TextView firstUsernameTextView;
    private TextView secondUsernameTextView;
    private TextView thirdUsernameTextView;
    private TextView firstFollowersTextView;
    private TextView secondFollowersTextView;
    private TextView thirdFollowersTextView;
    private TextView firstFollowingTextView;
    private TextView secondFollowingTextView;
    private TextView thirdFollowingTextView;
    private ImageView firstImageView;
    private ImageView secondImageView;
    private ImageView thirdImageView;
    private JInstaloader loader;
    private Thread loaderThread;
    private AlertDialog AD;
    private boolean loaderRunning = false;
    private ActionBarDrawerToggle abdt;
    private Toolbar tb;
    private DrawerLayout dl;
    private Intent firstUserActivity = null;
    private Intent secondUserActivity = null;
    private Intent thirdUserActivity = null;
    private SharedPreferences sp;
    private TextView logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.users_activity);
        loader = InstaloaderSingleton.getInstance().getLoader();
        firstUser = findViewById(R.id.first_user);
        secondUser = findViewById(R.id.second_user);
        thirdUser = findViewById(R.id.third_user);
        firstUsernameTextView = findViewById(R.id.first_username);
        secondUsernameTextView = findViewById(R.id.second_username);
        thirdUsernameTextView = findViewById(R.id.third_username);
        firstImageView = findViewById(R.id.first_user_image);
        secondImageView = findViewById(R.id.second_user_image);
        thirdImageView = findViewById(R.id.third_user_image);
        firstFollowingTextView = findViewById(R.id.first_following);
        secondFollowingTextView = findViewById(R.id.second_following);
        thirdFollowingTextView = findViewById(R.id.third_following);
        firstFollowersTextView = findViewById(R.id.first_followers);
        secondFollowersTextView = findViewById(R.id.second_followers);
        thirdFollowersTextView = findViewById(R.id.third_followers);
        logout = findViewById(R.id.logout);
        logout.setOnClickListener(view -> {
            if (!loaderRunning) {
                SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.login_key), MODE_PRIVATE);
                sharedPreferences.edit().remove(getString(R.string.username_key)).remove(getString(R.string.password_key)).apply();
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        sp = getSharedPreferences(getString(R.string.users_activity_key) + "_" + loader.getLoggedInUsername(), MODE_PRIVATE);
        dl = findViewById(R.id.users_activity);
        tb = findViewById(R.id.toolbar);
        tb.setTitle("Users " + "(" + (loader.getLoggedInUsername().equals("") ? ("Guest") : ("@" + loader.getLoggedInUsername())) + ")");
        abdt = new ActionBarDrawerToggle(this, dl, tb, R.string.open, R.string.close);
        abdt.setDrawerIndicatorEnabled(true);
        dl.addDrawerListener(abdt);
        abdt.syncState();
        firstUsernameTextView.setAutoSizeTextTypeUniformWithConfiguration(
                1, 100, 1, TypedValue.COMPLEX_UNIT_DIP);
        secondUsernameTextView.setAutoSizeTextTypeUniformWithConfiguration(
                1, 100, 1, TypedValue.COMPLEX_UNIT_DIP);
        thirdUsernameTextView.setAutoSizeTextTypeUniformWithConfiguration(
                1, 100, 1, TypedValue.COMPLEX_UNIT_DIP);


        firstUser.setOnClickListener(view -> {

            if (!loaderRunning) {
                if (firstUserProfile == null) popUpAddUserEditText(1);
                else {
                    if (firstUserActivity == null) {
                        firstUserActivity = new Intent(this, UnfollowersActivity.class);
                        firstUserActivity.putExtra(getString(R.string.profile_key), firstUserProfile);
                        firstUserActivity.putExtra(getString(R.string.index_key), 1);
                        firstUserActivity.putExtra(getString(R.string.first_key), true);
                    } else {
                        firstUserActivity.putExtra(getString(R.string.profile_key), firstUserProfile);
                        firstUserActivity.putExtra(getString(R.string.index_key), 1);
                        firstUserActivity.putExtra(getString(R.string.first_key), false);
                    }
                    startActivity(firstUserActivity);
                }
            }
        });

        secondUser.setOnClickListener(view -> {
            if (!loaderRunning) {
                if (secondUserProfile == null) popUpAddUserEditText(2);
                else {
                    if (secondUserActivity == null) {
                        secondUserActivity = new Intent(this, UnfollowersActivity.class);
                        secondUserActivity.putExtra(getString(R.string.profile_key), secondUserProfile);
                        secondUserActivity.putExtra(getString(R.string.index_key), 2);
                        secondUserActivity.putExtra(getString(R.string.first_key), true);
                    } else {
                        secondUserActivity.putExtra(getString(R.string.profile_key), secondUserProfile);
                        secondUserActivity.putExtra(getString(R.string.index_key), 2);
                        secondUserActivity.putExtra(getString(R.string.first_key), false);
                    }
                    startActivity(secondUserActivity);
                }
            }
        });
        thirdUser.setOnClickListener(view -> {
            if (!loaderRunning) {
                if (thirdUserProfile == null) popUpAddUserEditText(3);
                else {
                    if (thirdUserActivity == null) {
                        thirdUserActivity = new Intent(this, UnfollowersActivity.class);
                        thirdUserActivity.putExtra(getString(R.string.profile_key), thirdUserProfile);
                        thirdUserActivity.putExtra(getString(R.string.index_key), 3);
                        thirdUserActivity.putExtra(getString(R.string.first_key), true);
                    } else {
                        thirdUserActivity.putExtra(getString(R.string.profile_key), thirdUserProfile);
                        thirdUserActivity.putExtra(getString(R.string.index_key), 3);
                        thirdUserActivity.putExtra(getString(R.string.first_key), false);
                    }
                    startActivity(thirdUserActivity);
                }
            }
        });
        firstUser.setLongClickable(true);
        firstUser.setOnLongClickListener(view -> {
            if (!loaderRunning) {
                if (firstUserProfile != null) {
                    PopupMenu popupMenu = new PopupMenu(this, firstUser);

                    // Inflating popup menu from popup_menu.xml file
                    popupMenu.getMenuInflater().inflate(R.menu.user_popup_menu, popupMenu.getMenu());
                    popupMenu.setOnMenuItemClickListener(menuItem -> {
                        if (menuItem.getItemId() == R.id.delete) {
                            DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
                                switch (which) {
                                    case DialogInterface.BUTTON_POSITIVE:
                                        resetUser(1);
                                        break;

                                    case DialogInterface.BUTTON_NEGATIVE:
                                        //No button clicked
                                        break;
                                }
                            };

                            AlertDialog.Builder builder = new AlertDialog.Builder(this);
                            builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
                                    .setNegativeButton("No", dialogClickListener).show();
                        }

                        return true;
                    });
                    // Showing the popup menu
                    popupMenu.show();
                }
            }
            return true;
        });
        secondUser.setLongClickable(true);
        secondUser.setOnLongClickListener(view -> {
            if (!loaderRunning) {
                if (secondUserProfile != null) {
                    PopupMenu popupMenu = new PopupMenu(this, secondUser);

                    // Inflating popup menu from popup_menu.xml file
                    popupMenu.getMenuInflater().inflate(R.menu.user_popup_menu, popupMenu.getMenu());
                    popupMenu.setOnMenuItemClickListener(menuItem -> {
                        if (menuItem.getItemId() == R.id.delete) {
                            DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
                                switch (which) {
                                    case DialogInterface.BUTTON_POSITIVE:
                                        resetUser(2);
                                        break;

                                    case DialogInterface.BUTTON_NEGATIVE:
                                        //No button clicked
                                        break;
                                }
                            };

                            AlertDialog.Builder builder = new AlertDialog.Builder(this);
                            builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
                                    .setNegativeButton("No", dialogClickListener).show();
                        }
                        return true;
                    });
                    // Showing the popup menu
                    popupMenu.show();
                }
            }
            return true;
        });
        thirdUser.setLongClickable(true);
        thirdUser.setOnLongClickListener(view -> {
            if (!loaderRunning) {
                if (thirdUserProfile != null) {
                    PopupMenu popupMenu = new PopupMenu(this, thirdUser);

                    // Inflating popup menu from popup_menu.xml file
                    popupMenu.getMenuInflater().inflate(R.menu.user_popup_menu, popupMenu.getMenu());
                    popupMenu.setOnMenuItemClickListener(menuItem -> {
                        if (menuItem.getItemId() == R.id.delete) {
                            DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
                                switch (which) {
                                    case DialogInterface.BUTTON_POSITIVE:
                                        resetUser(3);
                                        break;

                                    case DialogInterface.BUTTON_NEGATIVE:
                                        //No button clicked
                                        break;
                                }
                            };

                            AlertDialog.Builder builder = new AlertDialog.Builder(this);
                            builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
                                    .setNegativeButton("No", dialogClickListener).show();
                        }
                        return true;
                    });
                    // Showing the popup menu
                    popupMenu.show();
                }
            }
            return true;
        });
        tb.setOnMenuItemClickListener(item -> {
            if (!loaderRunning) {
                if (item.getItemId() == R.id.refresh) {
                    refresh();
                }
            }
            return true;
        });
        Gson gson = new Gson();
        for (int i = 1; i < 3 + 1; i++) {
            String json = sp.getString(getString(R.string.user_key) + "_" + i, null);
            setUserProfile(i, json == null ? null : gson.fromJson(json, JInstaProfile.class));
        }

    }

    private void refresh() {
        tb.setTitle("Refreshing Users...");
        loaderRunning = true;
        Handler mainHandler = new Handler(Looper.getMainLooper());
        loaderThread = new Thread(() -> {
            if (firstUserProfile != null) {
                try {
                    boolean isAccessible = loader.isAccessible(firstUserProfile.getUsername());
                    if (!isAccessible) {
                        mainHandler.post(() -> {
                            errorUserNotAccessible(1);
                        });

                    } else {
                        JInstaProfile profile = loader.getProfileByUsername(firstUserProfile.getUsername());
                        mainHandler.post(() -> {
                            setUserProfile(1, profile);
                        });
                    }
                } catch (JProfileNotExistsException e) {
                    mainHandler.post(() -> {
                        errorUsernameNotExists(1);
                    });
                } catch (JException e) {
                    mainHandler.post(() -> {
                        errorUnexpected(1);
                    });
                }
            }
            if (secondUserProfile != null) {
                try {
                    boolean isAccessible = loader.isAccessible(secondUserProfile.getUsername());
                    if (!isAccessible) {
                        mainHandler.post(() -> {
                            errorUserNotAccessible(2);

                        });

                    } else {
                        JInstaProfile profile = loader.getProfileByUsername(secondUserProfile.getUsername());
                        mainHandler.post(() -> {
                            setUserProfile(2, profile);
                        });
                    }
                } catch (JProfileNotExistsException e) {
                    mainHandler.post(() -> {
                        errorUsernameNotExists(2);
                    });
                } catch (JException e) {
                    mainHandler.post(() -> {
                        errorUnexpected(2);
                    });
                }
            }
            if (thirdUserProfile != null) {
                try {
                    boolean isAccessible = loader.isAccessible(thirdUserProfile.getUsername());
                    if (!isAccessible) {
                        mainHandler.post(() -> {
                            errorUserNotAccessible(3);

                        });

                    } else {
                        JInstaProfile profile = loader.getProfileByUsername(thirdUserProfile.getUsername());
                        mainHandler.post(() -> {
                            setUserProfile(3, profile);
                        });
                    }
                } catch (JProfileNotExistsException e) {
                    mainHandler.post(() -> {
                        errorUsernameNotExists(3);
                    });
                } catch (JException e) {
                    mainHandler.post(() -> {
                        errorUnexpected(3);
                    });
                }
            }
            mainHandler.post(() -> tb.setTitle("Users " + "(" + (loader.getLoggedInUsername().equals("") ? ("Guest") : ("@" + loader.getLoggedInUsername())) + ")"));

            loaderRunning = false;
        });
        loaderThread.start();
    }

    private void popUpAddUserEditText(int index) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add User").setMessage("Username: \n\n@");

        final EditText input = new EditText(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("Add", (dialog, which) -> {
            addUser(index, input.getText().toString().trim());

        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();

    }

    private void setUserProfile(int index, JInstaProfile profile) {
        if (profile == null)
            return;
        Animation ltr = AnimationUtils.loadAnimation(this, R.anim.left_to_right);
        switch (index) {
            case 1:
                Glide.with(this).load(profile.getProfilePictureUrl()).into(firstImageView);

                firstUserProfile = profile;
                firstFollowersTextView.setText(getString(R.string.followers_newline) + profile.getFollowers());
                firstFollowingTextView.setText(getString(R.string.following_newline) + profile.getFollowing());
                firstUsernameTextView.setText("@" + profile.getUsername());
                firstUser.startAnimation(ltr);
                Gson gson = new Gson();

                break;
            case 2:
                Glide.with(this).load(profile.getProfilePictureUrl()).into(secondImageView);

                secondUserProfile = profile;
                secondFollowersTextView.setText(getString(R.string.followers_newline) + profile.getFollowers());
                secondFollowingTextView.setText(getString(R.string.following_newline) + profile.getFollowing());
                secondUsernameTextView.setText("@" + profile.getUsername());
                secondUser.startAnimation(ltr);
                break;
            case 3:
                Glide.with(this).load(profile.getProfilePictureUrl()).into(thirdImageView);

                thirdUserProfile = profile;
                thirdFollowersTextView.setText(getString(R.string.followers_newline) + profile.getFollowers());
                thirdFollowingTextView.setText(getString(R.string.following_newline) + profile.getFollowing());
                thirdUsernameTextView.setText("@" + profile.getUsername());
                thirdUser.startAnimation(ltr);
                break;
        }
        Gson gson = new Gson();
        String json = gson.toJson(profile);
        sp.edit().putString(getString(R.string.user_key) + "_" + index, json).apply();
    }

    private void resetUser(int index) {
        switch (index) {
            case 1:
                firstFollowersTextView.setText("");
                firstFollowingTextView.setText("");
                firstUsernameTextView.setText(R.string.add_user);
                firstImageView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.default_icon));
                firstUserProfile = null;

                break;
            case 2:
                secondFollowersTextView.setText("");
                secondFollowingTextView.setText("");
                secondUsernameTextView.setText(R.string.add_user);
                secondImageView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.default_icon));
                secondUserProfile = null;

                break;
            case 3:
                thirdFollowersTextView.setText("");
                thirdFollowingTextView.setText("");
                thirdUsernameTextView.setText(R.string.add_user);
                thirdImageView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.default_icon));
                thirdUserProfile = null;

                break;
        }
        sp.edit().remove(getString(R.string.user_key) + "_" + index).apply();
    }

    private void addUser(int index, String username) {
        loaderRunning = true;
        Handler mainHandler = new Handler(Looper.getMainLooper());

        loaderThread = new Thread(() -> {

            try {
                mainHandler.post(() -> {
                    switch (index) {
                        case 1:
                            firstUsernameTextView.setText("Adding");
                            break;
                        case 2:
                            secondUsernameTextView.setText("Adding");
                            break;
                        case 3:

                            thirdUsernameTextView.setText("Adding");
                            break;
                    }
                });
                boolean isAccessible = loader.isAccessible(username);
                if (!isAccessible) {
                    mainHandler.post(() -> {
                        errorUserNotAccessible(index);

                    });

                } else {
                    mainHandler.post(() -> {
                        switch (index) {
                            case 1:
                                firstUsernameTextView.setText("Getting Profile");
                                break;
                            case 2:
                                secondUsernameTextView.setText("Getting Profile");
                                break;
                            case 3:

                                thirdUsernameTextView.setText("Getting Profile");
                                break;
                        }
                    });
                    JInstaProfile profile = loader.getProfileByUsername(username);
                    mainHandler.post(() -> {
                        setUserProfile(index, profile);
                    });
                }

            } catch (JProfileNotExistsException e) {

                mainHandler.post(() -> {
                    errorUsernameNotExists(index);
                });
            } catch (JException e) {
                mainHandler.post(() -> {
                    errorUnexpected(index);
                });
            }
            loaderRunning = false;
        });
        loaderThread.start();
    }

    private void errorUnexpected(int index) {
        resetUser(index);
        Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
        switch (index) {
            case 1:
                firstUser.startAnimation(shake);
                break;
            case 2:
                secondUser.startAnimation(shake);
                break;
            case 3:
                thirdUser.startAnimation(shake);
                break;
        }
        AD = new AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage("Unexpected Error. Check Internet connection or try to log in to Instagram using the App")

                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton(android.R.string.ok, null)

                // A null listener allows the button to dismiss the dialog and take no further action.
                .show();
    }

    private void errorUsernameNotExists(int index) {
        resetUser(index);
        Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
        switch (index) {
            case 1:
                firstUser.startAnimation(shake);
                break;
            case 2:
                secondUser.startAnimation(shake);
                break;
            case 3:
                thirdUser.startAnimation(shake);
                break;
        }
        AD = new AlertDialog.Builder(this)
                .setTitle("Error")
                .setMessage("Username Does not Exist")

                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton(android.R.string.ok, null)

                // A null listener allows the button to dismiss the dialog and take no further action.
                .show();
    }

    private void errorUserNotAccessible(int index) {
        resetUser(index);
        Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
        switch (index) {
            case 1:
                firstUser.startAnimation(shake);
                break;
            case 2:
                secondUser.startAnimation(shake);
                break;
            case 3:
                thirdUser.startAnimation(shake);
                break;
        }
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
    protected void onDestroy() {
        super.onDestroy();
        if (AD != null)
            AD.dismiss();
    }
}