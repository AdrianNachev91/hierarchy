package com.ing.inghierarchy.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;

import javax.validation.constraints.NotBlank;

@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Accessors(chain = true)
@Data
@CompoundIndexes({
        @CompoundIndex(name = "corporate-id_role-id", def = "{'corporateId' : 1, 'roleId': 1}")
})
public class Person {
    @Id
    private String id;
    @NotBlank
    private String corporateId;
    @NotBlank
    private String name;
    @NotBlank
    private String roleId;
}
