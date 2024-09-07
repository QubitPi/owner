package io.github.qubitpi.owner;

import org.junit.Test;

import static org.junit.Assert.assertEquals;


/**
 * Created by chengmingwang on 12/24/16.
 */
public class ConfigInheritanceTest {
    @Config.Sources(
            {"classpath:test.properties","classpath:io/github/qubitpi/owner/first.properties"}
    )
    interface MyConfig extends Config {
        @DefaultValue("favoriteColor")
        String favoriteColor();
    }

    interface TheConfig1 extends MyConfig {

    }

    @Config.Sources(
            {"classpath:io/github/qubitpi/owner/second.properties"}
    )
    interface TheConfig2 extends MyConfig {

    }

    @Config.Sources(
            {"classpath:io/github/qubitpi/owner/second.properties"}
    )
    @Config.LoadPolicy(Config.LoadType.MERGE)
    interface TheConfig3 extends MyConfig {

    }

    @Test
    public void testSourcesInheritance() {
        TheConfig1 theConfig1 = ConfigFactory.create(TheConfig1.class);
        assertEquals("pink", theConfig1.favoriteColor());

        TheConfig2 theConfig2 = ConfigFactory.create(TheConfig2.class);
        assertEquals("favoriteColor", theConfig2.favoriteColor());

        TheConfig3 theConfig3 = ConfigFactory.create(TheConfig3.class);
        assertEquals("pink", theConfig3.favoriteColor());
    }
}
