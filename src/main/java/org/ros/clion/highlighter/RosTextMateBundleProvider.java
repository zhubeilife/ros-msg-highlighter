package org.ros.clion.highlighter;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.textmate.api.TextMateBundleProvider;
import org.jetbrains.plugins.textmate.api.TextMateBundleProvider.PluginBundle;

import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

public class RosTextMateBundleProvider implements TextMateBundleProvider {

    @Override
    public @NotNull List<PluginBundle> getBundles() {
        URL bundleUrl = getClass().getClassLoader().getResource("textmate/ROS");
        if (bundleUrl == null) {
            return Collections.emptyList();
        }
        File bundleDir = new File(bundleUrl.getPath());
        if (!bundleDir.exists() || !bundleDir.isDirectory()) {
            return Collections.emptyList();
        }
        PluginBundle bundle = new PluginBundle("ROS Interface", bundleDir.toPath());
        return List.of(bundle);
    }
}