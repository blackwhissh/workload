package com.blackwhissh.workload.dto.request;

import java.util.List;

public record PublishGiftRequest(String publisherWorkId, List<Integer> hourIdList) {
}
