package com.ezerium.shared.utils;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DataUtil<T, V> {

    private T key;
    private V value;

}
