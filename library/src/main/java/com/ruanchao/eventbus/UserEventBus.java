package com.ruanchao.eventbus;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class UserEventBus {

    private static UserEventBus mUserEventBus;
    private Map<Object,List<SubscribeMethod>> mCacheMap = null;
    private ExecutorService mExecutorService = null;


    private UserEventBus(){
        mCacheMap = new HashMap<>();
        mExecutorService = new ThreadPoolExecutor(
                1, Integer.MAX_VALUE,60,TimeUnit.SECONDS,new SynchronousQueue<Runnable>());
    }

    private Handler mHandler = new Handler();

    public static UserEventBus getDefault(){
        if (mUserEventBus == null){
            synchronized (UserEventBus.class){
                if (mUserEventBus == null){
                    mUserEventBus = new UserEventBus();
                }
            }
        }
        return mUserEventBus;
    }

    public void register(Object activity){

        List<SubscribeMethod> subscribeMethods = mCacheMap.get(activity);
        if (subscribeMethods == null){
            subscribeMethods = getSubscribeMethods(activity);
            mCacheMap.put(activity,subscribeMethods);
        }
    }

    public void post(final Object event){

        Set<Map.Entry<Object, List<SubscribeMethod>>> entries = mCacheMap.entrySet();
        for (Map.Entry<Object, List<SubscribeMethod>> entry:entries){
            final Object activity = entry.getKey();
            List<SubscribeMethod> subscribeMethods = entry.getValue();
            if (activity == null || subscribeMethods == null){
                return;
            }
            for (final SubscribeMethod subscribeMethod : subscribeMethods) {
                if (subscribeMethod.getEvent().isAssignableFrom(event.getClass())){
                    switch (subscribeMethod.getThreadMode()){
                        case Async:
                            mExecutorService.execute(new Runnable() {
                                @Override
                                public void run() {
                                    invoke(activity, subscribeMethod,event);
                                }
                            });
                            break;
                        case MainThread:
                            //判断当前线程是否是主线程
                            if (Looper.myLooper() == Looper.getMainLooper()){
                                invoke(activity, subscribeMethod,event);
                            }else {
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        invoke(activity, subscribeMethod,event);
                                    }
                                });
                            }
                            break;
                        case PostThread:
                            invoke(activity, subscribeMethod,event);
                            break;
                        case BackgroundThread:
                            if (Looper.myLooper() != Looper.getMainLooper()){
                                invoke(activity, subscribeMethod,event);
                            }else {
                                mExecutorService.execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        invoke(activity, subscribeMethod,event);
                                    }
                                });
                            }
                            break;
                    }
                }
            }
        }
    }

    private void invoke(Object activity, SubscribeMethod subscribeMethod, Object event) {
        if (activity != null) {
            try {
                subscribeMethod.getMethod().invoke(activity,event);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    public void unRegister(Object activity){

        if (mCacheMap.containsKey(activity)) {
            mCacheMap.remove(activity);
        }
    }

    private List<SubscribeMethod> getSubscribeMethods(Object activity) {
        Class<?> aClass = activity.getClass();
        List<SubscribeMethod> subscribeMethods = new ArrayList<>();
        while (aClass != null) {
            //过滤掉系统中的方法
            String name = aClass.getName();
            if (name.startsWith("java.")
                    || name.startsWith("javax.")
                    || name.startsWith("android.")){
                break;
            }
            //获得当前class所有生命的public方法
            Method[] methods = aClass.getDeclaredMethods();
            if (methods == null) {
                break;
            }
            for (Method method : methods) {
                Subscribe annotation = method.getAnnotation(Subscribe.class);
                if (annotation != null){
                    Class<?>[] parameterTypes = method.getParameterTypes();
                    if (parameterTypes.length != 1){
                        throw new RuntimeException("Subscribe method only receive one parameter");
                    }
                    subscribeMethods.add(new SubscribeMethod(method, parameterTypes[0], annotation.threadMode()));
                }
            }

            //遍历父类是否也注册了对应的订阅者
            aClass = aClass.getSuperclass();
        }


        return subscribeMethods;
    }
}
