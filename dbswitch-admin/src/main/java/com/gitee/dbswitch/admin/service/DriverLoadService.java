package com.gitee.dbswitch.admin.service;

import com.gitee.dbswitch.admin.config.DbswitchConfig;
import com.gitee.dbswitch.admin.type.SupportDbTypeEnum;
import java.io.File;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class DriverLoadService {

  private Map<SupportDbTypeEnum, Map<String, File>> drivers = new EnumMap<>(SupportDbTypeEnum.class);

  @Resource
  private DbswitchConfig dbswitchConfig;

  @EventListener(ApplicationReadyEvent.class)
  public void loadDrivers() {
    try {
      doLoadDrivers();
    } catch (Exception e) {
      log.error("load drivers failed:{}", e.getMessage(), e);
      throw e;
    }
  }

  private void doLoadDrivers() {
    String driversBasePath = dbswitchConfig.getDriversBasePath();
    File file = new File(driversBasePath);
    File[] types = file.listFiles();
    if (ArrayUtils.isEmpty(types)) {
      throw new IllegalArgumentException(
          "No drivers type found from path:" + driversBasePath);
    }
    for (File type : types) {
      if (!SupportDbTypeEnum.exists(type.getName())) {
        continue;
      }
      File[] driverVersions = type.listFiles();
      if (ArrayUtils.isEmpty(driverVersions)) {
        throw new IllegalArgumentException(
            "No driver version found from path:" + type.getAbsolutePath());
      }
      for (File driverVersion : driverVersions) {
        if (ArrayUtils.isEmpty(driverVersion.listFiles())) {
          throw new IllegalArgumentException(
              "No driver version jar file found from path:" + driverVersion.getAbsolutePath());
        }
        SupportDbTypeEnum typeEnum = SupportDbTypeEnum.of(type.getName());
        Map<String, File> versionMap = drivers.computeIfAbsent(typeEnum, k -> new HashMap<>());
        versionMap.put(driverVersion.getName(), driverVersion);
        log.info("Load driver for {} ,version:{},path:{}",
            typeEnum.getName(), driverVersion.getName(), driverVersion.getAbsolutePath());
      }
    }
  }

  public List<String> getDriverVersion(SupportDbTypeEnum dbTypeEnum) {
    return Optional.ofNullable(drivers.get(dbTypeEnum)).orElseGet(HashMap::new)
        .keySet().stream().collect(Collectors.toList());
  }

  public Map<String, File> getDriverVersionWithPath(SupportDbTypeEnum dbTypeEnum) {
    return Optional.ofNullable(drivers.get(dbTypeEnum)).orElse(new HashMap<>());
  }

  public File getVersionDriverFile(SupportDbTypeEnum dbTypeEnum, String driverVersion) {
    return drivers.get(dbTypeEnum).get(driverVersion);
  }

}
