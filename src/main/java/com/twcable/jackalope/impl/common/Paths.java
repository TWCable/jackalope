/*
 * Copyright 2015 Time Warner Cable, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.twcable.jackalope.impl.common;

import com.google.common.base.Strings;

/**
 * Utilities for manipulating path strings.
 */
public final class Paths {
    public final static String SEPARATOR = "/";


    private Paths() {
    }


    /**
     * Returns the first non-root segment of the path.
     *
     * @param path The path
     * @return The first segment of the path
     */
    public static String head(String path) {
        path = stripRoot(path);
        return path.contains(SEPARATOR) ? path.substring(0, path.indexOf(SEPARATOR)) : path;
    }


    /**
     * Returns the segments of the path after the first non-root segment.
     *
     * @param path The path
     * @return The segments of the path after the first
     */
    public static String tail(String path) {
        path = stripRoot(path);
        return path.contains(SEPARATOR) ? path.substring(path.indexOf(SEPARATOR) + 1) : "";
    }


    /**
     * Returns the segments of the path before the last segment
     *
     * @param path The path
     * @return The segments of the path before the last segment
     */
    public static String parent(String path) {
        String prefix = isAbsolute(path) && !isRoot(path) ? "/" : "";
        path = stripRoot(path);
        return prefix + (path.contains(SEPARATOR) ? path.substring(0, path.lastIndexOf(SEPARATOR)) : "");
    }


    /**
     * Returns the last non-root segment of the path
     *
     * @param path The path
     * @return The last segment of the path
     */
    public static String basename(String path) {
        return path.contains(SEPARATOR) ? path.substring(path.lastIndexOf(SEPARATOR) + 1) : path;
    }


    /**
     * Returns true if the first path is an ancestor of the second path
     *
     * @param first  The first path
     * @param second The second path
     * @return True if the first path is an ancestor of the second path
     */
    public static boolean ancestorOf(String first, String second) {
        return !Strings.isNullOrEmpty(first) && !first.equals(second) && second.startsWith(first);
    }


    /**
     * Returns true if the first path is an ancestor of the second path
     *
     * @param first  The first path
     * @param second The second path
     * @return True if the first path is an ancestor of the second path
     */
    public static boolean selfOrAncestorOf(String first, String second) {
        return (!Strings.isNullOrEmpty(first) && first.equals(second)) || ancestorOf(first, second);
    }


    /**
     * Returns the number of segments in the path
     *
     * @param path The path
     * @return The number of segments in the path. Returns 0 if the path is empty or the root path.
     */
    public static int depth(String path) {
        path = stripRoot(path);
        return !Strings.isNullOrEmpty(path) ? path.split(SEPARATOR).length : 0;
    }


    /**
     * Returns true it the path is an absolute path (i.e. starts with '/')
     *
     * @param path The path
     * @return True it the path is an absolute path (i.e. starts with '/')
     */
    public static boolean isAbsolute(String path) {
        return path.startsWith("/");
    }


    /**
     * Returns true it the path is the root path (i.e. '/')
     *
     * @param path The path
     * @return True it the path is the root path (i.e. '/')
     */
    public static boolean isRoot(String path) {
        return path.equals("/");
    }


    /**
     * Resolves the second path using the first path as context.
     *
     * @param first  The path to be used as the context
     * @param second The target path
     * @return If the second path is absolute, it is returned.  Otherwise, the return path is constructed by appending
     * the second (child) path to the first (parent) path.
     */
    //TODO: Normalize the paths
    public static String resolve(String first, String second) {
        return isAbsolute(second) ? second :
            Strings.isNullOrEmpty(first) || isRoot(first) ? first + second :
                first + SEPARATOR + second;
    }


    /**
     * Returns the path with the root element removed
     *
     * @param path The path to be modified
     * @return The path with the root element removed.
     */
    private static String stripRoot(String path) {
        return isAbsolute(path) ? path.substring(1) : path;
    }

    /**
     * Strip a trailing '/' on a path.
     *
     * Items saved in the JCR can be accessed as both /some/path and
     * /some/path/ but the standard output is w/o the trailing '/'.
     *
     * Note that the root path '/' is preserved as '/'.
     *
     * @param path The path to be modified
     * @return The path without a trailing '/' if one is found.
     */
    public static String stripTrailingSeparator(String path) {
        if (path != null && !path.equals(SEPARATOR)) {
            if (path.lastIndexOf(SEPARATOR) == (path.length() - 1)) {
                return path.substring(0, path.length() - 1);
            }
        }
        return path;
    }
}
