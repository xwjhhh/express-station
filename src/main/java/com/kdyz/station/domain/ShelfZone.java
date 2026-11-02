package com.kdyz.station.domain;

/** 货架区域：编码前缀用于取件码与库位编码。 */
public enum ShelfZone {
    NORMAL("A"), COLD("C"), FLOOR("F");

    private final String prefix;

    ShelfZone(String prefix) { this.prefix = prefix; }

    public String prefix() { return prefix; }
}
