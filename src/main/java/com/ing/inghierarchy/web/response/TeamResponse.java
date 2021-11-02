package com.ing.inghierarchy.web.response;

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
public class TeamResponse {

    private String id;
    private String title;
    private String teamType; // title of team type here
    private Set<TeamMemberResponse> crew = new HashSet<>();
}
