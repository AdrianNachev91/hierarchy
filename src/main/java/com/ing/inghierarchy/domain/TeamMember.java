package com.ing.inghierarchy.domain;

import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Document;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Accessors(chain = true)
@Data
@Document(collection = "team-members")
public class TeamMember extends Person {

    public static final String COLLECTION_NAME = "team-members";

    private String managedBy; // Corporate ID here, must be a lead of the management of this team
}
