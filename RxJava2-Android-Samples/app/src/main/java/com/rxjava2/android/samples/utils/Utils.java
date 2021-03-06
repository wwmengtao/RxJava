package com.rxjava2.android.samples.utils;

import com.androidnetworking.error.ANError;
import com.rxjava2.android.samples.ALog;
import com.rxjava2.android.samples.model.ApiUser;
import com.rxjava2.android.samples.model.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by amitshekhar on 27/08/16.
 */
public class Utils {

    private Utils() {
        // This class in not publicly instantiable.
    }

    public static List<User> getUserList() {

        List<User> userList = new ArrayList<>();

        User userOne = new User();
        userOne.firstname = "Amit";
        userOne.lastname = "Shekhar";
        userList.add(userOne);

        User userTwo = new User();
        userTwo.firstname = "Manish";
        userTwo.lastname = "Kumar";
        userList.add(userTwo);

        User userThree = new User();
        userThree.firstname = "Sumit";
        userThree.lastname = "Kumar";
        userList.add(userThree);

        return userList;
    }

    public static List<ApiUser> getApiUserList() {

        List<ApiUser> apiUserList = new ArrayList<>();

        ApiUser apiUserOne = new ApiUser();
        apiUserOne.firstname = "Amit";
        apiUserOne.lastname = "Shekhar";
        apiUserList.add(apiUserOne);

        ApiUser apiUserTwo = new ApiUser();
        apiUserTwo.firstname = "Manish";
        apiUserTwo.lastname = "Kumar";
        apiUserList.add(apiUserTwo);

        ApiUser apiUserThree = new ApiUser();
        apiUserThree.firstname = "Sumit";
        apiUserThree.lastname = "Kumar";
        apiUserList.add(apiUserThree);

        return apiUserList;
    }


    public static List<User> convertApiUserListToUserList(List<ApiUser> apiUserList) {

        List<User> userList = new ArrayList<>();

        for (ApiUser apiUser : apiUserList) {
            User user = new User();
            user.firstname = apiUser.firstname;
            user.lastname = apiUser.lastname;
            userList.add(user);
        }

        return userList;
    }

    public static List<User> getUserListWhoLovesCricket() {
        long time1, time2;
        time1 = System.currentTimeMillis();
        //
        List<User> userList = new ArrayList<>();

        User userOne = new User();
        userOne.id = 1;
        userOne.firstname = "Amit";
        userOne.lastname = "Shekhar";
        userList.add(userOne);

        User userTwo = new User();
        userTwo.id = 2;
        userTwo.firstname = "Manish";
        userTwo.lastname = "Kumar";
        userList.add(userTwo);
        //
        ALog.sleep(2000);//模拟耗时操作
        time2 = System.currentTimeMillis();
        ALog.Log("getUserListWhoLovesCricket cost time: "+(time2 - time1)+" "+Thread.currentThread().getName());
        return userList;
    }


    public static List<User> getUserListWhoLovesFootball() {
        long time1, time2;
        time1 = System.currentTimeMillis();
        //
        List<User> userList = new ArrayList<>();

        User userOne = new User();
        userOne.id = 1;
        userOne.firstname = "Amit";
        userOne.lastname = "Shekhar";
        userList.add(userOne);

        User userTwo = new User();
        userTwo.id = 3;
        userTwo.firstname = "Sumit";
        userTwo.lastname = "Kumar";
        userList.add(userTwo);
        //
        ALog.sleep(4000);//模拟耗时操作
        time2 = System.currentTimeMillis();
        ALog.Log("getUserListWhoLovesFootball cost time: "+(time2 - time1)+" "+Thread.currentThread().getName());
        return userList;
    }


    public static List<User> filterUserWhoLovesBoth(List<User> cricketFans, List<User> footballFans) {
        List<User> userWhoLovesBoth = new ArrayList<User>();
        for (User cricketFan : cricketFans) {
            for (User footballFan : footballFans) {
                if (cricketFan.id == footballFan.id) {
                    cricketFan.firstname += "love cricket";
                    footballFan.firstname += "love football";
                    userWhoLovesBoth.add(cricketFan);
                    userWhoLovesBoth.add(footballFan);
                }
            }
        }
        return userWhoLovesBoth;
    }

    public static void logError(String TAG, Throwable e) {
        if (e instanceof ANError) {
            ANError anError = (ANError) e;
            if (anError.getErrorCode() != 0) {
                // received ANError from server
                // error.getErrorCode() - the ANError code from server
                // error.getErrorBody() - the ANError body from server
                // error.getErrorDetail() - just a ANError detail
                ALog.Log(TAG+ "onError errorCode : " + anError.getErrorCode());
                ALog.Log(TAG+ "onError errorBody : " + anError.getErrorBody());
                ALog.Log(TAG+ "onError errorDetail : " + anError.getErrorDetail());
            } else {
                // error.getErrorDetail() : connectionError, parseError, requestCancelledError
                ALog.Log(TAG+ "onError errorDetail : " + anError.getErrorDetail());
            }
        } else {
            ALog.Log(TAG+ "onError errorMessage : " + e.getMessage());
        }
    }

}
