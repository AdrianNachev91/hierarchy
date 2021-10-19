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
@Document(collection = "managers")
public class Manager extends Person {

    public static final String COLLECTION_NAME = "managers";

    private String manages; // Corporate ID here
    private boolean lead;
}
