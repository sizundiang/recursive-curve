package com.szd.app.recursivecurve;

public enum CurveType {
    KOCH_SNOWFLAKE, HILBERT, SIERPINSKI;

    public String toString() {
        return name().toLowerCase();
    }
}
