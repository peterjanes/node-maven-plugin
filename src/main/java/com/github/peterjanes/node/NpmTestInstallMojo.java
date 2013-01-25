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
 * Install NPM artifacts for testing.
 */
@Mojo(
    name = "test-install",
    requiresProject = true,
    defaultPhase = LifecyclePhase.GENERATE_TEST_RESOURCES,
    requiresDependencyResolution = ResolutionScope.TEST)
public class NpmTestInstallMojo extends AbstractNpmInstallMojo {
    /**
     * Location of the file.
     */
    @Parameter(defaultValue = "${project.build.testOutputDirectory}", required = true)
    private File testOutputDirectory;

    /**
     *
     */
    @Parameter(property = "npm.dependencies.test")
    private String npmTestDependencies;

    /**
     *
     */
    @Parameter(property = "npm.extra.arguments.test")
    private String extraTestArguments;

    /**
     * Runs npm install.
     * @throws MojoExecutionException if anything unexpected happens.
     */
    public void execute() throws MojoExecutionException {
        execute(testOutputDirectory, npmTestDependencies, extraTestArguments, ResolutionScope.TEST);
    }
}
