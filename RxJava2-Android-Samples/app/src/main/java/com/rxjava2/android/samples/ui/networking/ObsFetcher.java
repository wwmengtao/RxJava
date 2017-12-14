package com.rxjava2.android.samples.ui.networking;

import com.rx2androidnetworking.Rx2AndroidNetworking;
import com.rxjava2.android.samples.ALog;
import com.rxjava2.android.samples.model.User;
import com.rxjava2.android.samples.model.UserDetail;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.functions.BiFunction;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by mengtao1 on 2017/12/14.
 */

public class ObsFetcher {
    private static int sleepIndex = 1;
    private static long preTime = -1, nowTime = -1;

    public static void reset(){
        sleepIndex = 1;
        preTime = -1;
        nowTime = -1;
    }
    /**
     * zip Operator Example
     */
    public static Observable<List<User>> getZipFansObservable() {
        preTime = System.currentTimeMillis();
        Observable<List<User>> data =
        Observable.zip(getCricketFansObservable().subscribeOn(Schedulers.newThread()),
                       getFootballFansObservable().subscribeOn(Schedulers.io()),
                new BiFunction<List<User>, List<User>, List<User>>() {
                    @Override
                    public List<User> apply(List<User> cricketFans, List<User> footballFans) throws Exception {
                        List<User> userWhoLovesBoth =
                                filterUserWhoLovesBoth(cricketFans, footballFans);
                        nowTime = System.currentTimeMillis();
                        //通过下列zip耗时粗略统计以及上述两个getxxxFansObservable方法耗时统计可以得知，zip的两个
                        //getxxxFansObservable方法是串行执行的，而非并行。为了提高效率，可以将两者放入新线程执行。
                        ALog.Log("getZipFansObservable cost time: "+(nowTime - preTime));
                        return userWhoLovesBoth;
                    }
                });
        return data;
    }

    private static List<User> filterUserWhoLovesBoth(List<User> cricketFans, List<User> footballFans) {
        List<User> userWhoLovesBoth = new ArrayList<>();
        for (User cricketFan : cricketFans) {
            for (User footballFan : footballFans) {
                if (cricketFan.id == footballFan.id) {
                    userWhoLovesBoth.add(cricketFan);
                }
            }
        }
        return userWhoLovesBoth;
    }

    /**
     * This observable return the list of User who loves cricket
     */
    public static Observable<List<User>> getCricketFansObservable() {
        long preTime, nowTime;
        preTime = System.currentTimeMillis();
        ALog.sleep(sleepIndex++*1000);
        Observable<List<User>> data = Rx2AndroidNetworking.get("https://fierce-cove-29863.herokuapp.com/getAllCricketFans")
                .build()
                .getObjectListObservable(User.class);
        nowTime = System.currentTimeMillis();
        ALog.Log("getCricketFansObservable cost time: "+(nowTime - preTime));
        return data;
    }

    /*
    * This observable return the list of User who loves Football
    */
    public static Observable<List<User>> getFootballFansObservable() {
        long preTime, nowTime;
        preTime = System.currentTimeMillis();
        ALog.sleep(sleepIndex++*1000);
        Observable<List<User>> data = Rx2AndroidNetworking.get("https://fierce-cove-29863.herokuapp.com/getAllFootballFans")
                .build()
                .getObjectListObservable(User.class);
        nowTime = System.currentTimeMillis();
        ALog.Log("getFootballFansObservable cost time: "+(nowTime - preTime));
        return data;
    }

    /**
     * flatMap and filter Operators Example
     */

    public static Observable<List<User>> getAllMyFriendsObservable() {
        return Rx2AndroidNetworking.get("https://fierce-cove-29863.herokuapp.com/getAllFriends/{userId}")
                .addPathParameter("userId", "1")
                .build()
                .getObjectListObservable(User.class);
    }

    public static Observable<UserDetail> getUserDetailObservable(long id) {
        return Rx2AndroidNetworking.get("https://fierce-cove-29863.herokuapp.com/getAnUserDetail/{userId}")
                .addPathParameter("userId", String.valueOf(id))
                .build()
                .getObjectObservable(UserDetail.class);
    }

    /**
     * flatMapWithZip Operator Example
     */

    public static Observable<List<User>> getUserListObservable() {
        return Rx2AndroidNetworking.get("https://fierce-cove-29863.herokuapp.com/getAllUsers/{pageNumber}")
                .addPathParameter("pageNumber", "0")
                .addQueryParameter("limit", "10")
                .build()
                .getObjectListObservable(User.class);
    }
}
