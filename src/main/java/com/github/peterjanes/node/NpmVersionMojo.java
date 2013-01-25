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

import java.text.SimpleDateFormat;

import java.util.Date;

import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

import org.apache.maven.project.MavenProject;

/**
 * Mojo to generate an NPM-compatible version string.
 */
@Mojo(name = "version", requiresProject = true, defaultPhase = LifecyclePhase.INITIALIZE)
public class NpmVersionMojo extends AbstractMojo {
    /**
     * The Maven project to analyze.
     */
    @Component
    private MavenProject mavenProject;

    /**
     *
     * @throws MojoExecutionException if anything unexpected happens.
     */
    public void execute() throws MojoExecutionException {
        String projectVersion = mavenProject.getVersion();
        ArtifactVersion artifactVersion = new DefaultArtifactVersion(projectVersion);
        String npmVersion = String.format("%s.%s.%s",
            artifactVersion.getMajorVersion(),
            artifactVersion.getMinorVersion(),
            artifactVersion.getIncrementalVersion());
        if (projectVersion.endsWith("-SNAPSHOT")) {
            npmVersion = npmVersion + String.format("-%s", new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
        }
        mavenProject.getProperties().put("node.project.version", npmVersion);
    }
}
