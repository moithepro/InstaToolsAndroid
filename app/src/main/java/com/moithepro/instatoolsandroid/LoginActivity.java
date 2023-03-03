package com.moithepro.instatoolsandroid;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
import com.google.android.material.button.MaterialButton;
import com.moithepro.instatoolsandroid.jInstaloader.JBadCredentialsException;
import com.moithepro.instatoolsandroid.jInstaloader.JConnectionException;
import com.moithepro.instatoolsandroid.jInstaloader.JException;
import com.moithepro.instatoolsandroid.jInstaloader.JInstaloader;
import com.moithepro.instatoolsandroid.jInstaloader.JInvalidArgumentException;
import com.moithepro.instatoolsandroid.jInstaloader.JTwoFactorAuthRequiredException;

import java.io.File;
import java.io.FileNotFoundException;

public class LoginActivity extends AppCompatActivity {
    private EditText password;
    private EditText username;
    private TextView forgot;
    private MaterialButton login;
    private AlertDialog AD;
    private Thread loginThread;
    private JInstaloader loader;
    private boolean loggingIn = false;
    private TextView guest;
    private SharedPreferences sp;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        if (!Python.isStarted())
            Python.start(new AndroidPlatform(this));
        InstaloaderSingleton.getInstance().setLoader(new JInstaloader());
        loader = InstaloaderSingleton.getInstance().getLoader();
        sp = getSharedPreferences(getString(R.string.login_key), MODE_PRIVATE);
        password = findViewById(R.id.password);
        password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        username = findViewById(R.id.username);
        String spu = sp.getString(getString(R.string.username_key), null);
        String spp = sp.getString(getString(R.string.password_key), null);

        login = findViewById(R.id.login);
        login.setOnClickListener((view -> {
            if (!loggingIn) {
                if (password.getText().toString().length() > 0 && username.getText().toString().length() > 0) {
                    loggingIn = true;
                    password.setEnabled(false);
                    username.setEnabled(false);
                    login.setText(R.string.logging_in);

                    Handler mainHandler = new Handler(Looper.getMainLooper());
                    loginThread = new Thread(() -> {


                        try {
                            String u = username.getText().toString().trim();
                            String p = password.getText().toString().trim();
                            loader.login(u, p);
                            File file = new File(this.getFilesDir(), "session");
                            try {

                                //loader.saveSession(file.getCanonicalPath());
                                loader.saveSession("session");

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            mainHandler.post(() -> {
                                sp.edit().putString(getString(R.string.username_key), u).putString(getString(R.string.password_key), p).apply();

                                Intent intent = new Intent(this, UsersActivity.class);
                                startActivity(intent);
                                loggingIn = false;
                                password.setEnabled(true);
                                username.setEnabled(true);
                                login.setText(R.string.log_in);
                                finish();
                                //setContentView(R.layout.users_activity);

                            });
                        } catch (JInvalidArgumentException e) {
                            mainHandler.post(() -> {
                                AD = new AlertDialog.Builder(this)
                                        .setTitle("Error")
                                        .setMessage("Username Does not Exist")

                                        // Specifying a listener allows you to take an action before dismissing the dialog.
                                        // The dialog is automatically dismissed when a dialog button is clicked.
                                        .setPositiveButton(android.R.string.ok, null)

                                        // A null listener allows the button to dismiss the dialog and take no further action.
                                        .show();
                                loggingIn = false;
                                password.setEnabled(true);
                                username.setEnabled(true);
                                login.setText(R.string.log_in);
                            });
                        } catch (JConnectionException e) {
                            mainHandler.post(() -> {
                                AD = new AlertDialog.Builder(this)
                                        .setTitle("Error")
                                        .setMessage("Connection Failed")

                                        // Specifying a listener allows you to take an action before dismissing the dialog.
                                        // The dialog is automatically dismissed when a dialog button is clicked.
                                        .setPositiveButton(android.R.string.ok, null)

                                        // A null listener allows the button to dismiss the dialog and take no further action.
                                        .show();
                                loggingIn = false;
                                password.setEnabled(true);
                                username.setEnabled(true);
                                login.setText(R.string.log_in);
                            });

                        } catch (JBadCredentialsException e) {
                            mainHandler.post(() -> {
                                AD = new AlertDialog.Builder(this)
                                        .setTitle("Error")
                                        .setMessage("Password is Wrong")

                                        // Specifying a listener allows you to take an action before dismissing the dialog.
                                        // The dialog is automatically dismissed when a dialog button is clicked.
                                        .setPositiveButton(android.R.string.ok, null)

                                        // A null listener allows the button to dismiss the dialog and take no further action.
                                        .show();
                                loggingIn = false;
                                password.setEnabled(true);
                                username.setEnabled(true);
                                login.setText(R.string.log_in);
                            });
                        } catch (JTwoFactorAuthRequiredException e) {
                            mainHandler.post(() -> {
                                AD = new AlertDialog.Builder(this)
                                        .setTitle("Error")
                                        .setMessage("Two Factor Authentication is required but is not supported right now. you can try logging in with another account.")

                                        // Specifying a listener allows you to take an action before dismissing the dialog.
                                        // The dialog is automatically dismissed when a dialog button is clicked.
                                        .setPositiveButton(android.R.string.ok, null)

                                        // A null listener allows the button to dismiss the dialog and take no further action.
                                        .show();
                                loggingIn = false;
                                password.setEnabled(true);
                                username.setEnabled(true);
                                login.setText(R.string.log_in);
                            });
                        } catch (JException e) {
                            mainHandler.post(() -> {
                                AD = new AlertDialog.Builder(this)
                                        .setTitle("Error")
                                        .setMessage("Unknown Error")

                                        // Specifying a listener allows you to take an action before dismissing the dialog.
                                        // The dialog is automatically dismissed when a dialog button is clicked.
                                        .setPositiveButton(android.R.string.ok, null)

                                        // A null listener allows the button to dismiss the dialog and take no further action.
                                        .show();
                                loggingIn = false;
                                password.setEnabled(true);
                                username.setEnabled(true);
                                login.setText(R.string.log_in);
                            });
                        }
                    });
                    loginThread.start();
                } else {
                    AD = new AlertDialog.Builder(this)
                            .setTitle("Error")
                            .setMessage("Username or Password is empty.")

                            // Specifying a listener allows you to take an action before dismissing the dialog.
                            // The dialog is automatically dismissed when a dialog button is clicked.
                            .setPositiveButton(android.R.string.ok, null)

                            // A null listener allows the button to dismiss the dialog and take no further action.
                            .show();
                    loggingIn = false;
                    password.setEnabled(true);
                    username.setEnabled(true);
                    login.setText(R.string.log_in);
                }
            }
        }));
        guest = findViewById(R.id.log_in_guest);
        guest.setOnClickListener(view -> {
            if (!loggingIn) {

                loggingIn = true;
                password.setEnabled(false);
                username.setEnabled(false);
                login.setText(R.string.logging_in);

                Handler mainHandler = new Handler(Looper.getMainLooper());
                loginThread = new Thread(() -> {


                    try {
                        loader.login("", "");

                        mainHandler.post(() -> {
                            sp.edit().putString(getString(R.string.username_key), "").putString(getString(R.string.password_key), "").apply();
                            Intent intent = new Intent(this, UsersActivity.class);
                            startActivity(intent);
                            loggingIn = false;
                            password.setEnabled(true);
                            username.setEnabled(true);
                            login.setText(R.string.log_in);
                            finish();
                            //setContentView(R.layout.users_activity);

                        });
                    } catch (JInvalidArgumentException e) {
                        mainHandler.post(() -> {
                            AD = new AlertDialog.Builder(this)
                                    .setTitle("Error")
                                    .setMessage("Username Does not Exist")

                                    // Specifying a listener allows you to take an action before dismissing the dialog.
                                    // The dialog is automatically dismissed when a dialog button is clicked.
                                    .setPositiveButton(android.R.string.ok, null)

                                    // A null listener allows the button to dismiss the dialog and take no further action.
                                    .show();
                            loggingIn = false;
                            password.setEnabled(true);
                            username.setEnabled(true);
                            login.setText(R.string.log_in);
                        });
                    } catch (JConnectionException e) {
                        mainHandler.post(() -> {
                            AD = new AlertDialog.Builder(this)
                                    .setTitle("Error")
                                    .setMessage("Connection Failed")

                                    // Specifying a listener allows you to take an action before dismissing the dialog.
                                    // The dialog is automatically dismissed when a dialog button is clicked.
                                    .setPositiveButton(android.R.string.ok, null)

                                    // A null listener allows the button to dismiss the dialog and take no further action.
                                    .show();
                            loggingIn = false;
                            password.setEnabled(true);
                            username.setEnabled(true);
                            login.setText(R.string.log_in);
                        });

                    } catch (JBadCredentialsException e) {
                        mainHandler.post(() -> {
                            AD = new AlertDialog.Builder(this)
                                    .setTitle("Error")
                                    .setMessage("Password is Wrong")

                                    // Specifying a listener allows you to take an action before dismissing the dialog.
                                    // The dialog is automatically dismissed when a dialog button is clicked.
                                    .setPositiveButton(android.R.string.ok, null)

                                    // A null listener allows the button to dismiss the dialog and take no further action.
                                    .show();
                            loggingIn = false;
                            password.setEnabled(true);
                            username.setEnabled(true);
                            login.setText(R.string.log_in);
                        });
                    } catch (JTwoFactorAuthRequiredException e) {
                        mainHandler.post(() -> {
                            AD = new AlertDialog.Builder(this)
                                    .setTitle("Error")
                                    .setMessage("Two Factor Authentication is required but is not supported right now. you can try logging in with another account.")

                                    // Specifying a listener allows you to take an action before dismissing the dialog.
                                    // The dialog is automatically dismissed when a dialog button is clicked.
                                    .setPositiveButton(android.R.string.ok, null)

                                    // A null listener allows the button to dismiss the dialog and take no further action.
                                    .show();
                            loggingIn = false;
                            password.setEnabled(true);
                            username.setEnabled(true);
                            login.setText(R.string.log_in);
                        });
                    } catch (JException e) {
                        mainHandler.post(() -> {
                            AD = new AlertDialog.Builder(this)
                                    .setTitle("Error")
                                    .setMessage("Unknown Error")

                                    // Specifying a listener allows you to take an action before dismissing the dialog.
                                    // The dialog is automatically dismissed when a dialog button is clicked.
                                    .setPositiveButton(android.R.string.ok, null)

                                    // A null listener allows the button to dismiss the dialog and take no further action.
                                    .show();
                            loggingIn = false;
                            password.setEnabled(true);
                            username.setEnabled(true);
                            login.setText(R.string.log_in);
                        });
                    }
                });
                loginThread.start();
            }
        });
        forgot = findViewById(R.id.forgot);
        forgot.setOnClickListener((view -> {
            if (!loggingIn) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/accounts/password/reset"));
                startActivity(browserIntent);
            }
        }));
        if (spu != null && spp != null) {
            if (!loggingIn) {

                loggingIn = true;
                password.setEnabled(false);
                username.setEnabled(false);
                login.setText(R.string.logging_in);

                Handler mainHandler = new Handler(Looper.getMainLooper());
                loginThread = new Thread(() -> {
                    File file = new File(this.getFilesDir(), "session");
                    try {

                        //loader.loadSession(file.getCanonicalPath());
                        loader.loadSession(spu,"session");
                        mainHandler.post(() -> {
                            Intent intent = new Intent(this, UsersActivity.class);
                            startActivity(intent);
                            loggingIn = false;
                            password.setEnabled(true);
                            username.setEnabled(true);
                            Toast.makeText(this, "Logged in as @" + loader.getLoggedInUsername(), Toast.LENGTH_SHORT).show();
                            login.setText(R.string.log_in);
                            finish();
                            //setContentView(R.layout.users_activity);

                        });
                    } catch (FileNotFoundException er) {
                        mainHandler.post(() -> {
                            AD = new AlertDialog.Builder(this)
                                    .setTitle("Error")
                                    .setMessage("Session File Not Found")

                                    // Specifying a listener allows you to take an action before dismissing the dialog.
                                    // The dialog is automatically dismissed when a dialog button is clicked.
                                    .setPositiveButton(android.R.string.ok, null)

                                    // A null listener allows the button to dismiss the dialog and take no further action.
                                    .show();

                        });
                        login(spu, spp);
                    } catch (Exception e) {
                        e.printStackTrace();
                        login(spu, spp);
                    }

                });
                loginThread.start();
            }
        }
        password.setOnTouchListener((v, event) -> {
            final int DRAWABLE_LEFT = 0;
            final int DRAWABLE_TOP = 1;
            final int DRAWABLE_RIGHT = 2;
            final int DRAWABLE_BOTTOM = 3;

            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (event.getRawX() >= (password.getRight() - password.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                    if (password.getInputType() == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD)
                        password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    else
                        password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    password.setSelection(password.length());

                    return true;
                }
            }
            return false;
        });

    }
    private void login(String u, String p) {
        Handler mainHandler = new Handler(Looper.getMainLooper());
        try {
            loader.login(u, p);

            try {
                //loader.saveSession(file.getCanonicalPath());
                loader.saveSession("session");
            } catch (Exception e) {
                e.printStackTrace();
            }


            mainHandler.post(() -> {
                Intent intent = new Intent(this, UsersActivity.class);
                startActivity(intent);
                loggingIn = false;
                password.setEnabled(true);
                username.setEnabled(true);
                login.setText(R.string.log_in);
                finish();
                //setContentView(R.layout.users_activity);

            });
        } catch (JInvalidArgumentException e) {
            mainHandler.post(() -> {
                AD = new AlertDialog.Builder(this)
                        .setTitle("Error")
                        .setMessage("Username Does not Exist")

                        // Specifying a listener allows you to take an action before dismissing the dialog.
                        // The dialog is automatically dismissed when a dialog button is clicked.
                        .setPositiveButton(android.R.string.ok, null)

                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .show();
                loggingIn = false;
                password.setEnabled(true);
                username.setEnabled(true);
                login.setText(R.string.log_in);
            });
        } catch (JConnectionException e) {
            mainHandler.post(() -> {
                AD = new AlertDialog.Builder(this)
                        .setTitle("Error")
                        .setMessage("Connection Failed")

                        // Specifying a listener allows you to take an action before dismissing the dialog.
                        // The dialog is automatically dismissed when a dialog button is clicked.
                        .setPositiveButton(android.R.string.ok, null)

                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .show();
                loggingIn = false;
                password.setEnabled(true);
                username.setEnabled(true);
                login.setText(R.string.log_in);
            });

        } catch (JBadCredentialsException e) {
            mainHandler.post(() -> {
                AD = new AlertDialog.Builder(this)
                        .setTitle("Error")
                        .setMessage("Password is Wrong")

                        // Specifying a listener allows you to take an action before dismissing the dialog.
                        // The dialog is automatically dismissed when a dialog button is clicked.
                        .setPositiveButton(android.R.string.ok, null)

                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .show();
                loggingIn = false;
                password.setEnabled(true);
                username.setEnabled(true);
                login.setText(R.string.log_in);
            });
        } catch (JTwoFactorAuthRequiredException e) {
            mainHandler.post(() -> {
                AD = new AlertDialog.Builder(this)
                        .setTitle("Error")
                        .setMessage("Two Factor Authentication is required but is not supported right now. you can try logging in with another account.")

                        // Specifying a listener allows you to take an action before dismissing the dialog.
                        // The dialog is automatically dismissed when a dialog button is clicked.
                        .setPositiveButton(android.R.string.ok, null)

                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .show();
                loggingIn = false;
                password.setEnabled(true);
                username.setEnabled(true);
                login.setText(R.string.log_in);
            });
        } catch (JException e) {
            mainHandler.post(() -> {
                AD = new AlertDialog.Builder(this)
                        .setTitle("Error")
                        .setMessage("Unknown Error")

                        // Specifying a listener allows you to take an action before dismissing the dialog.
                        // The dialog is automatically dismissed when a dialog button is clicked.
                        .setPositiveButton(android.R.string.ok, null)

                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .show();
                loggingIn = false;
                password.setEnabled(true);
                username.setEnabled(true);
                login.setText(R.string.log_in);
            });
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (AD != null)
            AD.dismiss();
    }
}