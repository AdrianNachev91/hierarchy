package com.hierarchy.web.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Accessors(chain = true)
public class HierarchyStructureResponse {
    private HierarchyChain hierarchyChain;
    private String previous; // link to previous set of teams
    private String next; // link to next set of teams
    private long total;
}
