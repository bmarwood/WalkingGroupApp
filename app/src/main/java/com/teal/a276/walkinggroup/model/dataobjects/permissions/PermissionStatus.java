package com.teal.a276.walkinggroup.model.dataobjects.permissions;

/**
 * Created by scott on 06/04/18.
 */

public enum PermissionStatus {
    PENDING("PENDING"),
    APPROVED("APPROVED"),
    DECLINED("DECLINED");

    final String status;

    PermissionStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
