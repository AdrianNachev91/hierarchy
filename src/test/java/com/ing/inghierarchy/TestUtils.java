package com.ing.inghierarchy;

import com.ing.inghierarchy.domain.*;
import com.ing.inghierarchy.web.request.EmployeeRequest;
import com.ing.inghierarchy.web.request.RoleRequest;
import com.ing.inghierarchy.web.request.TeamRequest;
import com.ing.inghierarchy.web.TeamTypeRequest;

import java.util.HashSet;
import java.util.Set;

public class TestUtils {

    public static String json(String singleQuotedJson) {
        return singleQuotedJson.replaceAll("'", "\"");
    }

    public static Employee employee(String name, String corporateId, String roleId) {
        return Employee.builder().name(name).corporateId(corporateId).roleId(roleId).build();
    }

    public static EmployeeRequest employeeRequest(String name, String corporateId, String roleId) {
        return EmployeeRequest.builder().name(name).corporateId(corporateId).roleId(roleId).build();
    }

    public static Team team(String title, String managedBy, String leadId, String teamType) {
        return Team.builder().title(title).managedBy(managedBy).leadId(leadId).teamType(teamType).build();
    }

    public static TeamRequest teamRequest(String title, String managedBy, String leadId, String teamType) {
        return TeamRequest.builder().title(title).managedBy(managedBy).leadId(leadId).teamType(teamType).build();
    }

    public static TeamType teamType(String title) {
        return TeamType.builder().title(title).build();
    }

    public static TeamTypeRequest teamTypeRequest(String title) {
        return TeamTypeRequest.builder().title(title).build();
    }

    public static ManagementChain managementChain(boolean attachedToTeam, Set<ManagementChain.ManagerInChain> managementChains) {
        return ManagementChain.builder().attachedToTeam(attachedToTeam).managersChain(new HashSet<>(managementChains)).build();
    }

    public static ManagementChain.ManagerInChain managerInChain(String managerId, String managesId) {
        return ManagementChain.ManagerInChain.builder().managerId(managerId).manages(managesId).build();
    }

    public static Role role(String title) {
        return Role.builder().title(title).build();
    }

    public static RoleRequest roleRequest(String title) {
        return RoleRequest.builder().title(title).build();
    }
}
