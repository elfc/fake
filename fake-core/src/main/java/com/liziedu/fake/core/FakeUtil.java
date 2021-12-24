package com.liziedu.fake.core;

import static java.lang.String.format;

public class FakeUtil {

    /**
     * 检查传入boolean 状态, 如果为false 则抛出状态异常
     * @param expression
     * @param errorMessage
     * @param errorMessageArgs
     */
    public static void checkState(boolean expression,
                                  String errorMessage,
                                  Object... errorMessageArgs) {
        if (!expression) {
            throw new IllegalStateException(format(errorMessage, errorMessageArgs));
        }
    }
}
