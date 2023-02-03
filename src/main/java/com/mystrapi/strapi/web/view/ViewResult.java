package com.mystrapi.strapi.web.view;

import lombok.Data;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * rest controller 通用返回对象
 *
 * @author tangqiang
 */
@Data
public class ViewResult<T> {

    private boolean success;
    private String msg;
    private T data;

    public ViewResult(boolean success, String msg, T data) {
        this.success = success;
        this.msg = msg;
        this.data = data;
    }

    public static <T> ViewResult<T> success(T data) {
        return ViewResult.success("success", data);
    }

    public static <T> ViewResult<T> success(String msg, T data) {
        return ViewResult.<T>builder().code(true).msg(msg).data(data).build();
    }

    public static <T> ViewResult<T> failure(T data) {
        return ViewResult.failure("success", data);
    }

    public static <T> ViewResult<T> failure(String msg, T data) {
        return ViewResult.<T>builder().code(false).msg(msg).data(data).build();
    }

    @Contract(value = " -> new", pure = true)
    public static <T> @NotNull ViewResultBuilder<T> builder() {
        return new ViewResultBuilder<>();
    }

    public static class ViewResultBuilder<T> {
        private boolean success;
        private String msg;
        private T data;

        public ViewResultBuilder() {
        }

        public ViewResultBuilder<T> code(boolean success) {
            this.success = success;
            return this;
        }

        public ViewResultBuilder<T> msg(String msg) {
            this.msg = msg;
            return this;
        }

        public ViewResultBuilder<T> data(T data) {
            this.data = data;
            return this;
        }

        public ViewResult<T> build() {
            return new ViewResult<>(this.success, this.msg, this.data);
        }
    }

}
