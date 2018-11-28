package com.agileengine;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


public class SmartXmlAnalyzer {

    private static Logger LOGGER = LoggerFactory.getLogger(SmartXmlAnalyzer.class);

    private static String CHARSET_NAME = "utf8";

    public static void main(String[] args) {
        String originResourcePath = args[0];
        String targetResourcePath = args[1];

        String targetElementId;
        try {
            targetElementId = args[3];
        } catch (Exception e) {
            targetElementId = "make-everything-ok-button";
        }

        Map<Element, MatchResult> rankedElements = new HashMap<>();

        List<Attribute> attributes = findElementById(new File(originResourcePath), targetElementId)
                .map(button -> button.attributes().asList())
                .orElseThrow(() -> new IllegalArgumentException("Nothing found by provided id"));

        Document doc;
        File targetHtmlFile = new File(targetResourcePath);
        try {

            doc = Jsoup.parse(
                    targetHtmlFile,
                    CHARSET_NAME,
                    targetHtmlFile.getAbsolutePath());

        } catch (IOException e) {
            LOGGER.error("Error reading [{}] file", targetHtmlFile.getAbsolutePath(), e);
            return;
        }

        attributes.forEach(attribute -> {
            findElementsByQueryFromDoc(doc,"[" + attribute.getKey() + "=\"" + attribute.getValue() + "\"]")
            .ifPresent(elements -> rankElements(rankedElements, elements, attribute));
        });

        rankedElements.values().stream()
                .max(new MatchResultComparator())
                .ifPresent(
                        val -> {
                            rankedElements.entrySet().stream()
                                    .filter(entry -> val == entry.getValue())
                                    .findFirst()
                                    .ifPresent(matchedEntry -> {
                                        System.out.println(matchedEntry.getKey().cssSelector());
                                        System.out.println("Attributes contributed to score:" + matchedEntry.getValue().getMatchedAttributes());
                                    });
                        }
        );
    }

    private static Optional<Element> findElementById(File htmlFile, String targetElementId) {
        try {
            Document doc = Jsoup.parse(
                    htmlFile,
                    CHARSET_NAME,
                    htmlFile.getAbsolutePath());

            return Optional.of(doc.getElementById(targetElementId));

        } catch (IOException e) {
            LOGGER.error("Error reading [{}] file", htmlFile.getAbsolutePath(), e);
            return Optional.empty();
        }
    }

    private static Optional<Elements> findElementsByQueryFromDoc(Document doc, String cssQuery) {
            return Optional.of(doc.select(cssQuery));
    }

    private static void rankElements(Map<Element, MatchResult> rankedElements, Elements elements, Attribute attribute) {
        elements.forEach(element -> {
            if (rankedElements.get(element) != null) {
                rankedElements.get(element).setScore(rankedElements.get(element).getScore() + 1);
                rankedElements.get(element).getMatchedAttributes().add(attribute.toString());
            } else {
                rankedElements.put(element, new MatchResult(attribute.toString()));
            }
        });
    }
}