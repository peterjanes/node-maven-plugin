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
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.Executor;
import org.apache.commons.exec.PumpStreamHandler;

import org.apache.maven.artifact.Artifact;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import org.apache.maven.project.MavenProject;

/**
 * Abstract mojo to install NPM artifacts.
 */
@Mojo(name = "package", requiresProject = true, defaultPhase = LifecyclePhase.PACKAGE)
public class NpmPackMojo extends AbstractMojo {
    private static final String EXECUTION_FAILED = "Command execution failed.";

    /**
     * Location of the file.
     */
    @Parameter(defaultValue = "${project.build.directory}", required = true)
    private File outputDirectory;

    /**
     * Location of the NPM executable.
     */
    @Parameter(defaultValue = "npm", required = true)
    private String executable;

    @Component
    private MavenProject mavenProject;

    /**
     *
     * @throws MojoExecutionException if anything unexpected happens.
     */
    public void execute() throws MojoExecutionException {
        if (!outputDirectory.exists()) {
            outputDirectory.mkdirs();
        }

        CommandLine commandLine = new CommandLine(executable);
        Executor exec = new DefaultExecutor();
        exec.setWorkingDirectory(outputDirectory);

        List commandArguments = new ArrayList();
        commandArguments.add("pack");
        commandArguments.add("commonjs");

        String[] args = new String[commandArguments.size()];
        for (int i = 0; i < commandArguments.size(); i++) {
            args[i] = (String) commandArguments.get(i);
        }

        commandLine.addArguments(args, false);

        OutputStream stdout = System.out;
        OutputStream stderr = System.err;

        try {
            getLog().debug("Executing command line: " + commandLine);
            exec.setStreamHandler(new PumpStreamHandler(stdout, stderr, System.in));

            int resultCode = exec.execute(commandLine);

            if (0 != resultCode) {
                throw new MojoExecutionException("Result of " + commandLine + " execution is: '" + resultCode
                    + "'.");
            }

            Artifact artifact = mavenProject.getArtifact();
            String fileName = String.format(
                "%s-%s.tgz",
                artifact.getArtifactId(),
                mavenProject.getProperties().getProperty("node.project.version"));
            artifact.setFile(new File(outputDirectory, fileName));
        } catch (ExecuteException e) {
            throw new MojoExecutionException(EXECUTION_FAILED, e);
        } catch (IOException e) {
            throw new MojoExecutionException(EXECUTION_FAILED, e);
        }
    }
}
