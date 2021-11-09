package com.hierarchy.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
@Data
@Document(collection = "management-chain")
public class ManagementChain {

    public static final String COLLECTION_NAME = "management-chain";

    public static final String FIELD_ID = "id";
    public static final String FIELD_ATTACHED_TO_TEAM = "attachedToTeam";
    public static final String FIELD_MANAGER_ID = "managerChain.managerId";

    @Id
    private String id;
    private boolean attachedToTeam; // Whether the chain leads to a team or not, false by default
    @Builder.Default
    private Set<ManagerInChain> managersChain = new HashSet<>();

    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Accessors(chain = true)
    @Data
    public static class ManagerInChain {
        @NotBlank
        private String managerId;
        private String manages; // null when last of the chain
    }
}
