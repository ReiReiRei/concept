package org.conept;

/**
 * Created by ReiReiRei on 05.03.2017.
 */
class Location {
    private int id;
    private String name;
    private String[] alternames;

    Location(int id, String name, String[] alternames) {
        this.id = id;
        this.name = name;
        this.alternames = alternames;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String[] getAlternames() {
        return alternames;
    }
}
