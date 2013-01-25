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

import java.io.File;

import org.apache.maven.plugin.MojoExecutionException;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;

/**
 * Install NPM artifacts.
 */
@Mojo(
    name = "install",
    requiresProject = true,
    defaultPhase = LifecyclePhase.GENERATE_RESOURCES,
    requiresDependencyResolution = ResolutionScope.COMPILE)
public class NpmInstallMojo extends AbstractNpmInstallMojo {
    /**
     * Location of the file.
     */
    @Parameter(defaultValue = "${project.build.outputDirectory}", required = true)
    private File outputDirectory;

    /**
     *
     */
    @Parameter(property = "npm.dependencies")
    private String npmDependencies;

    /**
     *
     */
    @Parameter(property = "npm.extra.arguments")
    private String extraArguments;

    /**
     * Runs npm install.
     * @throws MojoExecutionException if anything unexpected happens.
     */
    public void execute() throws MojoExecutionException {
        execute(outputDirectory, npmDependencies, extraArguments, ResolutionScope.COMPILE);
    }
}
