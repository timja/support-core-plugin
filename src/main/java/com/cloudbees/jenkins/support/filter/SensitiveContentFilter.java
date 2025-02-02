/*
 * The MIT License
 *
 * Copyright (c) 2018, CloudBees, Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.cloudbees.jenkins.support.filter;

import com.cloudbees.jenkins.support.util.WordReplacer;
import hudson.Extension;
import hudson.ExtensionList;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.accmod.Restricted;
import org.kohsuke.accmod.restrictions.NoExternalUse;

import edu.umd.cs.findbugs.annotations.NonNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Filters contents based on names provided by all {@linkplain NameProvider known sources}.
 *
 * @see NameProvider
 * @since TODO
 */
@Extension
@Restricted(NoExternalUse.class)
public class SensitiveContentFilter implements ContentFilter {

    public static SensitiveContentFilter get() {
        return ExtensionList.lookupSingleton(SensitiveContentFilter.class);
    }

    @Override
    public @NonNull String filter(@NonNull String input) {
        ContentMappings mappings = ContentMappings.get();
        String filtered = input;
        List<String> searchList = new ArrayList<>();
        List<String> replacementList = new ArrayList<>();

        for (ContentMapping mapping : mappings) {
            searchList.add(mapping.getOriginal());
            replacementList.add(mapping.getReplacement());
        }
        if (!searchList.isEmpty()) {
            filtered = WordReplacer.replaceWordsIgnoreCase(input, searchList.toArray(new String[0]), replacementList.toArray(new String[0]));
        }

        return filtered;
    }

    @Override
    public synchronized void reload() {
        ContentMappings mappings = ContentMappings.get();
        Set<String> stopWords = mappings.getStopWords();
        for (NameProvider provider : NameProvider.all()) {
            provider.names()
                    .filter(name -> StringUtils.isNotBlank(name) && !stopWords.contains(name.toLowerCase(Locale.ENGLISH)))
                    .forEach(name -> mappings.getMappingOrCreate(name, original -> ContentMapping.of(original, provider.generateFake())));
        }
    }

}
