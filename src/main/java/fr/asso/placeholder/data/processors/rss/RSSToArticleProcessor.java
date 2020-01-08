package fr.asso.placeholder.data.processors.rss;

import fr.asso.placeholder.data.processors.rss.models.Article;
import fr.asso.placeholder.data.sources.rss.models.DublinCoreModule;
import fr.asso.placeholder.data.sources.rss.models.RSSCategory;
import fr.asso.placeholder.data.sources.rss.models.RSSContent;
import fr.asso.placeholder.data.sources.rss.models.RSSEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Processor;
import org.springframework.messaging.handler.annotation.SendTo;

import java.util.List;
import java.util.stream.Collectors;

@SpringBootApplication
@EnableBinding(Processor.class)
public class RSSToArticleProcessor {

    private static Logger LOGGER = LoggerFactory.getLogger(RSSToArticleProcessor.class.getName());

    public static void main(String[] args) {
        SpringApplication.run(RSSToArticleProcessor.class, args);
    }

    private String extractRights(List<DublinCoreModule> modules) {
        List<DublinCoreModule> founds = modules.stream()
                .filter(module -> module.getRights() != null)
                .collect(Collectors.toList());
        if (!founds.isEmpty()) {
            return founds.get(0).getRights();
        }
        return null;
    }

    private String extractPublisher(List<DublinCoreModule> modules) {
        List<DublinCoreModule> founds = modules.stream()
                .filter(module -> module.getPublisher() != null)
                .collect(Collectors.toList());
        if (!founds.isEmpty()) {
            return founds.get(0).getPublisher();
        }
        return null;
    }

    private String extractCreator(List<DublinCoreModule> modules) {
        List<DublinCoreModule> founds = modules.stream()
                .filter(module -> module.getCreator() != null)
                .collect(Collectors.toList());
        if (!founds.isEmpty()) {
            return founds.get(0).getCreator();
        }
        return null;
    }

    private String extractIdentifier(List<DublinCoreModule> modules) {
        List<DublinCoreModule> founds = modules.stream()
                .filter(module -> module.getIdentifier() != null)
                .collect(Collectors.toList());
        if (!founds.isEmpty()) {
            return founds.get(0).getIdentifier();
        }
        return null;
    }

    @StreamListener(Processor.INPUT)
    @SendTo(Processor.OUTPUT)
    public Article process(RSSEntry entry) {
        LOGGER.info("Handling entry {}", entry.getUri());
        return Article.builder()
                .title(entry.getTitle())
                .categories(entry.getCategories().stream().map(RSSCategory::getName).collect(Collectors.toList()))
                .contributors(entry.getContributors().stream().map(RSSContent::getValue).collect(Collectors.toList()))
                .description(entry.getDescription().getValue())
                .link(entry.getLink())
                .source(entry.getSource())
                .updatedDate(entry.getUpdatedDate())
                .uri(entry.getUri())
                .enclosure(entry.getEnclosures().isEmpty() ? entry.getEnclosures().get(0).getValue() : null)
                .publishedDate(entry.getPublishedDate())
                .creator(extractCreator(entry.getModules()))
                .publisher(extractPublisher(entry.getModules()))
                .rights(extractRights(entry.getModules()))
                .identifier(extractIdentifier(entry.getModules()))
                .extract(entry.getContents().stream().reduce("", (partialContent, rssContent2) -> partialContent.concat(rssContent2.getValue()), String::concat))
                .build();
    }
}
