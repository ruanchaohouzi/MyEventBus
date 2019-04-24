package com.ruanchao.eventbus;

import java.lang.reflect.Method;

public class SubscribeMethod {

    private Method method;
    private Class<?> event;
    private ThreadMode threadMode;
    private boolean isStickyEvent = false;

    public SubscribeMethod(Method method, Class<?> event, ThreadMode threadMode, boolean isStickyEvent) {
        this.method = method;
        this.event = event;
        this.threadMode = threadMode;
        this.isStickyEvent = isStickyEvent;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public Class<?> getEvent() {
        return event;
    }

    public void setEvent(Class<?> event) {
        this.event = event;
    }

    public ThreadMode getThreadMode() {
        return threadMode;
    }

    public void setThreadMode(ThreadMode threadMode) {
        this.threadMode = threadMode;
    }

    public boolean isStickyEvent() {
        return isStickyEvent;
    }

    public void setStickyEvent(boolean stickyEvent) {
        isStickyEvent = stickyEvent;
    }
}
