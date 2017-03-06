package org.conept;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.shingle.ShingleFilter;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import java.io.IOException;
import java.util.Vector;

/**
 * Created by ReiReiRei on 05.03.2017.
 */
class Annotator {
    private ConceptIndex index;

    enum AnnotatorType {
        exectly,
        lowercase,
        altername
    }

    private AnnotatorType annotatorType;

    private String text;

    Annotator(ConceptIndex index, AnnotatorType annotatorType, String text) {
        this.index = index;
        this.annotatorType = annotatorType;
        this.text = text;
    }

    public Vector<GeoAnnotation> annotate() {
        final Vector<GeoAnnotation> annotations = new Vector<>();

        try {
            Analyzer analyzer = new StandardAnalyzer();
            TokenStream tokenizer = analyzer.tokenStream("text", text);
            tokenizer = new ShingleFilter(tokenizer, 2, index.getMaxWordsInName());
            CharTermAttribute charTermAttribute = tokenizer.addAttribute(CharTermAttribute.class);
            tokenizer.reset();

            while (tokenizer.incrementToken()) {
                String token = charTermAttribute.toString();
                if (annotatorType == AnnotatorType.exectly) {
                    Vector<GeoAnnotation> annotation = getOtherAnnotations(exactlyNameAnnotatorRaw(token));
                    if (annotation != null) {
                        annotations.addAll(annotation);
                    }
                }
                if (annotatorType == AnnotatorType.lowercase) {
                    Vector<GeoAnnotation> annotation = getOtherAnnotations(lowerCaseAnnotatorRaw(token));
                    if (annotation != null) {
                        annotations.addAll(annotation);
                    }
                }
                if(annotatorType == AnnotatorType.altername) {
                    Vector<GeoAnnotation> annotation = getOtherAnnotations(alternamesAnnotatorRaw(token));
                    if (annotation != null) {
                        annotations.addAll(annotation);
                    }

                }

            }
            tokenizer.end();
            tokenizer.close();
        } catch (IOException e) {

        }
        return annotations;
    }

    private GeoAnnotation exactlyNameAnnotatorRaw(String token) {
        if (index.getNameIndex().containsKey(token)) {
            Location a = index.getNameIndex().get(token);
            return new GeoAnnotation(a.getId(), a.getName(), a.getName(), 0, 0, 1);
        } else {
            return null;
        }

    }

    private GeoAnnotation lowerCaseAnnotatorRaw(String token) {
        if (index.getLowerCaseIndex().containsKey(token.toLowerCase())) {
            Location a = index.getLowerCaseIndex().get(token.toLowerCase());
            return new GeoAnnotation(a.getId(), a.getName(), a.getName(), 0, 0, 1);
        } else {
            return null;
        }
    }

    private GeoAnnotation alternamesAnnotatorRaw(String token) {
        if (index.getLowerCaseIndex().containsKey(token.toLowerCase())) {
            Location a = index.getLowerCaseIndex().get(token.toLowerCase());
            return new GeoAnnotation(a.getId(), a.getName(), a.getName(), 0, 0, 1);
        } else {
            return null;
        }
    }

    private Vector<GeoAnnotation> getOtherAnnotations(GeoAnnotation annotation) {
        Vector<GeoAnnotation> annotations = new Vector<>();
        if(annotation == null) {
            return null;
        }

        int startIndex = text.indexOf(annotation.getCoveredText());
        if(startIndex == -1) {
            return null;
        }
        annotations.add(new GeoAnnotation(annotation.getGeonameid(), annotation.getName(), annotation.getCoveredText(), startIndex, startIndex + annotation.getCoveredText().length(), 1));
        int nextIndex = startIndex + 1;
        nextIndex = text.indexOf(annotation.getCoveredText(), nextIndex);
        while (nextIndex < text.length() && nextIndex > 0) {
            nextIndex = text.indexOf(annotation.getCoveredText(), nextIndex);
            if (nextIndex == -1) break;
            annotations.add(new GeoAnnotation(annotation.getGeonameid(), annotation.getName(), annotation.getCoveredText(), nextIndex, nextIndex + annotation.getCoveredText().length(), 1));
            nextIndex++;
        }
        return annotations;
    }


}
