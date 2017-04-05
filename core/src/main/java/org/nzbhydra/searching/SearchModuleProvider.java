package org.nzbhydra.searching;

import org.nzbhydra.config.IndexerConfig;
import org.nzbhydra.config.SearchModuleType;
import org.nzbhydra.database.IndexerEntity;
import org.nzbhydra.database.IndexerRepository;
import org.nzbhydra.database.IndexerStatusEntity;
import org.nzbhydra.indexers.Indexer;
import org.nzbhydra.indexers.Newznab;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class SearchModuleProvider {

    private static final Logger logger = LoggerFactory.getLogger(SearchModuleProvider.class);

    private static final Map<SearchModuleType, Class<? extends Indexer>> searchModuleClasses = new HashMap<>();

    static {
        searchModuleClasses.put(SearchModuleType.NEWZNAB, Newznab.class);
    }

    @Autowired
    private AutowireCapableBeanFactory beanFactory;

    @Autowired
    private IndexerRepository indexerRepository;

    @Autowired
    private SearchModuleConfigProvider searchModuleConfigProvider;

    private Map<String, Indexer> searchModuleInstances = new HashMap<>();


    public List<Indexer> getIndexers() {
        return new ArrayList<>(searchModuleInstances.values());
    }

    public List<Indexer> getEnabledIndexers() {
        return searchModuleInstances.values().stream().filter(x -> x.getConfig().isEnabled()).collect(Collectors.toList());
    }


    /**
     * Must be called by <tt>{@link SearchModuleConfigProvider}</tt> when config is loaded.
     */
    public void loadIndexers() {
        if (searchModuleConfigProvider.getIndexers() == null) {
            logger.error("Indexers not set. Check your configuration");
            return;
        }
        logger.info("Loading indexers");
        searchModuleInstances.clear();
        for (IndexerConfig config : searchModuleConfigProvider.getIndexers()) {
            try {
                Indexer searchModule = beanFactory.createBean(SearchModuleProvider.searchModuleClasses.get(config.getSearchModuleType()));
                logger.info("Found indexer {}", config.getName());

                IndexerEntity indexerEntity = indexerRepository.findByName(config.getName());
                if (indexerEntity == null) {
                    logger.info("Indexer with name {} not yet in database. Adding it", config.getName());
                    indexerEntity = new IndexerEntity();
                    indexerEntity.setName(config.getName());
                    indexerEntity.setStatus(new IndexerStatusEntity());
                    indexerEntity = indexerRepository.save(indexerEntity);
                    logger.info("Now {} indexers in database", indexerRepository.count());
                }

                searchModule.initialize(config, indexerEntity);
                searchModuleInstances.put(config.getName(), searchModule);
            } catch (Exception e) {
                logger.error("Unable to instantiate indexer with name {} and type {}", config.getName(), config.getSearchModuleType(), e);
            }
        }
        if (searchModuleInstances.isEmpty()) {
            logger.warn("No indexers configured");
        }
    }
}
