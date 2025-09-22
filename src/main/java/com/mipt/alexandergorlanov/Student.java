package com.mipt.alexandergorlanov;

public interface Student<T,R> {
    R study(T subject);
}
