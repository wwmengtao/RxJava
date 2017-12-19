package com.rxjava2.android.samples.utils;

import com.rx2androidnetworking.Rx2AndroidNetworking;
import com.rxjava2.android.samples.ALog;
import com.rxjava2.android.samples.model.User;
import com.rxjava2.android.samples.model.UserDetail;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by mengtao1 on 2017/12/14.
 */

public class ObsFetcher {
    private static final String TAG = "ObsFetcher ";
    private static int sleepIndex = 1;
    private static long preTime = -1, nowTime = -1;

    public static void reset(){
        sleepIndex = 1;
        preTime = -1;
        nowTime = -1;
    }

    /**
     * getZipFansObservable：通过下列getZipFansObservable耗时粗略统计两个getxxxFansObservable方法耗时统计可以得知，zip的两个
     * getxxxFansObservable方法是串行执行的，而非并行。就算将两者放入新线程执行也是如此，这或许就是Rx2AndroidNetworking的运行特点。
     * 注意此时的zip耗时和ZipExampleActivity中的zip耗时区别，后者zip中两个getxxxFansObservable方法是真正的并行。
     * @return
     */

    public static Observable<List<User>> getZipFansObservable() {
        ALog.Log("/************************"+TAG+"************************/\n");
        ALog.Log(TAG+"getZipFansObservable");
        preTime = System.currentTimeMillis();
        Observable<List<User>> data =
            Observable.zip(getCricketFansObs().subscribeOn(Schedulers.newThread()),
                       getFootballFansObs().subscribeOn(Schedulers.io()),
                new BiFunction<List<User>, List<User>, List<User>>() {
                    @Override
                    public List<User> apply(List<User> cricketFans, List<User> footballFans) throws Exception {
                        List<User> userWhoLovesBoth =
                                filterUserWhoLovesBoth(cricketFans, footballFans);
                        nowTime = System.currentTimeMillis();
                        ALog.Log(TAG+"getZipFansObservable cost time: "+(nowTime - preTime));
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
    public static Observable<List<User>> getCricketFansObs() {
        long preTime, nowTime;
        preTime = System.currentTimeMillis();
        ALog.sleep(sleepIndex++*1000);
        Observable<List<User>> data = Rx2AndroidNetworking.get("https://fierce-cove-29863.herokuapp.com/getAllCricketFans")
                .build()
                .getObjectListObservable(User.class);
        nowTime = System.currentTimeMillis();
        ALog.Log(TAG+"getCricketFansObs cost time: "+(nowTime - preTime));
        return data;
    }

    /*
    * This observable return the list of User who loves Football
    */
    public static Observable<List<User>> getFootballFansObs() {
        long preTime, nowTime;
        preTime = System.currentTimeMillis();
        ALog.sleep(sleepIndex++*1000);
        Observable<List<User>> data = Rx2AndroidNetworking.get("https://fierce-cove-29863.herokuapp.com/getAllFootballFans")
                .build()
                .getObjectListObservable(User.class);
        nowTime = System.currentTimeMillis();
        ALog.Log(TAG+"getFootballFansObs cost time: "+(nowTime - preTime));
        return data;
    }

    /**
     * flatMap and filter Operators Example
     */

    public static Observable<List<User>> getAllMyFriendsObs() {
        return Rx2AndroidNetworking.get("https://fierce-cove-29863.herokuapp.com/getAllFriends/{userId}")
                .addPathParameter("userId", "1")
                .build()
                .getObjectListObservable(User.class);
    }

    public static Observable<UserDetail> getUserDetailObs(long id) {
        return Rx2AndroidNetworking.get("https://fierce-cove-29863.herokuapp.com/getAnUserDetail/{userId}")
                .addPathParameter("userId", String.valueOf(id))
                .build()
                .getObjectObservable(UserDetail.class);
    }

    /**
     * flatMapWithZip Operator Example
     */

    public static Observable<List<User>> getUserListObs() {
        return Rx2AndroidNetworking.get("https://fierce-cove-29863.herokuapp.com/getAllUsers/{pageNumber}")
                .addPathParameter("pageNumber", "0")
                .addQueryParameter("limit", "10")
                .build()
                .getObjectListObservable(User.class);
    }

    public static Observable<Long> getIntervalMapObs() {
        return Observable.interval(0, 2, TimeUnit.SECONDS)
                .map(new Function<Long,Long>() {
                    @Override
                    public Long apply(Long l) throws Exception {
                        ALog.sleep(sleepIndex++*1000);
                        nowTime = System.currentTimeMillis();
                        //下列log信息说明RX的interval配合map可以实现上一个任务执行完毕之后再间隔一定时间执行下一个任务
                        if(0!=preTime)ALog.Log("Observable.interval: "+(nowTime - preTime)/1000);
                        preTime = System.currentTimeMillis();
                        return l;
                    }
                });
    }

    //getIntervalMapFlatMapObs：实现定时获取zip混合数据的效果，注意map和flatMap的区别
    public static Observable<List<User>> getIntervalMapFlatMapObs() {
        return Observable.interval(0, 2, TimeUnit.SECONDS)
                .map(new Function<Long, Observable<List<User>>>() {
                    @Override
                    public Observable<List<User>> apply(Long l) throws Exception {
                        ALog.sleep(sleepIndex++*1000);
                        nowTime = System.currentTimeMillis();
                        //下列log信息说明RX的interval配合map可以实现上一个任务执行完毕之后再间隔一定时间执行下一个任务
                        if(0!=preTime)ALog.Log("Observable.interval: "+(nowTime - preTime)/1000);
                        preTime = System.currentTimeMillis();
                        return ObsFetcher.getZipFansObservable();
                    }
                })
                .flatMap(new Function<Observable<List<User>>, Observable<List<User>>>(){
                    @Override
                    public Observable<List<User>> apply(Observable<List<User>> obs) throws Exception {
                        return obs;
                    }
                });
    }

    //getIntervalFlatMapObs：实现定时获取zip混合数据的效果
    public static Observable<List<User>> getIntervalFlatMapObs() {
        return Observable.interval(0, 2, TimeUnit.SECONDS)
                .flatMap(new Function<Long, Observable<List<User>>>(){
                    @Override
                    public Observable<List<User>> apply(Long obs) throws Exception {
                        return ObsFetcher.getZipFansObservable();
                    }
                });
    }
}
