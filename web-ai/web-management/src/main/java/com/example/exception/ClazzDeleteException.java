package com.example.exception;

// 在 Spring Boot 中，强烈建议继承 RuntimeException（运行时异常）
// 这样抛出时不需要在方法签名上写 throws，代码更简洁
public class ClazzDeleteException extends RuntimeException {

        // 提供一个带消息的构造方法
        public ClazzDeleteException(String message) {
            super(message);
        }
    }

