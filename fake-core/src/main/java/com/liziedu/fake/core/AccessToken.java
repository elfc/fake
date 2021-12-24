package com.liziedu.fake.core;

public interface AccessToken {

    /**
     * token 放在请求头中还是参数中
     */
    enum TokenPosition {

        HEADER, PARAMETER

    }

    /**
     * 获取每次请求带入的token
     * @return
     */
    String token();

    /**
     * token 放置的位置
     * @param position
     */
    void tokenPosition(TokenPosition position);
}
