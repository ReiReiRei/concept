package org.conept;


import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.shingle.ShingleFilter;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import java.io.File;
import java.io.IOException;
import java.io.SyncFailedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Vector;
import java.util.stream.Stream;


/**
 * Created by ReiReiRei on 05.03.2017.
 */
public class ConceptIndex {
    private final HashMap<Integer, Location> idIndex = new HashMap<>(15000);
    private final HashMap<String, Location> lowerCaseIndex = new HashMap<>(15000);
    private final HashMap<String, Location> nameIndex = new HashMap<>(15000);
    private final HashMap<String, Location> alternameIndex = new HashMap<>(15000);
    private int maxWordsInName = 1;

    private static final Logger log = Logger.getLogger(ConceptIndex.class
            .getName());

    ConceptIndex(Path path) {

        try (Stream<String> stream = Files.lines(path)) {
            stream.map(this::parseLocation).forEach(location -> getIdIndex().put(location.getId(), location));
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }

        //nameindex
        getIdIndex().forEach((k, v) -> getNameIndex().put(v.getName(), v));

        //lowercaseindex
        getIdIndex().forEach((k, v) -> getLowerCaseIndex().put(v.getName().toLowerCase(), v));


        getIdIndex().forEach((k, v) -> {
            String[] alternames = v.getAlternames();
            for (String altername : alternames) {
                getAlternameIndex().put(altername.toLowerCase(), v);
            }
        });
        getAlternameIndex().forEach((k, v) -> this.maxWordsInName = Math.max(this.getMaxWordsInName(), k.length()));
    }

    private Location parseLocation(final String line) {
        String[] tokens = line.split("\t");

        int id = Integer.parseInt(tokens[0]);
        String name = tokens[1];
        String[] alternatenames = tokens[3].split(",");
        if (alternatenames.length == 0) {
            alternatenames = new String[1];
            alternatenames[0] = name;
        }

        Double latitude = -999999.0;
        try {
            latitude = Double.parseDouble(tokens[4]);
        } catch (NumberFormatException e) {
            latitude = -999999.0;
        }
        Double longitude = -999999.0;
        try {
            longitude = Double.parseDouble(tokens[5]);
        } catch (NumberFormatException e) {
            longitude = -999999.0;
        }

        int population = 0;
        try {
            population = Integer.parseInt(tokens[14]);
        } catch (NumberFormatException e) {
            population = 0;
        }

        String featureCode = tokens[7];
        String countryCode = tokens[8];
        String admin1Code = tokens[10];
        String admin2Code = tokens[11];
        return new Location(id, name, alternatenames);
    }

    public HashMap<String, Location> getLowerCaseIndex() {
        return lowerCaseIndex;
    }

    public HashMap<Integer, Location> getIdIndex() {
        return idIndex;
    }

    public HashMap<String, Location> getNameIndex() {
        return nameIndex;
    }

    public HashMap<String, Location> getAlternameIndex() {
        return alternameIndex;
    }

    public int getMaxWordsInName() {
        return maxWordsInName;
    }
}


