/*
 * Copyright (c) 2012-2015, Luigi R. Viggiano
 * All rights reserved.
 *
 * This software is distributable under the BSD license.
 * See the terms of the BSD license in the documentation provided with this software.
 */

package io.github.qubitpi.owner.examples;

import io.github.qubitpi.owner.Config;
import io.github.qubitpi.owner.Config.HotReload;
import io.github.qubitpi.owner.Config.Sources;
import io.github.qubitpi.owner.ConfigFactory;
import io.github.qubitpi.owner.Reloadable;
import io.github.qubitpi.owner.event.ReloadEvent;
import io.github.qubitpi.owner.event.ReloadListener;
import io.github.qubitpi.owner.util.Util;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.Properties;

/**
 * @author Luigi R. Viggiano
 */
public class HotReloadExample {
    private static final String CFG_FILE = "file:target/examples-generated-resources/HotReloadExample.properties";
    private static File target;

    static {
        try {
            target = Util.fileFromURI(CFG_FILE);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    @Sources(CFG_FILE)
    @HotReload(1)
    interface AutoReloadConfig extends Config, Reloadable {
        @DefaultValue("5")
        Integer someValue();
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        System.out.printf("\n\n HOT RELOAD EXAMPLE \n\n");

        Util.save(target, new Properties() {{
            setProperty("someValue", "10");
        }});

        AutoReloadConfig cfg = ConfigFactory.create(AutoReloadConfig.class);

        cfg.addReloadListener(new ReloadListener() {
            public void reloadPerformed(ReloadEvent event) {
                System.out.print("\rReload intercepted at " + new Date() + " \n");
            }
        });

        System.out.println("The program is running. ");

        System.out.println("Now you can change the file located at: \n\n\t" + target.getAbsolutePath() +
                           "\n\n ...and see the changes reflected below\n\n");
        int someValue = 0;
        while (someValue >= 0) {
            someValue = cfg.someValue();
            System.out.print("\rsomeValue is: " + someValue + "\t\t\t\t");
            Thread.sleep(500);
        }
    }
}
