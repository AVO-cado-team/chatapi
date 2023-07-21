package sk.avo.chatapi.presentation.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sk.avo.chatapi.config.Application;
import sk.avo.chatapi.presentation.dto.dev.BuildInfoResponse;

@RestController
@RequestMapping("/api/dev/")
public class BuildInfo {
  private final Application application;

  public BuildInfo(Application application) {
    this.application = application;
  }

  @GetMapping("/buildinfo")
  public BuildInfoResponse getBuildInfo() {
    BuildInfoResponse buildInfoResponse = new BuildInfoResponse();
    buildInfoResponse.setVersion(application.getVersion());
    buildInfoResponse.setBuildTime(application.getBuildTime());
    buildInfoResponse.setGitCommitSha(application.getGitCommitSha());
    buildInfoResponse.setGitBranch(application.getGitBranch());
    return buildInfoResponse;
  }
}
