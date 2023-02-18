package com.hierarchy.web.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.List;

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
    @Builder.Default
    private List<ManagerResponse> manages = new ArrayList<>(); // if this is empty then this is the team lead
    @Builder.Default
    private List<TeamResponse> teamsManaged = new ArrayList<>();
}
