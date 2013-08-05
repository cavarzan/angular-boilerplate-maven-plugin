package org.github.cavarzan.plugin;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.io.IOException;

/**
 * @author Deividi Cavarzan
 */
@Mojo(name = "build", defaultPhase = LifecyclePhase.PREPARE_PACKAGE )
public class AngularGruntMojo extends AbstractMojo {

    @Parameter (defaultValue = "src/main/web-app", required = true )
    File angularProjectDirectory;

    @Parameter(defaultValue = "${os.name}", readonly = true)
    String osName;

    @Parameter(defaultValue = "true", required = true)
    boolean executeNpm;

    @Parameter(defaultValue = "true", required = true)
    boolean executeBower;

    public void execute() throws MojoExecutionException {
        if (executeNpm) {
            npmInstall();
        }
        if (executeBower) {
            bowerUpdate();
            bowerInstall();
        }
        grunt();
    }

    private void npmInstall() throws MojoExecutionException {
        executeCommand("npm install");
    }
    private void bowerInstall() throws MojoExecutionException {
        executeCommand("bower install --no-color");
    }
    private void bowerUpdate() throws MojoExecutionException {
        executeCommand("npm update -g bower --no-color");
    }
    private void grunt() throws MojoExecutionException {
        executeCommand("grunt --force --no-color");
    }

    private void executeCommand(String command) throws MojoExecutionException {
        try {
            getLog().info("--------------------------------------");
            getLog().info("         " + command.toUpperCase());
            getLog().info("--------------------------------------");
            if (isWindows()) {
                command = "cmd /c " + command;
            }
            CommandLine cmdLine = CommandLine.parse(command);
            DefaultExecutor executor = new DefaultExecutor();
            executor.setWorkingDirectory(angularProjectDirectory);
            executor.execute(cmdLine);
        } catch (IOException e) {
            throw new MojoExecutionException("Error during : " + command, e);
        }
    }

    private boolean isWindows() {
        return osName.startsWith("Windows");
    }
}
