package org.ehealth.hr.domain.dto.reports;

import lombok.Builder;

import java.util.List;

@Builder(toBuilder = true)
public record ReportAssignedEmployeeDto(
    List<HistoryAssignedEmployee> report
) {
}
