package com.timjaanson.desktopctrl.resource;

import com.timjaanson.desktopctrl.entity.BaseResponse;
import com.timjaanson.desktopctrl.volume.entity.VolumeRequest;
import com.timjaanson.desktopctrl.volume.service.VolumeService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UIResource {
    static final Logger log = LoggerFactory.getLogger(UIResource.class);

    private final VolumeService volumeService;

    @GetMapping("/volume")
    public VolumeRequest getVolume() throws Exception {
        return volumeService.getVolume();
    }

    @PostMapping("/volume")
    public BaseResponse setVolume(@RequestBody VolumeRequest volumeRequest) {
        log.info("setVolume request: {}", volumeRequest);
        return volumeService.setVolume(volumeRequest);
    }
}
