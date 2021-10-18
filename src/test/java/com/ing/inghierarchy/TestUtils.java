package com.ing.inghierarchy;

import com.ing.inghierarchy.domain.Manager;

public class TestUtils {

    public static String json(String singleQuotedJson) {
        return singleQuotedJson.replaceAll("'", "\"");
    }

    public static Manager manager(String name, boolean lead, String manages, String corporateId, String roleId) {
        return Manager.builder().name(name).lead(lead).manages(manages).corporateId(corporateId).roleId(roleId).build();
    }
}
