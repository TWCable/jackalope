/*
 * Copyright 2014-2016 Time Warner Cable, Inc.
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
import lombok.val;
import org.checkerframework.checker.nullness.qual.Nullable;

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
    public static String head(@Nullable String path) {
        if (path == null) return "";
        path = stripRoot(path);
        return path.contains(SEPARATOR) ? path.substring(0, path.indexOf(SEPARATOR)) : path;
    }


    /**
     * Returns the segments of the path after the first non-root segment.
     *
     * @param path The path
     * @return The segments of the path after the first
     */
    public static String tail(@Nullable String path) {
        if (path == null) return "";
        path = stripRoot(path);
        return path.contains(SEPARATOR) ? path.substring(path.indexOf(SEPARATOR) + 1) : "";
    }


    /**
     * Returns the segments of the path before the last segment
     *
     * @param path The path
     * @return The segments of the path before the last segment
     */
    public static String parent(@Nullable String path) {
        if (path == null) return "/";
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
    public static String basename(@Nullable String path) {
        if (path == null) return "";
        return path.contains(SEPARATOR) ? path.substring(path.lastIndexOf(SEPARATOR) + 1) : path;
    }


    /**
     * Returns true if the first path is an ancestor of the second path
     *
     * @param first  The first path; null is effectively an empty string
     * @param second The second path; null is effectively an empty string
     * @return True if the first path is an ancestor of the second path
     */
    public static boolean ancestorOf(@Nullable String first, @Nullable String second) {
        val firstPath = first != null ? first : "";
        val secondPath = second != null ? second : "";
        val selfOrAncestorOf = selfOrAncestorOf(firstPath, secondPath);
        return selfOrAncestorOf && !firstPath.equals(secondPath);
    }


    /**
     * Returns true if the first path is either the same or an ancestor of the second path
     *
     * @param first  The first path; null is effectively an empty string
     * @param second The second path; null is effectively an empty string
     * @return True if the first path is either the same or an ancestor of the second path
     */
    @SuppressWarnings("RedundantIfStatement")
    public static boolean selfOrAncestorOf(@Nullable String first, @Nullable String second) {
        val firstPath = first != null ? first : "";
        val secondPath = second != null ? second : "";
        if (firstPath.isEmpty()) {
            if (secondPath.isEmpty()) return true;
            else return false;
        }
        else {
            if (secondPath.startsWith(firstPath)) return true;
            else return false;
        }
    }


    /**
     * Returns the number of segments in the path
     *
     * @param path The path
     * @return The number of segments in the path. Returns 0 if the path is empty or the root path.
     */
    public static int depth(@Nullable String path) {
        if (path == null) return 0;
        path = stripRoot(path);
        return !Strings.isNullOrEmpty(path) ? path.split(SEPARATOR).length : 0;
    }


    /**
     * Returns true it the path is an absolute path (i.e. starts with '/')
     *
     * @param path The path; null is treated as an empty string
     * @return True it the path is an absolute path (i.e. starts with '/')
     */
    public static boolean isAbsolute(@Nullable String path) {
        return path != null && path.startsWith("/");
    }


    /**
     * Returns true it the path is the root path (i.e. '/')
     *
     * @param path The path; null is treated as an empty string
     * @return True it the path is the root path (i.e. '/')
     */
    public static boolean isRoot(@Nullable String path) {
        return path != null && path.equals("/");
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
    public static String resolve(@Nullable String first, @Nullable String second) {
        val firstPath = first != null ? first : "";
        val secondPath = second != null ? second : "";
        if (isAbsolute(secondPath)) return secondPath;
        else if (firstPath.isEmpty() || isRoot(firstPath)) return first + secondPath;
        else return firstPath + SEPARATOR + secondPath;
    }


    /**
     * Returns the path with the root element removed
     *
     * @param path The path to be modified
     * @return The path with the root element removed.
     */
    private static String stripRoot(@Nullable String path) {
        if (path == null) return "";
        return isAbsolute(path) ? path.substring(1) : path;
    }
}
