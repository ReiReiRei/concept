package org.conept;

import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;
import org.springframework.stereotype.*;
import org.springframework.web.bind.annotation.*;

import java.util.Vector;

import static org.conept.Annotator.AnnotatorType.altername;
import static org.conept.Annotator.AnnotatorType.exectly;
import static org.conept.Annotator.AnnotatorType.lowercase;

@Controller
@EnableAutoConfiguration
public class ConceptController {

    private final static ConceptIndex index = new ConceptIndex("C:\\education\\projects\\concept\\src\\main\\resources\\cities15000.txt");

    @RequestMapping("/")
    @ResponseBody
    String home() {
        return new Integer(index.getIdIndex().size()).toString();
    }

    String toXML(Vector<GeoAnnotation> annotations) {
        StringBuilder builder = new StringBuilder();
        builder.append("<matches>\n");
        annotations.forEach(ann -> {
            builder.append("<match>\n<geonameid>" + ann.getGeonameid() + "</geonameid>\n<name>" + ann.getName() + "</name>\n<covered_text>" + ann.getCoveredText() + "</covered_text>\n<start>" + ann.getStart() + "</start>\n<end>" + ann.getEnd() + "</end>\n" + "<score>" + ann.getScore() + "</score>\n</match>");
        });
        builder.append("</matches>");
        return builder.toString();
    }

    @RequestMapping(value = "/lowercase")
    @ResponseBody
    public String annotationLowercase(@RequestParam(value = "text", defaultValue = "") String text) {

        Annotator annotator = new Annotator(index, lowercase, text);
        Vector<GeoAnnotation> annotations = annotator.annotate();
        return toXML(annotations);
    }

    @RequestMapping(value = "/alternames")
    @ResponseBody
    public String annotationAlternames(@RequestParam(value = "text", defaultValue = "") String text) {

        Annotator annotator = new Annotator(index, altername, text);
        Vector<GeoAnnotation> annotations = annotator.annotate();

        return toXML(annotations);
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(ConceptController.class, args);
    }
}
