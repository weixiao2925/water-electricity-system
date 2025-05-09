package org.example.cvitme01.controller.admin;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.example.cvitme01.entity.RestBean;
import org.example.cvitme01.entity.dto.TariffTier;
import org.example.cvitme01.entity.dto.TariffVersion;
import org.example.cvitme01.service.AdminTariffService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/tariff")
@RequiredArgsConstructor
public class AdminTariffController {

    private final AdminTariffService adminTariffService;

    @GetMapping("/list")
    public RestBean<List<TariffTier>> getTariffTiers() {
        return RestBean.success(adminTariffService.getTariffTiers());
    }

    @GetMapping("/now-version")
    public RestBean<TariffVersion> getNowVersion(
            @RequestParam("type") @Valid
            @NotNull @Pattern(regexp = "water|electricity") String type ) {
        TariffVersion message = adminTariffService.getNowVersion(type);
        return message !=null
                ?RestBean.success(message)
                :RestBean.failure(400, "未知错误，请联系管理员");
    }

    @GetMapping("/version")
    public RestBean<List<TariffVersion>> getVersion(
            @RequestParam("type") @Valid
            @NotNull @Pattern(regexp = "water|electricity") String type){
        List<TariffVersion> message = adminTariffService.getVersion(type);
        return message !=null
                ?RestBean.success(message)
                :RestBean.failure(400, "未知错误，请联系管理员");
    }

    @PostMapping("/version-change")
    public RestBean<String> versionChange(
            @RequestParam("type") @Valid @NotNull
            @Pattern(regexp = "water|electricity") String type,
            @RequestParam("oldId") long oldId,
            @RequestParam("newId") long newId) {
        String message = adminTariffService.changeTariffVersion(type, oldId, newId);
        return message == null
                ? RestBean.success()
                : RestBean.failure(400, message);
    }

    @PostMapping("/save")
    public RestBean<String> saveTariffTier(
            @RequestParam("type") @Valid @NotNull
            @Pattern(regexp = "water|electricity") String type,
            @RequestParam("versionId") long versionId,
            @RequestBody List<TariffTier> tariffTiers) {
        String message = adminTariffService.saveTariffTier(type, versionId, tariffTiers);
        return message == null
                ? RestBean.success()
                : RestBean.failure(400, message);
    }




}
