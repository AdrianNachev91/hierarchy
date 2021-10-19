package com.ing.inghierarchy.web;

import com.ing.inghierarchy.domain.Person;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Accessors(chain = true)
@Data
public class ManagerRequest extends Person {

    private String manages; // Corporate ID here
    private boolean lead;
}
