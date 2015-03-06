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

package com.twcable.jackalope.impl.jcr;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import java.util.Collection;

class NodeIteratorImpl extends RangeIteratorImpl<Node> implements NodeIterator {
    NodeIteratorImpl(Collection<Node> nodes) {
        super(nodes);
    }


    @Override
    public Node nextNode() {
        return (Node)next();
    }
}
