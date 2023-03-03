import instaloader
import os
from os.path import join, dirname
from com.chaquo.python import Python
from instaloader import ConnectionException, InvalidArgumentException, BadCredentialsException, \
    ProfileNotExistsException, LoginRequiredException, Instaloader, Profile, \
    TwoFactorAuthRequiredException, \
    PrivateProfileNotFollowedException

# error codes
NO_ERROR = 0
E_InvalidArgumentException = 1
E_ConnectionException = 2
E_BadCredentialsException = 3
E_ProfileNotExistsException = 4
E_LoginRequiredException = 5
E_TwoFactorAuthRequiredException = 6
E_PrivateProfileNotFollowedException = 7
E_Exception = 8
E_FileNotFoundError = 9

class Result:

    def __init__(self, e: int, arg):
        self.e = e
        self.arg = arg

    def get_e(self):
        return self.e

    def get_arg(self):
        return self.arg


class Interface:
    def __init__(self):
        self.L: Instaloader = None
    def load_session(self,username, session):
        try:
            print("load")
            self.L = instaloader.Instaloader()
            #files_dir = str(Python.getPlatform().getApplication().getFilesDir())
            #self.L.load_session_from_file(join(dirname(__file__),session))
            self.L.load_session_from_file(username,filename=join(os.environ["HOME"],session))
            return Result(NO_ERROR, Profile.own_profile(self.L.context).username)
        except FileNotFoundError as err:
            print(err)
            return Result(E_FileNotFoundError, 0)
        #except Exception as ex:
        #    print(ex)
        #    return Result(E_Exception, 0)
    def save_session(self, session):
        try:
            print("save")
            #files_dir = str(Python.getPlatform().getApplication().getFilesDir())
            self.L.save_session_to_file(join(os.environ["HOME"],session))
            return Result(NO_ERROR, 0)
        except LoginRequiredException as err:
            print(err)
            return Result(E_LoginRequiredException, 0)
        #except Exception as ex:
        #    print(ex)
        #    return Result(E_Exception, 0)
    def login(self, username, password):
        try:
            print("login")
            self.L = instaloader.Instaloader()
            if not (username == "" and password == ""):
                self.L.login(username, password)
            return Result(NO_ERROR, 0)
        except InvalidArgumentException as err:
            return Result(E_InvalidArgumentException, 0)

        except ConnectionException as err:
            print(err)
            return Result(E_ConnectionException, 0)

        except BadCredentialsException as err:
            return Result(E_BadCredentialsException, 0)
        except TwoFactorAuthRequiredException as err:
            return Result(E_TwoFactorAuthRequiredException, 0)
        except Exception as ex:
            print(ex)
            return Result(E_Exception, 0)

    def close(self):
        self.L.close()

    def get_profile_from_username(self, username):
        try:
            profile: Profile = instaloader.Profile.from_username(self.L.context, username)
            return Result(NO_ERROR, profile)
        except ProfileNotExistsException as err:
            return Result(E_ProfileNotExistsException, 0)
        except Exception as ex:
            print(ex)
            return Result(E_Exception, 0)

    def get_profile_from_id(self, id):
        try:
            profile: Profile = instaloader.Profile.from_id(self.L.context, id)
            return Result(NO_ERROR, profile)
        except ProfileNotExistsException as err:
            return Result(E_ProfileNotExistsException, 0)
        except Exception as ex:
            print(ex)
            return Result(E_Exception, 0)

    def get_followers(self, username):
        try:
            print("get")
            profile: Profile = instaloader.Profile.from_username(self.L.context, username)
            print("getting")
            return Result(NO_ERROR, list(profile.get_followers()))
        except ProfileNotExistsException as err:
            return Result(E_ProfileNotExistsException, 0)
        except LoginRequiredException as err:
            return Result(E_LoginRequiredException, 0)
        except PrivateProfileNotFollowedException as err:
            return Result(E_PrivateProfileNotFollowedException, 0)
        except Exception as ex:
            print(ex)
            return Result(E_Exception, 0)

    def get_following(self, username):
        try:
            profile: Profile = instaloader.Profile.from_username(self.L.context, username)
            return Result(NO_ERROR, list(profile.get_followees()))
        except ProfileNotExistsException as err:
            return Result(E_ProfileNotExistsException, 0)
        except LoginRequiredException as err:
            return Result(E_LoginRequiredException, 0)
        except PrivateProfileNotFollowedException as err:
            return Result(E_PrivateProfileNotFollowedException, 0)
        except Exception as ex:
            print(ex)
            return Result(E_Exception, 0)

    def is_accessible(self, username):
        try:
            profile: Profile = instaloader.Profile.from_username(self.L.context, username)
            return Result(NO_ERROR, not profile.is_private or profile.followed_by_viewer)
        except ProfileNotExistsException as err:
            return Result(E_ProfileNotExistsException, 0)
        except Exception as ex:
            print(ex)
            return Result(E_Exception, 0)

    def id_get_profile(self, id):
        try:
            return Result(NO_ERROR, instaloader.Profile.from_id(self.L.context, int(id)))
        except ProfileNotExistsException as err:
            return Result(E_ProfileNotExistsException, 0)
        except Exception as ex:
            print(ex)
            return Result(E_Exception, 0)

    def username_get_profile(self, username):
        try:
            return Result(NO_ERROR, instaloader.Profile.from_username(self.L.context, username))
        except ProfileNotExistsException as err:
            return Result(E_ProfileNotExistsException, 0)
        except Exception as ex:
            print(ex)
            return Result(E_Exception, 0)
