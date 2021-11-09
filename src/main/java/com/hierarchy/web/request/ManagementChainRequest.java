package com.hierarchy.web.request;

import com.hierarchy.domain.ManagementChain;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotEmpty;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Accessors(chain = true)
@Data
public class ManagementChainRequest {

    @NotEmpty
    private Set<ManagementChain.ManagerInChain> managersChain;

    public ManagementChain toManagementChain() {
        return ManagementChain.builder().managersChain(this.managersChain).build();
    }
}
