/*
 * Copyright 2014 jerdct.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.genesis.utils.filters;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * 
 * Simple chain implementation.
 *
 * @author jerdct
 * @param <I>
 * @since 0.1
 */
public class BasicChain<I> implements Chain<I> {

    private final LinkedList<Filter<I>> chain;

    /**
     * Basically the constrctor.
     */
    public BasicChain() {
        chain = new LinkedList<>();
    }

    @Override
    public Chain<I> add(Filter<I> filter) {
        chain.add(filter);
        return this;
    }

    @Override
    public Chain<I> addFirst(Filter<I> filter) {
        chain.addFirst(filter);
        return this;
    }

    @Override
    public Chain<I> addLast(Filter<I> filter) {
        chain.addLast(filter);
        return this;
    }

    @Override
    public Chain<I> addBefore(Filter<I> filter, Filter<I> referenceElement) {
        int index = chain.indexOf(referenceElement);
        if (index != -1) {
            chain.add(index, filter);
        } else {
            addLast(filter);
        }
        return this;
    }

    @Override
    public Chain<I> addAfter(Filter<I> filter, Filter<I> referenceElement) {
        int index = chain.indexOf(referenceElement);
        if (index != -1) {
            chain.add(index + 1, filter);
        } else {
            addLast(filter);
        }
        return this;
    }

    @Override
    public Chain<I> removeFirst() {
        chain.pollFirst();
        return this;
    }

    @Override
    public Chain<I> removeLast() {
        chain.pollLast();
        return this;
    }

    @Override
    public Chain<I> remove(int index) {
        if (index > -1 && index < chain.size()) {
            chain.remove(index);
        }
        return this;
    }

    @Override
    public void run(ChainBehavior behavior, I input) {
        if (behavior != null) {
            switch (behavior) {
                case IGNORE_FAILURE:
                    doRun((FilterResult res) -> {
                        return true;
                    }, input);
                    break;
                case STOP_ON_FAILURE:
                    doRun((FilterResult res) -> {
                        return res.isSuccess();
                    }, input);
                    break;
                default:
                    break;
            }
        }
    }

    private void doRun(Function<FilterResult, Boolean> Continuefunction, I input) {
        Filter<I> element;
        while ((element = chain.poll()) != null) {
            if (!Continuefunction.apply(element.doIt(input))) {
                element.onFailure(input);
                break;
            }
        }

    }

    @Override
    public List<Filter<I>> toList() {
        return chain;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + Objects.hashCode(this.chain);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final BasicChain<?> other = (BasicChain<?>) obj;
        if (!Objects.equals(this.chain, other.chain)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "BasicSynchChain{" + "chain=" + chain + '}';
    }

    
    
}
