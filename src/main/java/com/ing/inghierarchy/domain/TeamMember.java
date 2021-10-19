package com.ing.inghierarchy.domain;

import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Document;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@SuperBuilder
@Accessors(chain = true)
@Data
@Document(collection = "team-members")
public class TeamMember extends Person {
    public static final String COLLECTION_NAME = "team-members";
}
