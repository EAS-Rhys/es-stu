/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0 and the Server Side Public License, v 1; you may not use this file except
 * in compliance with, at your election, the Elastic License 2.0 or the Server
 * Side Public License, v 1.
 */

package org.elasticsearch.upgrades;

import org.elasticsearch.test.AbstractXContentTestCase;
import org.elasticsearch.xcontent.NamedXContentRegistry;
import org.elasticsearch.xcontent.XContentParser;

import java.io.IOException;

public class SystemIndexMigrationTaskStateXContentTests extends AbstractXContentTestCase<SystemIndexMigrationTaskState> {

    @Override
    protected SystemIndexMigrationTaskState createTestInstance() {
        return SystemIndexMigrationTaskStateTests.randomSystemIndexMigrationTask();
    }

    @Override
    protected SystemIndexMigrationTaskState doParseInstance(XContentParser parser) throws IOException {
        return SystemIndexMigrationTaskState.PARSER.parse(parser, null);
    }

    @Override
    protected boolean supportsUnknownFields() {
        return false;
    }

    @Override
    protected NamedXContentRegistry xContentRegistry() {
        return new NamedXContentRegistry(SystemIndexMigrationExecutor.getNamedXContentParsers());
    }
}
