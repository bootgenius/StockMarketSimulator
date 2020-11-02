package org.stockmarketsimulator.backend.domain;

public class Symbol {

    //region Fields
    private String name;

    private String description;
    //endregion

    //region Properties
    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
    //endregion

    //region .ctor
    public Symbol(String name, String description) {
        this.name = name;
        this.description = description;
    }
    //endregion

}
