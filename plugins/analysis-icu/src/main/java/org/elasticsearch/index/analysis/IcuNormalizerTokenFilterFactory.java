/*
 * Copyright Elasticsearch B.V. and/or licensed to Elasticsearch B.V. under one
 * or more contributor license agreements. Licensed under the Elastic License
 * 2.0 and the Server Side Public License, v 1; you may not use this file except
 * in compliance with, at your election, the Elastic License 2.0 or the Server
 * Side Public License, v 1.
 */

package org.elasticsearch.index.analysis;

import com.ibm.icu.text.FilteredNormalizer2;
import com.ibm.icu.text.Normalizer2;
import com.ibm.icu.text.UnicodeSet;

import org.apache.lucene.analysis.TokenStream;
import org.elasticsearch.Version;
import org.elasticsearch.common.logging.DeprecationCategory;
import org.elasticsearch.common.logging.DeprecationLogger;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.IndexSettings;

/**
 * Uses the {@link org.apache.lucene.analysis.icu.ICUNormalizer2Filter} to normalize tokens.
 * <p>The {@code name} can be used to provide the type of normalization to perform.</p>
 * <p>The {@code unicodeSetFilter} attribute can be used to provide the UniCodeSet for filtering.</p>
 */
public class IcuNormalizerTokenFilterFactory extends AbstractTokenFilterFactory implements NormalizingTokenFilterFactory {

    private static final DeprecationLogger deprecationLogger = DeprecationLogger.getLogger(IcuNormalizerTokenFilterFactory.class);

    private final Normalizer2 normalizer;

    public IcuNormalizerTokenFilterFactory(IndexSettings indexSettings, Environment environment, String name, Settings settings) {
        super(indexSettings, name, settings);
        String method = settings.get("name", "nfkc_cf");
        Normalizer2 normalizer = Normalizer2.getInstance(null, method, Normalizer2.Mode.COMPOSE);
        this.normalizer = wrapWithUnicodeSetFilter(indexSettings, normalizer, settings);
    }

    @Override
    public TokenStream create(TokenStream tokenStream) {
        return new org.apache.lucene.analysis.icu.ICUNormalizer2Filter(tokenStream, normalizer);
    }

    static Normalizer2 wrapWithUnicodeSetFilter(final IndexSettings indexSettings, final Normalizer2 normalizer, final Settings settings) {
        String unicodeSetFilter = settings.get("unicodeSetFilter");
        if (indexSettings.getIndexVersionCreated().onOrAfter(Version.V_7_0_0)) {
            if (unicodeSetFilter != null) {
                deprecationLogger.critical(
                    DeprecationCategory.ANALYSIS,
                    "icu_normalizer_unicode_set_filter",
                    "[unicodeSetFilter] has been deprecated in favor of [unicode_set_filter]"
                );
            } else {
                unicodeSetFilter = settings.get("unicode_set_filter");
            }
        }
        if (unicodeSetFilter != null) {
            UnicodeSet unicodeSet = new UnicodeSet(unicodeSetFilter);

            unicodeSet.freeze();
            return new FilteredNormalizer2(normalizer, unicodeSet);
        }
        return normalizer;
    }
}
