package com.timjaanson.desktopctrl.volume.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class VolumeRequest {
    private Integer volumeLevel;
    private boolean muted;
}
