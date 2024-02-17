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

import java.util.List;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;

/**
 *
 * @author antons
 */
@Mojo(name = "tree", defaultPhase = LifecyclePhase.PROCESS_RESOURCES,
    requiresDependencyCollection = ResolutionScope.COMPILE_PLUS_RUNTIME,
    requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME)
public class TreeMojo extends AbstractSizeMojo {

    public void execute() throws MojoExecutionException {

        SizeNode root = buildTree();
        root.sort("true".equals(netto) ? SizeNode.SizeComparator.instance() : SizeNode.CumulativeSizeComparator.instance());
        getLog().info("-------------------------------------------");
        displayNode(root, "", null);
    }

    private void displayNode(SizeNode node, String prefix, Boolean lastone) {
        String newprefix = "";
        String pref = "";
        if(lastone != null) {
            newprefix = prefix + (lastone ? "   ":"|  ");
            pref = lastone ? "+- " : "+- ";
        }

        getLog().info(prefix + pref + node.node().getArtifact().getId() + " <" + node.sizeAsString()+ ", " + node.cumulatedSizeAsString()+ ">");
        List<SizeNode> children = node.children();
        if((children == null) || children.isEmpty()) return;
        int len = children.size();
        for(int i = 0; i < len; i++) {
            SizeNode childrenNode = children.get(i);
            displayNode(childrenNode, newprefix , (i >= len-1));
        }
    }
}
