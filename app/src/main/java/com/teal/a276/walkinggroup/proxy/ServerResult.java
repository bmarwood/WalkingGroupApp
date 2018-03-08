package com.teal.a276.walkinggroup.proxy;

/**
 * Created by scott on 07/03/18.
 */

public interface ServerResult<T> {
    void result(T ans);
    void error(String error);
}
