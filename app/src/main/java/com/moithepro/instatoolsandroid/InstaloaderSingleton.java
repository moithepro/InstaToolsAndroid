package com.moithepro.instatoolsandroid;

import android.content.Context;

import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
import com.moithepro.instatoolsandroid.jInstaloader.JInstaloader;

public class InstaloaderSingleton {
    private JInstaloader loader;


    // Getter/setter

    public JInstaloader getLoader() {
        return loader;
    }

    public void setLoader(JInstaloader loader) {
        this.loader = loader;
    }

    private static InstaloaderSingleton instance;

    public static InstaloaderSingleton getInstance() {
        if (instance == null)
            instance = new InstaloaderSingleton();
        return instance;
    }

    private InstaloaderSingleton() { }
}