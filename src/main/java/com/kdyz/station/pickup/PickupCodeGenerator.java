package com.kdyz.station.pickup;

/** 取件码生成接口。 */
public interface PickupCodeGenerator {
    /** 根据库位生成在库期间唯一的取件码。 */
    String generate(String slot);

    /** 取件后回收取件码，允许复用。 */
    void recycle(String code);
}
