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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.Executor;
import org.apache.commons.exec.PumpStreamHandler;

import org.apache.maven.artifact.Artifact;

import org.apache.maven.plugin.MojoExecutionException;

import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;

import org.apache.maven.project.MavenProject;

/**
 * Abstract mojo to install NPM artifacts.
 */
public abstract class AbstractNpmInstallMojo extends AbstractNpmMojo {
    private static final String EXECUTION_FAILED = "Command execution failed.";

    @Parameter(defaultValue = "npm", required = true)
    private String executable;

    /**
     *
     * @param outputDirectory The working directory for the npm command.
     * @param npmDependencies A comma-separated list of npm packages to install.
     * @param extraArguments Extra arguments to provide to the npm command.
     * @param artifactScope Scope of the artifacts to include.
     * @throws MojoExecutionException if anything unexpected happens.
     */
    void execute(
        File outputDirectory,
        String npmDependencies,
        String extraArguments,
        ResolutionScope artifactScope) throws MojoExecutionException {
        if (!outputDirectory.exists()) {
            outputDirectory.mkdirs();
        }

        CommandLine commandLine = new CommandLine(executable);
        Executor exec = new DefaultExecutor();
        exec.setWorkingDirectory(outputDirectory);

        List commandArguments = new ArrayList();
        commandArguments.add("install");

        if (null != npmDependencies) {
            String[] packages = npmDependencies.split(",");
            for (String pkg : packages) {
                commandArguments.add(pkg);
            }
        }

        List<Artifact> nodeArtifacts = getNodeArtifacts(artifactScope);
        for (Artifact artifact : nodeArtifacts) {
            commandArguments.add(artifact.getFile().getAbsolutePath());
        }

        if (null != extraArguments) {
            String[] extraArgs = extraArguments.split(" ");
            for (String extraArg : extraArgs) {
                commandArguments.add(extraArg);
            }
        }

        String[] args = new String[commandArguments.size()];
        for (int i = 0; i < commandArguments.size(); i++) {
            args[i] = (String) commandArguments.get(i);
        }

        commandLine.addArguments(args, false);

        OutputStream stdout = getLog().isDebugEnabled() ? System.err : new ByteArrayOutputStream();
        OutputStream stderr = getLog().isDebugEnabled() ? System.err : new ByteArrayOutputStream();

        try {
            getLog().debug(
                "Executing command line " + commandLine
                + " in directory " + outputDirectory.getAbsolutePath());
            exec.setStreamHandler(new PumpStreamHandler(stdout, stderr, System.in));

            int resultCode = exec.execute(commandLine);

            if (0 != resultCode) {
                System.out.println("STDOUT: " + stdout);
                System.err.println("STDERR: " + stderr);
                throw new MojoExecutionException("Result of " + commandLine + " execution is: '" + resultCode
                    + "'.");
            }
        } catch (ExecuteException e) {
            throw new MojoExecutionException(EXECUTION_FAILED, e);

        } catch (IOException e) {
            throw new MojoExecutionException(EXECUTION_FAILED, e);
        }
    }
}
