package com.teal.a276.walkinggroup.model.dataobjects;

/**
 * An enum that represents a key for adding query parameters to the messaging end point
 */
public enum MessageQueryKey {
    IS_EMERGENCY("is-emergency"),
    TO_GROUP("togroup"),
    FOR_USER("foruser"),
    READ_STATUS("status");

    final String value;

    MessageQueryKey(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
