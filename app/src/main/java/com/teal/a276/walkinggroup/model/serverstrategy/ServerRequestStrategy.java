package com.teal.a276.walkinggroup.model.serverstrategy;

/**
 * Created by scott on 11/03/18.
 */

public interface ServerRequestStrategy<T> {
    void makeServerRequest();
    T getServerResult();
}
