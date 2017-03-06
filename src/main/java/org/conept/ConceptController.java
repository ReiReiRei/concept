package org.conept;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.Vector;

import static org.conept.Annotator.AnnotatorType.altername;
import static org.conept.Annotator.AnnotatorType.lowercase;

class IndexLoader {
    private ClassLoader classLoader = getClass().getClassLoader();
    private File file = new File("cities15000.txt");
    private final ConceptIndex index = new ConceptIndex(file.toPath());

    IndexLoader() throws IOException {
    }


    public ConceptIndex getIndex() {
        return index;
    }
}

@Controller
@EnableAutoConfiguration
public class ConceptController {

    private ConceptIndex index;

    @PostConstruct
    public void init() {
        try {
            index = (new IndexLoader()).getIndex();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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
