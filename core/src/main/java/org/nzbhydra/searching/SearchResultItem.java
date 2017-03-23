package org.nzbhydra.searching;

import com.google.common.base.MoreObjects;
import lombok.Data;
import org.nzbhydra.searching.searchmodules.AbstractIndexer;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Data
public class SearchResultItem implements Comparable<SearchResultItem> {

    private boolean agePrecise;
    private Map<String, String> attributes = new HashMap<>();
    private String description;
    private String details;
    private Instant firstFound;
    private String group;
    private Integer guid;
    private AbstractIndexer indexer;
    private String indexerGuid;
    private Integer indexerScore;
    private String link;
    private String poster;
    private Instant pubDate;
    private Integer searchResultId;
    private Long size;
    private String title;
    private Instant usenetDate;

    public Optional<Instant> getUsenetDate() {
        return Optional.ofNullable(usenetDate);
    }

    @Override
    public int compareTo(SearchResultItem o) {
        return o.getPubDate().compareTo(pubDate);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("guid", guid)
                .add("indexerName", indexer.getName())
                .add("title", title)
                .add("pubDate", pubDate)
                .add("size", size)
                .toString();
    }
}
