package com.stonnotes.logtracking.exception;

import com.stonnotes.logtracking.result.CodeMsg;

/**
 * @Author: javan
 * @Date: 2019/6/21
 */
public class GlobalException extends RuntimeException{

    private static final long serialVersionUID = 1L;

    private CodeMsg cm;

    public GlobalException(CodeMsg cm) {
        super(cm.toString());
        this.cm = cm;
    }

    public CodeMsg getCm() {
        return cm;
    }
}
