package org.conept;

/**
 * Created by ReiReiRei on 05.03.2017.
 */
class GeoAnnotation {
    private int geonameid;
    private String name;
    private String coveredText;
    private int start;
    private int end;
    private double score;

    GeoAnnotation(int geonameid, String name, String coveredText, int start, int end, double score) {
        this.geonameid = geonameid;
        this.name = name;
        this.coveredText = coveredText;
        this.start = start;
        this.end = end;
        this.score = score;
    }

    public String getName() {
        return name;
    }

    public int getGeonameid() {
        return geonameid;
    }

    public String getCoveredText() {
        return coveredText;
    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    public double getScore() {
        return score;
    }
}
