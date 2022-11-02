package com.timjaanson.desktopctrl.volume.entity;

import com.timjaanson.desktopctrl.entity.BaseResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VolumeResponse extends BaseResponse {
    private Integer volumeLevel;
    private boolean muted;
}
