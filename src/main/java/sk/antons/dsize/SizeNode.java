/*
 * Copyright 2024 Anton Straka
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package sk.antons.dsize;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.apache.maven.shared.dependency.graph.DependencyNode;

/**
 *
 * @author antons
 */
public class SizeNode {
    private DependencyNode node;
    private int size;
    private int cumulatedSize;


    private SizeNode parent = null;
    private List<SizeNode> children = new ArrayList<>();

    public int size() { return size; }
    public String sizeAsString() { return numAsString(size); }
    public SizeNode size(int value) { this.size = value; return this; }
    public int cumulatedSize() { return cumulatedSize; }
    public String cumulatedSizeAsString() { return numAsString(cumulatedSize); }
    public DependencyNode node() { return node; }
    public SizeNode parent() { return parent; }
    public List<SizeNode> children() { return children; }


    protected void calculateCumulatedSize() {
        int sum = 0;
        for(SizeNode sizeNode : children) {
            sizeNode.calculateCumulatedSize();
            sum = sum + sizeNode.cumulatedSize;
        }
        this.cumulatedSize = sum + size;
    }

    public static SizeNode instance(DependencyNode node) {
        SizeNode rv = new SizeNode();
        rv.node = node;
        return rv;
    }

    public SizeNode addChild(SizeNode child) {
        if(child != null) {
            child.parent = this;
            this.children.add(child);
        }
        return this;
    }

    private static String numAsString(int size) {
//        if(size > 1000000-1) return size /10000000 + "MB";
//        if(size > 1000-1) return size /1000 + "KB";
//        return size + "";
        String num = "" + size;
        StringBuilder sb = new StringBuilder();
        int len = num.length();
        for(int i = 0; i < len; i++) {
            char c = num.charAt(i);
            int rest = len - i;
            if(i == 0) ;
            else if(rest == 3) sb.append('.');
            else if(rest == 6) sb.append('.');
            sb.append(c);
        }
        return sb.toString();
    }

    public void sort(Comparator<SizeNode> comparator) {
        Collections.sort(children, comparator);
        for(SizeNode sizeNode : children) {
            sizeNode.sort(comparator);
        }
    }

    public static class SizeComparator implements Comparator<SizeNode> {

        @Override
        public int compare(SizeNode t1, SizeNode t2) {
            int num1 = t1 == null ? 0 : t1.size;
            int num2 = t2 == null ? 0 : t2.size;
            return num2 - num1;
        }
        public static SizeComparator instance() { return new SizeComparator(); }

    }

    public static class CumulativeSizeComparator implements Comparator<SizeNode> {

        @Override
        public int compare(SizeNode t1, SizeNode t2) {
            int num1 = t1 == null ? 0 : t1.cumulatedSize;
            int num2 = t2 == null ? 0 : t2.cumulatedSize;
            return num2 - num1;
        }
        public static CumulativeSizeComparator instance() { return new CumulativeSizeComparator(); }

    }


}
