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

import hudson.Extension;
import hudson.ExtensionList;
import hudson.ExtensionPoint;
import org.kohsuke.accmod.Restricted;
import org.kohsuke.accmod.restrictions.NoExternalUse;

import edu.umd.cs.findbugs.annotations.NonNull;
import java.util.Locale;
import java.util.function.Function;
import java.util.function.Supplier;
import org.kohsuke.randname.RandomNameGenerator;

/**
 * Provides a way to generate random names.
 *
 * @since TODO
 */
@Extension
@Restricted(NoExternalUse.class)
public class DataFaker implements ExtensionPoint, Function<Function<String, String>, Supplier<String>> {

    /**
     * @return the singleton instance
     */
    public static DataFaker get() {
        return ExtensionList.lookupSingleton(DataFaker.class);
    }

    private final RandomNameGenerator generator = new RandomNameGenerator();

    /**
     * Applies the provided function to a random name and normalizes the result.
     */
    @Override
    public Supplier<String> apply(@NonNull Function<String, String> nameTransformer) {
        return () -> nameTransformer.apply(generator.next()).toLowerCase(Locale.ENGLISH).replace(' ', '_');
    }

}
