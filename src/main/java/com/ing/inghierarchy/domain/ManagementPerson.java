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
@Document(collation = "management-person")
public class ManagementPerson extends Person {
    private String manages; // Corporate ID here
    private boolean isChapterLead;
}
