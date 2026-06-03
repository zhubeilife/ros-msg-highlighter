package org.ros.highlighter;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.textmate.api.TextMateBundleProvider;

import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

public class RosTextMateBundleProvider implements TextMateBundleProvider {

    @Override
    public @NotNull List<PluginBundle> getBundles() {
        try {
            // Resolve plugin root via a resource known to be inside the plugin JAR.
            // The classloader returns a URL of the form:
            //   jar:file:/path/to/lib/plugin.jar!/META-INF/plugin.xml
            // Strip the "jar:" prefix and the "!/..." suffix to get the JAR file URI,
            // then navigate up two levels (lib/plugin.jar → lib → plugin root).
            URL res = RosTextMateBundleProvider.class.getResource("/META-INF/plugin.xml");
            if (res == null) return Collections.emptyList();
            String spec = res.toString();
            URI jarUri = spec.startsWith("jar:")
                    ? URI.create(spec.substring(4, spec.indexOf("!/")))
                    : res.toURI();
            Path bundlePath = Paths.get(jarUri).getParent().getParent().resolve("textmate/ROS");
            if (!Files.isDirectory(bundlePath)) return Collections.emptyList();
            return List.of(new PluginBundle("ROS Interface", bundlePath));
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
}
