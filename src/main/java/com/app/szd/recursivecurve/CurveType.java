package com.app.szd.recursivecurve;

public enum CurveType {
    KOCH_SNOWFLAKE, HILBERT, SIERPINSKI;

    public String toString() {
        return name().toLowerCase();
    }
}
