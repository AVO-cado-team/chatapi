package sk.avo.chatapi.presentation.dto.dev;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BuildInfoResponse {
  private String version;
  private String buildTime;
  private String gitCommitSha;
  private String gitBranch;
}
