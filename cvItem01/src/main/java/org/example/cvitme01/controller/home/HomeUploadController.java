package org.example.cvitme01.controller.home;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.example.cvitme01.entity.RestBean;
import org.example.cvitme01.entity.dto.Reading;
import org.example.cvitme01.service.HomeUploadService;
import org.example.cvitme01.utils.Const;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/home/upload")
@RequiredArgsConstructor
public class HomeUploadController {

    private final HomeUploadService homeUploadService;

    @PostMapping("/image")
    public RestBean<Reading> uploadImage(@RequestParam("file") MultipartFile file,
                                         @RequestParam("type") @Valid @NotNull @Pattern(regexp = "(water|electricity|gas)") String type,
                                         @RequestAttribute(Const.ATTR_USER_ID) int id) throws Exception {
        Reading message = homeUploadService.uploadImage(file, type, id);
        return message != null
                ? RestBean.success(message)
                : RestBean.failure(400, "未知错误，请联系管理员");
    }

    @PostMapping("/image/save")
    public RestBean<String> saveImage(@RequestParam("type") @Valid @NotNull @Pattern(regexp = "(water|electricity|gas)") String type,
                                      @RequestBody @Valid Reading reading,
                                      @RequestAttribute(Const.ATTR_USER_ID) int id) throws Exception {
        String message = homeUploadService.saveImage(type, reading, id);
        return message == null
                ? RestBean.success()
                : RestBean.failure(400, message);
    }


}
