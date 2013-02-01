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
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import java.util.List;

import org.apache.maven.artifact.Artifact;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;

import org.apache.maven.project.MavenProject;

import org.json.simple.JSONObject;

/**
 * Create package.json file from Maven contents.
 */
@Mojo(
    name = "package-json",
    requiresProject = true,
    defaultPhase = LifecyclePhase.PREPARE_PACKAGE,
    requiresDependencyResolution = ResolutionScope.TEST)
public class NpmPackageJsonMojo extends AbstractNpmMojo {
    /**
     * Location of the file.
     */
    @Parameter(defaultValue = "${project.build.outputDirectory}", required = true)
    private File outputDirectory;

    /**
     * Main module to load on require('this-project').
     */
    @Parameter(defaultValue = "exports.js", required = true)
    private String mainModule;

    /**
     *
     */
    @Parameter(property = "npm.dependencies")
    private String npmDependencies;

    /**
     *
     */
    @Parameter(property = "npm.dependencies.test")
    private String npmTestDependencies;

    /**
     *
     * @throws MojoExecutionException if anything unexpected happens.
     */
    public void execute() throws MojoExecutionException {
        if (!outputDirectory.exists()) {
            outputDirectory.mkdirs();
        }

        File packageJsonFile = new File(outputDirectory, "package.json");

        try {
            packageJsonFile.getParentFile().mkdirs();
            JSONObject packageJson = new JSONObject();
            packageJson.put("name", mavenProject.getName());
            packageJson.put("version", mavenProject.getProperties().getProperty("node.project.version"));
            packageJson.put("description", mavenProject.getDescription());
            packageJson.put("main", mainModule);

            List<Artifact> dependencyArtifacts = getNodeArtifacts(ResolutionScope.COMPILE);
            JSONObject dependencies = new JSONObject();
            // TODO: dependency.getVersion() may (and often will) return a
            // -SNAPSHOT. Figure out how to open the .tgz and pull in its
            // actual version, or rewrite the version as a range.
            for (Artifact dependency : dependencyArtifacts) {
                dependencies.put(dependency.getArtifactId(), dependency.getVersion());
            }
            addNpmDependencies(dependencies, npmDependencies);
            packageJson.put("dependencies", dependencies);

            List<Artifact> devDependencyArtifacts = getNodeArtifacts(ResolutionScope.TEST);
            JSONObject devDependencies = new JSONObject();
            for (Artifact devDependency : devDependencyArtifacts) {
                devDependencies.put(devDependency.getArtifactId(), devDependency.getVersion());
            }
            addNpmDependencies(devDependencies, npmTestDependencies);
            packageJson.put("devDependencies", devDependencies);

            JSONObject scripts = new JSONObject();
            scripts.put("test", "mocha -u exports -R xunit-file --recursive");
            scripts.put("coverage", "mocha -u exports -R mocha-lcov-reporter-file --recursive");
            packageJson.put("scripts", scripts);

            Writer w = new FileWriter(packageJsonFile);
            w.write(packageJson.toString());
            w.flush();
            w.close();
        } catch (IOException e) {
            throw new MojoExecutionException("Could not write package.json to " + packageJsonFile.getAbsolutePath(), e);
        }
    }

    private void addNpmDependencies(JSONObject dependencies, String npmDependencies) {
        if (null != npmDependencies) {
            String[] packages = npmDependencies.split(",");
            for (String pkg : packages) {
                String[] parsed = pkg.split("@");
                if (parsed.length == 2) {
                    if (parsed[1].startsWith("[")) {
                        getLog().warn("Version range found, ignoring " + pkg);
                    } else {
                        dependencies.put(parsed[0], parsed[1]);
                    }
                } else {
                    // TODO: get most recent version for pkg (npm view)
                    // 'dist-tags': { latest: '0.5.1', stable: '0.1.0' },
                    // TODO: get version from .tgz installs (no npm command,
                    // extract and use package/package.json)
                    getLog().warn("Version not found, ignoring " + pkg);
                }
            }
        }
    }
}
