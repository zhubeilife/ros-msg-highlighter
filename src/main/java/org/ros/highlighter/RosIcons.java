package org.ros.highlighter;

import com.intellij.openapi.util.IconLoader;
import javax.swing.Icon;

public final class RosIcons {
    private RosIcons() {}

    public static final Icon MSG    = IconLoader.getIcon("/icons/ros_msg.svg",    RosIcons.class);
    public static final Icon SRV    = IconLoader.getIcon("/icons/ros_srv.svg",    RosIcons.class);
    public static final Icon ACTION = IconLoader.getIcon("/icons/ros_action.svg", RosIcons.class);
}
