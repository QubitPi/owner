/*
 * Copyright (c) 2012-2015, Luigi R. Viggiano
 * All rights reserved.
 *
 * This software is distributable under the BSD license.
 * See the terms of the BSD license in the documentation provided with this software.
 */

package io.github.qubitpi.owner.examples;

import io.github.qubitpi.owner.Accessible;
import io.github.qubitpi.owner.ConfigFactory;
import io.github.qubitpi.owner.Mutable;
import io.github.qubitpi.owner.Reloadable;
import io.github.qubitpi.owner.event.ReloadEvent;
import io.github.qubitpi.owner.event.ReloadListener;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.management.ManagementFactory;

/**
 * @author Luigi R. Viggiano
 */
public class JMXExample {

    public interface MyConfig extends Mutable, Accessible, Reloadable {

        @Key("server.port.number")
        @DefaultValue("80")
        int port();

        @Key("server.host.name")
        @DefaultValue("localhost")
        String hostname();

    }

    public static void main(String[] args) throws InterruptedException, MalformedObjectNameException,
            InstanceAlreadyExistsException, NotCompliantMBeanException, MBeanRegistrationException {
        MyConfig cfg = ConfigFactory.create(MyConfig.class);

        cfg.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                System.out.printf("prop change event [%s = %s -> %s]%n", evt.getPropertyName(), evt.getOldValue(), evt.getNewValue());
            }
        });

        cfg.addReloadListener(new ReloadListener() {
            public void reloadPerformed(ReloadEvent event) {
                System.out.printf("reload event detected%n");
            }
        });

        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();

        mbs.registerMBean(cfg, new ObjectName("io.github.qubitpi.owner:type=configuration,name=MyConfig"));

        System.out.println("Now, launch jconsole and attach to the java process. Set a negative port number to exit.");

        while (cfg.port() >= 0) {
            System.out.printf("\rport: %d hostname: %s \t\t\t\t", cfg.port(), cfg.hostname());
            Thread.sleep(500);
        }

    }

}
