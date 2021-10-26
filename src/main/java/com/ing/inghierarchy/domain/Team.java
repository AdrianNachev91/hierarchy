package com.ing.inghierarchy.domain;

import lombok.*;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@EqualsAndHashCode(exclude = {"id"})
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
@Data
@Document(collection = "teams")
public class Team {

    public static final String COLLECTION_NAME = "teams";

    @Id
    private String id;
    private String title;
    private String teamType; // team type ID
    private String managedBy; // management ID
    private String[] crew; // TeamMember IDs
}
