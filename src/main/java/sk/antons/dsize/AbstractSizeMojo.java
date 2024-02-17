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

import java.io.File;
import java.util.List;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.filter.ArtifactFilter;
import org.apache.maven.artifact.resolver.filter.ScopeArtifactFilter;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.DefaultProjectBuildingRequest;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuildingRequest;
import org.apache.maven.shared.dependency.graph.DependencyGraphBuilder;
import org.apache.maven.shared.dependency.graph.DependencyGraphBuilderException;
import org.apache.maven.shared.dependency.graph.DependencyNode;

/**
 *
 * @author antons
 */
public abstract class AbstractSizeMojo extends AbstractMojo {

    @Parameter(property = "netto", defaultValue = "${netto}",  required = false )
    String netto;

    @Parameter(property = "scope", defaultValue = "${scope}", required = false )
    String scope;

    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    MavenProject project;

    @Parameter(defaultValue = "${session}", readonly = true, required = true)
    MavenSession session;

    @Component(hint = "default")
    DependencyGraphBuilder dependencyGraphBuilder;

    @Parameter(defaultValue = "${localRepository}", readonly = true, required = true)
    protected ArtifactRepository localRepository;



    protected SizeNode buildTree() throws MojoExecutionException {
        if(!(
            Artifact.SCOPE_COMPILE.equals(scope)
            || Artifact.SCOPE_COMPILE_PLUS_RUNTIME.equals(scope)
            || Artifact.SCOPE_IMPORT.equals(scope)
            || Artifact.SCOPE_PROVIDED.equals(scope)
            || Artifact.SCOPE_RUNTIME.equals(scope)
            || Artifact.SCOPE_RUNTIME_PLUS_SYSTEM.equals(scope)
            || Artifact.SCOPE_SYSTEM.equals(scope)
            || Artifact.SCOPE_TEST.equals(scope)
            )) {
            scope = Artifact.SCOPE_RUNTIME;
        }

        ArtifactFilter artifactFilter = null;
        //artifactFilter = new ScopeArtifactFilter(Artifact.SCOPE_COMPILE_PLUS_RUNTIME);
        //artifactFilter = new ScopeArtifactFilter(Artifact.SCOPE_COMPILE);
        artifactFilter = new ScopeArtifactFilter(scope);
        ProjectBuildingRequest buildingRequest = new DefaultProjectBuildingRequest(session.getProjectBuildingRequest());

        buildingRequest.setProject(project);

        try {
            getLog().info("read depdendency graph...");
            DependencyNode rootNode = dependencyGraphBuilder.buildDependencyGraph(buildingRequest, artifactFilter);
            getLog().info("read depdendency graph done");
            SizeNode root = SizeNode.instance(rootNode);
            String base = localRepository.getBasedir();
            buildTree(root, base);
            root.calculateCumulatedSize();
            return root;

        } catch (DependencyGraphBuilderException e) {
            throw new MojoExecutionException("Error", e);
        }
    }

    private void buildTree(SizeNode parent, String base) {
        if(parent == null) return;
        String path = localRepository.pathOf(parent.node().getArtifact());
        File f = new File(base + "/" + path);
        if(f.exists()) parent.size((int)f.length());
        List<DependencyNode> children = parent.node().getChildren();
        if((children == null) || children.isEmpty()) return;
        int len = children.size();
        for(int i = 0; i < len; i++) {
            DependencyNode childrenNode = children.get(i);
            SizeNode child = SizeNode.instance(childrenNode);
            parent.addChild(child);
            buildTree(child, base);
        }
    }
}
