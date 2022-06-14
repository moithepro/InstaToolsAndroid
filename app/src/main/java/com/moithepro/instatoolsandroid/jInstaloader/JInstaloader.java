package com.moithepro.instatoolsandroid.jInstaloader;
// error codes

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;

import java.util.ArrayList;
import java.util.List;

public class JInstaloader {
    public static final int NO_ERROR = 0;
    public static final int E_InvalidArgumentException = 1;
    public static final int E_ConnectionException = 2;
    public static final int E_BadCredentialsException = 3;
    public static final int E_ProfileNotExistsException = 4;
    public static final int E_LoginRequiredException = 5;
    public static final int E_TwoFactorAuthRequiredException = 6;
    public static final int E_PrivateProfileNotFollowedException = 7;
    public static final int E_Exception = 8;
    private Python py;
    private PyObject instaloaderInterface;

    public JInstaloader() {
        py = Python.getInstance();
        instaloaderInterface = py.getModule("interface").get("Interface");
    }

    private void ignoreThisMethodItHasNoPracticalUsage(String username, String password) throws JInvalidArgumentException, JConnectionException, JBadCredentialsException, JProfileNotExistsException, JLoginRequiredException, JTwoFactorAuthRequiredException, JPrivateProfileNotFollowedException, JException {
        PyObject res = instaloaderInterface.callAttr("login",instaloaderInterface, username, password);
        int err = res.callAttr("get_e").toInt();
        switch (err) {
            case NO_ERROR:
                break;
            case E_InvalidArgumentException:
                throw new JInvalidArgumentException();
            case E_ConnectionException:
                throw new JConnectionException();
            case E_BadCredentialsException:
                throw new JBadCredentialsException();
            case E_ProfileNotExistsException:
                throw new JProfileNotExistsException();
            case E_LoginRequiredException:
                throw new JLoginRequiredException();
            case E_TwoFactorAuthRequiredException:
                throw new JTwoFactorAuthRequiredException();
            case E_PrivateProfileNotFollowedException:
                throw new JPrivateProfileNotFollowedException();
            case E_Exception:
                throw new JException();
        }
    }

    public void login(String username, String password) throws JInvalidArgumentException, JConnectionException, JBadCredentialsException, JTwoFactorAuthRequiredException, JException {

        PyObject res = instaloaderInterface.callAttr("login",instaloaderInterface, username, password);
        int err = res.callAttr("get_e").toInt();
        switch (err) {
            case NO_ERROR:
                break;
            case E_InvalidArgumentException:
                throw new JInvalidArgumentException();
            case E_ConnectionException:
                throw new JConnectionException();
            case E_BadCredentialsException:
                throw new JBadCredentialsException();
            case E_TwoFactorAuthRequiredException:
                throw new JTwoFactorAuthRequiredException();
            case E_Exception:
                throw new JException();
        }
    }

    public List<JInstaProfile> getFollowers(String username) throws JProfileNotExistsException, JLoginRequiredException, JPrivateProfileNotFollowedException, JException {
        PyObject res = instaloaderInterface.callAttr("get_followers",instaloaderInterface, username);
        int err = res.callAttr("get_e").toInt();
        switch (err) {
            case NO_ERROR:
                break;
            case E_ProfileNotExistsException:
                throw new JProfileNotExistsException();
            case E_LoginRequiredException:
                throw new JLoginRequiredException();
            case E_PrivateProfileNotFollowedException:
                throw new JPrivateProfileNotFollowedException();
            case E_Exception:
                throw new JException();
        }
        List<PyObject> p = res.callAttr("get_arg").asList();
        List<JInstaProfile> profiles = new ArrayList<>();
        for (PyObject obj : p) {
            profiles.add(new JInstaProfile(obj.get("username").toString(), ""/*obj.get("full_name").toString()*/, "", -1, -1,"" /*obj.get("profile_pic_url").toString()*/, obj.get("is_verified").toBoolean()));
        }
        return profiles;
    }
}
