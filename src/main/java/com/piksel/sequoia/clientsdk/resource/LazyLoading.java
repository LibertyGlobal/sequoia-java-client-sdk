package com.piksel.sequoia.clientsdk.resource;

import static java.util.Objects.nonNull;

import com.google.gson.JsonElement;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Supplier;
import lombok.extern.slf4j.Slf4j;

@Slf4j
abstract class LazyLoading<T extends Resource> {

    static final int FIRST_PAGE = 1;

    // just to provide easier way to navigate back and forth in future
    final Map<Integer, Page<T>> pages = new HashMap<>();

    PageableResourceEndpoint<T> endpoint;

    int pageIndex;
    int resourceIndex = 0;
    Integer totalCount;

    ResourceDeserializer<T> deserializer;
    
    T next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        if (!currentPage().containsIndex(resourceIndex)) {
            loadNextAndUpdateIndexes();
        }
        try {
            return currentPage().at(resourceIndex++);
        } catch (Page.PageResourceDoesNotExist pne) {
            throw new NoSuchElementException(pne.getMessage());
        }
    }
    
   
    boolean hasNext() {
        return theCurrentPageHasContents() &&
            ((currentPage().containsIndex(resourceIndex) || currentPage().isNotLast()) && nextPageIsNotEmpty()
                && nextPageContainsResources());
    }

    abstract boolean theCurrentPageHasContents();

    abstract boolean nextPageContainsResources();


    Integer getTotalCount(JsonElement payload) {
        Meta meta = deserializer.metaFrom(payload).orElse(deserializer.emptyMeta());
        return meta.getTotalCount();
    }

    Page<T> currentPage() {
        return pages.get(pageIndex);
    }

    Optional<Integer> totalCount() {
        return Optional.ofNullable(totalCount);
    }

    Supplier<NoSuchElementException> noSuchElementException() {
        return NoSuchElementException::new;
    }

    boolean nextPageIsNotEmpty() {
        if (lastPageItem(currentPage().getMeta()) && metaHasNext()) {
            loadNextAndUpdateIndexes();
            return (currentPage().items() > 0);
        }
        return true;
    }

    abstract void loadNextAndUpdateIndexes();

    boolean metaHasNext() {
        return nonNull(currentPage().getMeta().getNext());
    }
    
    int addPage(JsonElement payload) {
        long startTime = System.nanoTime();
        Meta meta = deserializer.metaFrom(payload).orElse(deserializer.emptyMeta());
        pages.put(meta.getPage(), Page.from(meta, deserializer.contentsFrom(payload)));
        long endTime = System.nanoTime();
        log.debug("time to process json - {} seconds", (double) (endTime - startTime) / 1000000000.0);
        return meta.getPage();
    }

    boolean lastPageItem(AbstractMeta meta) {
        return resourceIndex == meta.getPerPage();
    }

}
