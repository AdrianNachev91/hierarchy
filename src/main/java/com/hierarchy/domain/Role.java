package com.hierarchy.domain;

import lombok.*;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@EqualsAndHashCode(exclude = {"id"})
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
@Data
@Document(collection = "roles")
public class Role {

    public static final String COLLECTION_NAME = "roles";

    @Id
    private String id;
    @Indexed(unique = true)
    private String title;
}
