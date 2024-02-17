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
import java.util.List;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;

/**
 *
 * @author antons
 */
@Mojo(name = "list", defaultPhase = LifecyclePhase.PROCESS_RESOURCES,
    requiresDependencyCollection = ResolutionScope.COMPILE_PLUS_RUNTIME,
    requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME)
public class ListMojo extends AbstractSizeMojo {

    public void execute() throws MojoExecutionException {

        SizeNode root = buildTree();
        List<String> ids = new ArrayList<>();
        List<SizeNode> nodes = new ArrayList<>();
        traverse(root, ids, nodes);
        Collections.sort(nodes, "true".equals(netto) ? SizeNode.SizeComparator.instance() : SizeNode.CumulativeSizeComparator.instance());
        getLog().info("-------------------------------------------");
        displayNode(nodes);
    }

    private void traverse(SizeNode node, List<String> ids, List<SizeNode> nodes) {
        if(node == null) return;
        String id = node.node().getArtifact().getId();
        if(!ids.contains(id)) {
            ids.add(id);
            nodes.add(node);
        }
        for(SizeNode child : node.children()) {
            traverse(child, ids, nodes);
        }
    }
    private void displayNode(List<SizeNode> nodes) {
        for(SizeNode node : nodes) {
            getLog().info(node.node().getArtifact().getId() + " <" + node.sizeAsString()+ ", " + node.cumulatedSizeAsString()+ ">");
        }
    }
}
