/*
 * Copyright 2013 Peter Janes (peterjanes)
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
 *
 */
package com.github.peterjanes.node;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.maven.artifact.Artifact;

import org.apache.maven.plugin.AbstractMojo;

import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.ResolutionScope;

import org.apache.maven.project.MavenProject;

/**
 * Abstract mojo to install NPM artifacts.
 */
public abstract class AbstractNpmMojo extends AbstractMojo {

    @Component
    protected MavenProject mavenProject;

    /**
     * Get the list of artifacts that are identified with type "commonjs" or
     * "nodejs".
     *
     * @param resolutionScope Scope of the artifacts to include.
     * @return The list of NodeJS artifacts.
     */
    public List<Artifact> getNodeArtifacts(ResolutionScope resolutionScope) {
        String scope = resolutionScope.id();
        Set<Artifact> artifacts = mavenProject.getArtifacts();
        List<Artifact> nodeArtifacts = new ArrayList<Artifact>();
        for (Artifact a : artifacts) {
            if (scope.equals(a.getScope()) &&
                ("commonjs".equals(a.getType())) || ("nodejs".equals(a.getType()))
            ) {
                nodeArtifacts.add(a);
            }
        }
        return nodeArtifacts;
    }
}
