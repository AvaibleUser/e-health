package org.ehealth.ward.service.finance;

import java.util.List;
import java.util.Optional;

import org.ehealth.ward.domain.dto.finance.bill.BillDto;
import org.ehealth.ward.domain.dto.finance.bill.UpdateBillDto;

public interface IBillService {

    List<BillDto> findPatientBills(long patientId);

    Optional<BillDto> findPatientBillById(long patientId, long billId);

    void addBill(long patientId);

    void updateBill(long billId, long patientId, UpdateBillDto bill);
}
