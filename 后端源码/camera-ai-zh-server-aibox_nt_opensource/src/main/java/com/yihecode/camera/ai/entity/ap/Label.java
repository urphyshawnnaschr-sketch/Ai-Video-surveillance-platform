package com.yihecode.camera.ai.entity.ap;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Label {
    private String name;
    private String color;
    private Boolean editCell;
    private List<Label> children;
}
