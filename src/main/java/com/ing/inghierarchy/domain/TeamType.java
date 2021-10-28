package com.ing.inghierarchy.domain;

import lombok.*;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
@Data
@Document(collection = "team-types")
@EqualsAndHashCode(exclude = {"id"})
public class TeamType {

    public static final String COLLECTION_NAME = "team-types";

    @Id
    private String id;
    private String title; // i.e. squad or chapter
}
