package org.ehealth.rx.util;

import static org.mockito.BDDMockito.then;

import org.mockito.BDDMockito.Then;

public class ThenMockAlias {

    public static <T> Then<T> thenMock(T mock) {
        return then(mock);
    }
}
