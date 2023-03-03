package com.moithepro.instatoolsandroid.jInstaloader;
// error codes

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;

import java.io.FileNotFoundException;
import java.io.Serializable;
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

    public static final int E_FileNotFoundError = 9;
    private Python py;
    private PyObject instaloaderInterface;
    private String loggedInUsername = null;

    public JInstaloader() {
        py = Python.getInstance();
        instaloaderInterface = py.getModule("interface").get("Interface");
    }

    public String getLoggedInUsername() {
        return loggedInUsername;
    }

    private void ignoreThisMethodItHasNoPracticalUsage(String username, String password) throws JInvalidArgumentException, JConnectionException, JBadCredentialsException, JProfileNotExistsException, JLoginRequiredException, JTwoFactorAuthRequiredException, JPrivateProfileNotFollowedException, JException {
        PyObject res = instaloaderInterface.callAttr("login", instaloaderInterface, username, password);
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
    public void loadSession(String username,String filename) throws FileNotFoundException, JException {
        PyObject res = instaloaderInterface.callAttr("load_session", instaloaderInterface,username, filename);
        int err = res.callAttr("get_e").toInt();
        switch (err) {
            case NO_ERROR:
                loggedInUsername = res.callAttr("get_arg").toString();
                break;
            case E_FileNotFoundError:
                throw new FileNotFoundException();
            case E_Exception:
                throw new JException();
        }
    }
    public void saveSession(String filename) throws JException, JLoginRequiredException {
        PyObject res = instaloaderInterface.callAttr("save_session", instaloaderInterface, filename);
        int err = res.callAttr("get_e").toInt();
        switch (err) {
            case NO_ERROR:
                break;
            case E_LoginRequiredException:
                throw new JLoginRequiredException();
            case E_Exception:
                throw new JException();
        }
    }
    public void login(String username, String password) throws JInvalidArgumentException, JConnectionException, JBadCredentialsException, JTwoFactorAuthRequiredException, JException {

        PyObject res = instaloaderInterface.callAttr("login", instaloaderInterface, username, password);
        int err = res.callAttr("get_e").toInt();
        switch (err) {
            case NO_ERROR:
                loggedInUsername = username;
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
        PyObject res = instaloaderInterface.callAttr("get_followers", instaloaderInterface, username);
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
            profiles.add(new JInstaProfile(obj.get("username").toString(), ""/*obj.get("full_name").toString()*/, -1, -1, -1, "" /*obj.get("profile_pic_url").toString()*/, obj.get("is_verified").toBoolean()));
        }
        return profiles;
    }
    public List<JInstaProfile> getFollowing(String username) throws JProfileNotExistsException, JLoginRequiredException, JPrivateProfileNotFollowedException, JException {
        PyObject res = instaloaderInterface.callAttr("get_following", instaloaderInterface, username);
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
            profiles.add(new JInstaProfile(obj.get("username").toString(), ""/*obj.get("full_name").toString()*/, -1, -1, -1, "" /*obj.get("profile_pic_url").toString()*/, obj.get("is_verified").toBoolean()));
        }
        return profiles;
    }

    public boolean isAccessible(String username) throws JProfileNotExistsException, JException {
        PyObject res = instaloaderInterface.callAttr("is_accessible", instaloaderInterface, username);
        int err = res.callAttr("get_e").toInt();
        switch (err) {
            case NO_ERROR:
                break;
            case E_ProfileNotExistsException:
                throw new JProfileNotExistsException();
            case E_Exception:
                throw new JException();
        }
        return res.callAttr("get_arg").toBoolean();
    }
    public JInstaProfile getProfileByUsername(String username) throws JProfileNotExistsException, JException {
        PyObject res = instaloaderInterface.callAttr("get_profile_from_username", instaloaderInterface, username);
        int err = res.callAttr("get_e").toInt();
        switch (err) {
            case NO_ERROR:
                break;
            case E_ProfileNotExistsException:
                throw new JProfileNotExistsException();
            case E_Exception:
                throw new JException();
        }
        PyObject obj = res.callAttr("get_arg");
        return new JInstaProfile(obj.get("username").toString(), ""/*obj.get("full_name").toString()*/, obj.get("userid").toLong(), obj.get("followers").toLong(), obj.get("followees").toLong(), obj.get("profile_pic_url").toString(), obj.get("is_verified").toBoolean());
    }
    public JInstaProfile getProfileById(long id) throws JProfileNotExistsException, JException {
        PyObject res = instaloaderInterface.callAttr("get_profile_from_id", instaloaderInterface, id);
        int err = res.callAttr("get_e").toInt();
        switch (err) {
            case NO_ERROR:
                break;
            case E_ProfileNotExistsException:
                throw new JProfileNotExistsException();
            case E_Exception:
                throw new JException();
        }
        PyObject obj = res.callAttr("get_arg");
        return new JInstaProfile(obj.get("username").toString(), ""/*obj.get("full_name").toString()*/, obj.get("userid").toLong(), obj.get("followers").toLong(), obj.get("followees").toLong(), obj.get("profile_pic_url").toString(), obj.get("is_verified").toBoolean());
    }

}
