package org.ros.highlighter;

import com.intellij.ide.plugins.PluginManagerCore;
import com.intellij.openapi.extensions.PluginId;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.textmate.api.TextMateBundleProvider;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

public class RosTextMateBundleProvider implements TextMateBundleProvider {

    @Override
    public @NotNull List<PluginBundle> getBundles() {
        var plugin = PluginManagerCore.getPlugin(PluginId.getId("org.ros.highlighter.ros-msg"));
        if (plugin == null) return Collections.emptyList();
        Path bundlePath = plugin.getPluginPath().resolve("textmate/ROS");
        if (!Files.isDirectory(bundlePath)) return Collections.emptyList();
        return List.of(new PluginBundle("ROS Interface", bundlePath));
    }
}
