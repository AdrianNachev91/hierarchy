package com.hierarchy.web.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
public class ManagerResponse {

    private String id;
    private String corporateId;
    private String role;
    private String name;
    private Set<ManagerResponse> manages = new HashSet<>(); // if this is empty then this is the team lead
    private Set<TeamResponse> teamsManaged = new HashSet<>();
}
