package com.ing.inghierarchy;

import com.ing.inghierarchy.domain.Manager;
import com.ing.inghierarchy.domain.Team;
import com.ing.inghierarchy.domain.TeamMember;
import com.ing.inghierarchy.domain.TeamType;
import com.ing.inghierarchy.web.request.ManagerRequest;
import com.ing.inghierarchy.web.request.TeamMemberRequest;
import com.ing.inghierarchy.web.request.TeamRequest;
import com.ing.inghierarchy.web.TeamTypeRequest;

public class TestUtils {

    public static String json(String singleQuotedJson) {
        return singleQuotedJson.replaceAll("'", "\"");
    }

    public static Manager manager(String name, boolean lead, String manages, String corporateId, String roleId) {
        return Manager.builder().name(name).lead(lead).manages(manages).corporateId(corporateId).roleId(roleId).build();
    }

    public static ManagerRequest managerRequest(String name, boolean lead, String manages, String corporateId, String roleId) {
        return ManagerRequest.builder().name(name).lead(lead).manages(manages).corporateId(corporateId).roleId(roleId).build();
    }

    public static TeamMember teamMember(String name, String corporateId, String roleId) {
        return TeamMember.builder().name(name).corporateId(corporateId).roleId(roleId).build();
    }

    public static TeamMemberRequest teamMemberRequest(String name, String corporateId, String roleId) {
        return TeamMemberRequest.builder().name(name).corporateId(corporateId).roleId(roleId).build();
    }

    public static Team team(String title, String managedBy, String teamType) {
        return Team.builder().title(title).managedBy(managedBy).teamType(teamType).build();
    }

    public static TeamRequest teamRequest(String title, String managedBy, String teamType) {
        return TeamRequest.builder().title(title).managedBy(managedBy).teamType(teamType).build();
    }

    public static TeamType teamType(String title) {
        return TeamType.builder().title(title).build();
    }

    public static TeamTypeRequest teamTypeRequest(String title) {
        return TeamTypeRequest.builder().title(title).build();
    }
}
