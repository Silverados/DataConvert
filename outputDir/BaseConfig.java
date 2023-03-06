// Version: {2023-03-06T11:48:54.060978800}
package com.wyw.config;

import java.util.*;

//====================CUSTOM BLOCK START====================
//====================CUSTOM BLOCK END====================

public class BaseConfig {

//====================CUSTOM BLOCK 2 START====================
//====================CUSTOM BLOCK 2 END====================

    @FunctionalInterface
    public interface Void0 {
        void run();
    }

    @FunctionalInterface
    public interface Void1<T> {
        void run(T t);
    }

    @FunctionalInterface
    public interface Void2<T, U> {
        void run(T t, U u);
    }

    @FunctionalInterface
    public interface Void3<T, U, V> {
        void run(T t, U u, V v);
    }

    @FunctionalInterface
    public interface Func0<R> {
        R run();
    }

    @FunctionalInterface
    public interface Func1<T, R> {
        R run(T t);
    }

    @FunctionalInterface
    public interface Func2<T, U, R> {
        R run(T t, U u);
    }

    @FunctionalInterface
    public interface Func3<T, U, V, R> {
        R run(T t, U u, V v);
    }
}
