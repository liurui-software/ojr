/*
 * Copyright The OpenTelemetry Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package com.ojr.core.resources;

import com.google.errorprone.annotations.MustBeClosed;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.sdk.resources.Resource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Factory for {@link Resource} retrieving Container ID information. It supports both cgroup v1 and
 * v2 runtimes.
 */
public final class ContainerResource {
    // copied from ContainerIncubatingAttributes
    private static final AttributeKey<String> CONTAINER_ID = AttributeKey.stringKey("container.id");

    static final Filesystem FILESYSTEM_INSTANCE = new Filesystem();

    private final CgroupV1ContainerIdExtractor v1Extractor;
    private final CgroupV2ContainerIdExtractor v2Extractor;

    public ContainerResource() {
        this(new CgroupV1ContainerIdExtractor(), new CgroupV2ContainerIdExtractor());
    }

    // Visible for testing
    ContainerResource(
            CgroupV1ContainerIdExtractor v1Extractor, CgroupV2ContainerIdExtractor v2Extractor) {
        this.v1Extractor = v1Extractor;
        this.v2Extractor = v2Extractor;
    }


    public Optional<String> getContainerId() {
        Optional<String> v1Result = v1Extractor.extractContainerId();
        if (v1Result.isPresent()) {
            return v1Result;
        }
        return v2Extractor.extractContainerId();
    }

     // Exists for testing
    static class Filesystem {
        boolean isReadable(Path path) {
            return Files.isReadable(path);
        }

        @MustBeClosed
        Stream<String> lines(Path path) throws IOException {
            return Files.lines(path);
        }

        List<String> lineList(Path path) throws IOException {
            try (Stream<String> lines = lines(path)) {
                return lines.collect(Collectors.toList());
            }
        }
    }
}
