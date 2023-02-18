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
public class TeamResponse {

    private String id;
    private String title;
    private String teamType; // title of team type here
    private TeamMemberResponse lead;
    @Builder.Default
    private List<TeamMemberResponse> crew = new ArrayList<>();
}
