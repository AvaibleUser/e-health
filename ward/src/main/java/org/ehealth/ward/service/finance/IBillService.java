package org.ehealth.ward.service.finance;

import java.util.List;
import java.util.Optional;

import org.ehealth.ward.domain.dto.finance.bill.BillDto;
import org.ehealth.ward.domain.dto.finance.bill.UpdateBillDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IBillService {

    Page<BillDto> findPatientBills(long patientId, Pageable pageable);

    List<BillDto> findOpenPatientBills(long patientId);

    Optional<BillDto> findPatientBillById(long patientId, long billId);

    void addBill(long patientId);

    void updateBill(long billId, long patientId, UpdateBillDto bill);
}
