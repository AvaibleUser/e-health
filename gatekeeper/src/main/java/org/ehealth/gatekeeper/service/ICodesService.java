package org.ehealth.gatekeeper.service;

public interface ICodesService {

    String generateConfirmCode(String key);

    boolean confirmCode(String key, String code);

    boolean existsCode(String key, String code);
}
