package com.flipkart.sample.retreat;

import com.yammer.tenacity.core.properties.TenacityPropertyKey;

public enum SampleCommand implements TenacityPropertyKey {
    GET_EMPLOYEE_DETAILS,
    SAVE_EMPLOYEE_DETAILS
}
