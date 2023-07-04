package sk.avo.chatapi.presentation.dev;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import sk.avo.chatapi.presentation.dev.dto.*;
import sk.avo.chatapi.config.Application;

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
