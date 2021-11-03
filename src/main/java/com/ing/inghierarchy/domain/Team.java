package com.ing.inghierarchy.domain;

import lombok.*;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(exclude = {"id"})
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
@Data
@Document(collection = "teams")
public class Team {

    public static final String COLLECTION_NAME = "teams";

    public static final String FIELD_MANAGED_BY = "managedBy";
    public static final String FIELD_TEAM_TYPE = "teamType";

    @Id
    private String id;
    @Indexed(unique = true)
    private String title;
    private String teamType; // team type ID
    private String managedBy; // management ID
    private List<String> crew = new ArrayList<>(); // TeamMember IDs
}
