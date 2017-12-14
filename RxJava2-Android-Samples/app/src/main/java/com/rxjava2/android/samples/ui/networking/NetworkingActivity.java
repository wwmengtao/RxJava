package com.rxjava2.android.samples.ui.networking;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Pair;
import android.view.View;

import com.rx2androidnetworking.Rx2AndroidNetworking;
import com.rxjava2.android.samples.ALog;
import com.rxjava2.android.samples.R;
import com.rxjava2.android.samples.model.ApiUser;
import com.rxjava2.android.samples.model.User;
import com.rxjava2.android.samples.model.UserDetail;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by amitshekhar on 04/02/17.
 */

public class NetworkingActivity extends AppCompatActivity {

    public static final String TAG = NetworkingActivity.class.getSimpleName()+" ";
    private Unbinder mUnbinder = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_networking);
        mUnbinder = ButterKnife.bind(this);
    }

    @Override
    protected void onDestroy(){
        ObsFetcher.reset();
        mUnbinder.unbind();
        super.onDestroy();
    }

    /**
     * Map Operator Example
     */
    @OnClick(R.id.map)
    public void map(View view) {
        ALog.Log(TAG+"Execute operation: "+"map");
        Rx2AndroidNetworking.get("https://fierce-cove-29863.herokuapp.com/getAnUser/{userId}")
                .addPathParameter("userId", "1")
                .build()
                .getObjectObservable(ApiUser.class)
                .map(new Function<ApiUser, User>() {
                    @Override
                    public User apply(ApiUser apiUser) throws Exception {
                        // here we get ApiUser from server
                        User user = new User(apiUser);
                        // then by converting, we are returning user
                        return user;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(ObserverInfo.getObUser());
    }

    @OnClick(R.id.zip)
    public void zip(View view) {
        ALog.Log(TAG+"Execute operation: "+"zip");
        /*
        * This do the complete magic, make both network call
        * and then returns the list of user who loves both
        * Using zip operator to get both response at a time
        */
        long time_dsw_1, time_dsw_2;
        time_dsw_1 = System.currentTimeMillis();
        ObsFetcher.getZipFansObservable()//如果单独调用这一句，那么也是会执行的，不管有没有观察者注册。
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(ObserverInfo.getObListUser());
        time_dsw_2 = System.currentTimeMillis();
        ALog.Log(TAG+"zip cost time: "+(time_dsw_2 - time_dsw_1));
    }

    @OnClick(R.id.flatMapAndFilter)
    public void flatMapAndFilter(View view) {
        ALog.Log(TAG+"Execute operation: "+"flatMapAndFilter");
        ObsFetcher.getAllMyFriendsObservable()
        .flatMap(new Function<List<User>, ObservableSource<User>>() { // flatMap - to return users one by one
            @Override
            public ObservableSource<User> apply(List<User> usersList) throws Exception {
                return Observable.fromIterable(usersList); // returning user one by one from usersList.
            }
        })
        .filter(new Predicate<User>() {
            @Override
            public boolean test(User user) throws Exception {
                // filtering user who follows me.
                return user.isFollowing;
            }
        })
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(ObserverInfo.getObUser());
    }


    /**
     * take Operator Example
     */
    @OnClick(R.id.take)
    public void take(View view) {
        ALog.Log(TAG+"Execute operation: "+"take");
        ObsFetcher.getUserListObservable()
                .flatMap(new Function<List<User>, ObservableSource<User>>() { // flatMap - to return users one by one
                    @Override
                    public ObservableSource<User> apply(List<User> usersList) throws Exception {
                        return Observable.fromIterable(usersList); // returning user one by one from usersList.
                    }
                })
                .take(4) // it will only emit first 4 users out of all
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(ObserverInfo.getObUser());
    }


    /**
     * flatMap Operator Example
     */
    @OnClick(R.id.flatMap)
    public void flatMap(View view) {
        ALog.Log(TAG+"Execute operation: "+"flatMap");
        ObsFetcher.getUserListObservable()
                .flatMap(new Function<List<User>, ObservableSource<User>>() { // flatMap - to return users one by one
                    @Override
                    public ObservableSource<User> apply(List<User> usersList) throws Exception {
                        return Observable.fromIterable(usersList); // returning user one by one from usersList.
                    }
                })
                .flatMap(new Function<User, ObservableSource<UserDetail>>() {
                    @Override
                    public ObservableSource<UserDetail> apply(User user) throws Exception {
                        // here we get the user one by one
                        // and returns corresponding getUserDetailObservable
                        // for that userId
                        return ObsFetcher.getUserDetailObservable(user.id);
                    }
                })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(ObserverInfo.getObUserDetail());
    }

    @OnClick(R.id.flatMapWithZip)
    public void flatMapWithZip(View view) {
        ALog.Log(TAG+"Execute operation: "+"flatMapWithZip");
        ObsFetcher.getUserListObservable()
                .flatMap(new Function<List<User>, ObservableSource<User>>() { // flatMap - to return users one by one
                    @Override
                    public ObservableSource<User> apply(List<User> usersList) throws Exception {
                        return Observable.fromIterable(usersList); // returning user one by one from usersList.
                    }
                })
                .flatMap(new Function<User, ObservableSource<Pair<UserDetail, User>>>() {
                    @Override
                    public ObservableSource<Pair<UserDetail, User>> apply(User user) throws Exception {
                        // here we get the user one by one and then we are zipping
                        // two observable - one getUserDetailObservable (network call to get userDetail)
                        // and another Observable.just(user) - just to emit user
                        return Observable.zip(ObsFetcher.getUserDetailObservable(user.id),
                                Observable.just(user),
                                new BiFunction<UserDetail, User, Pair<UserDetail, User>>() {
                                    @Override
                                    public Pair<UserDetail, User> apply(UserDetail userDetail, User user) throws Exception {
                                        // runs when network call completes
                                        // we get here userDetail for the corresponding user
                                        return new Pair<>(userDetail, user); // returning the pair(userDetail, user)
                                    }
                                });
                    }
                })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(ObserverInfo.getObPair());
    }
}
