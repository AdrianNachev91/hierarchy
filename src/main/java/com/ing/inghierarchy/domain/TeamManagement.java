package com.ing.inghierarchy.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
@Data
@Document(collection = "team-management")
public class TeamManagement {

    public static final String COLLECTION_NAME = "team-management";

    @Id
    private String id;
    private Set<String> managerChain; // ids of managers
}
