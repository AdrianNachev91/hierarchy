package com.ing.inghierarchy.domain;

import lombok.*;
import lombok.experimental.Accessors;
import org.springframework.data.mongodb.core.mapping.Document;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
@Data
@Document(collation = "crew-person")
public class CrewPerson extends Person {
    private String managedBy; // Corporate ID here, must be a lead of the management of this team
}
