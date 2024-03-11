package com.pjs.golf.common.exception;


import lombok.NoArgsConstructor;

/**
 * <pre>
 * 해당하는 데이터가 존재하지 않을 때 발생시킬 RuntimeException.
 * 서비스에서 null에 따른 로직을 건너뛰고 optional 객체가 비어있을경우
 * 바로 해당 Exception 던져서 컨트롤러 계층에서 try catch 로 처리하기 위해 만듬.
 * </pre>
 * */
@NoArgsConstructor
public class NoSuchDataCustomException extends RuntimeException {
    public NoSuchDataCustomException(String message) {
        super(message);
    }
}
