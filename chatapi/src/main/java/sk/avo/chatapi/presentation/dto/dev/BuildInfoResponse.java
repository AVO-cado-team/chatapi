package sk.avo.chatapi.presentation.dto.dev;

import lombok.Data;

@Data
public class BuildInfoResponse {
    private String version;
    private String buildTime;
    private String gitCommitSha;
    private String gitBranch;
}
