package com.ing.inghierarchy.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
@Data
@Document(collation = "team")
public class Team {

    @Id
    private String id;
    private String title;
    private String teamType; // team type ID
    private String managedBy; // management ID
    private CrewPerson[] crew;
}
