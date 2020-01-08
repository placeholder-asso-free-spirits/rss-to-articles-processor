package fr.asso.placeholder.data.processors.rss.models;

import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@Builder
public class Article {
    private String id;
    private List<String> categories;
    private String extract;
    private List<String> contributors;
    private String description;
    private String enclosure;
    private String link;
    private Date publishedDate;
    private String source;
    private String title;
    private Date updatedDate;
    private String uri;
    private String publisher;
    private String creator;
    private String rights;
    private String identifier;
}
