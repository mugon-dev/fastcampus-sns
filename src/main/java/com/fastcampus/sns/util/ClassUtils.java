package com.fastcampus.sns.util;

import java.util.Optional;

public class ClassUtils {

    // 안전하게 casting
    public static <T> Optional<T> getSafeCastInstance(Object o, Class<T> clazz) {
        return clazz != null && clazz.isInstance(o) ? Optional.of(clazz.cast(o)) : Optional.empty();
    }

}
