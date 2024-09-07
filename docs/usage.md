---
layout: docs
title: Basic usage
prev_section: installation
next_section: loading-strategies
permalink: /docs/usage/
---

The approach used by OWNER APIs, is to define a Java interface associated to a
properties file.

Suppose your properties file is defined as `ServerConfig.properties`:  

```properties
port=80
hostname=foobar.com
maxThreads=100
```

To access this properties file you need to define a convenient Java interface 
`ServerConfig.java` in the same package:

```java
import org.aeonbits.owner.Config;

public interface ServerConfig extends Config {
    int port();
    String hostname();
    @DefaultValue("42")
    int maxThreads();

    @DefaultValue("77")
    Optional<Integer> someInteger();
}
```

Notice that the above interface extends from `Config`, that
is a marker interface recognized by OWNER as valid to work with.

We'll call this interface the *Properties Mapping Interface* or just
*Mapping Interface* since its goal is to map Properties into a an easy to use
piece of code.


How does the mapping work?
--------------------------

Since the properties file does have the same name as the Java class, and they
are located in the same package, the OWNER API will be able to automatically
associate them.  
For instance, if your *mapping interface* is called `com.foo.bar.ServerConfig`, 
OWNER will try to associate it to `com.foo.bar.ServerConfig.properties`, 
loading from the classpath.  


The properties names defined in the properties file will be associated to the
methods in the Java class having the same name.  
For instance, the property `port` defined in the properties file will be 
associated to the method `int port()` in the Java class, the property `hostname`
will be associated to the method `String hostname()` and the appropriate type
conversion will apply automatically, so the method `port()` will return an int
while the method `hostname()` will return a Java string, since the interface is
defined in this way.

The mapping mechanism is fully customizable, as well the automatic type 
conversion we just introduced is flexible enough to cover most of the Java types 
as well as object types defined by the user.  
You can see how in the next chapters.

## Using the Config object

At this point, you can create the ServerConfig object and use it in your code:

```java
ServerConfig cfg = ConfigFactory.create(ServerConfig.class);
System.out.println("Server " + cfg.hostname() + ":" + cfg.port() +
                   " will run " + cfg.maxThreads());
```


Using @DefaultValue and @Key annotations
----------------------------------------

Did you notice that in the above example it is specified `@DefaultValue("42")` 
annotation? 

```java
public interface ServerConfig extends Config {
    int port();
    String hostname();
    @DefaultValue("42")    // here!!!
    int maxThreads();
}
```

It is used in case the `maxThread` key is missing from the
properties file.

This annotation gets automatically converted to `int`, since `maxThreads()`
returns an `int`. 

Using the annotations, you can also customize the property keys:

```properties
# Example of property file 'ServerConfig.properties'
server.http.port=80
server.host.name=foobar.com
server.max.threads=100
```

This time, as commonly happens in Java applications, the properties names are
separated by dots. Instead of just "port" we have "server.http.port", so we
need to map this property name to the associated method using the `@Key`
annotation.

```java
/*
 * Example of ServerConfig.java interface mapping the previous 
 * properties file.
 */
public interface ServerConfig extends Config {
    @Key("server.http.port")
    int port();

    @Key("server.host.name")
    String hostname();

    @Key("server.max.threads");
    @DefaultValue("42")
    int maxThreads();
}
```

The `@DefaultValue` and `@Key` annotations are the basics to start using the
OWNER API.

<div class="note">
  <h5>You can leave the properties file away during development!</h5>
  <p>
     During the development you may decide to just use the `@DefaultValue` to
     provide a default configuration, without really adding the properties file.
     You can add the properties file later or leave this task to the end user.
  </p>
</div>

Undefined properties
--------------------

Suppose you have defined a method in your *mapping interface* that cannot be 
resolved to any property loaded from a properties file, and this method doesn't 
define a `@DefaultValue` what happens? Simple: it will return null, or a 
NullPointerException;

Suppose our ServerConfig class was looking like this:

```java
public static interface ServerConfig extends Config {
    String hostname();
    int port();
    Boolean debugEnabled();
}
```

If we don't have any ServerConfig.properties associated to it, when we call the
method `String hostname()` it will return null, as well as when we call the 
method `Boolean debugEnabled()` since the return types String and Boolean are
java objects. But if we call the method `int port()` then a 
`NullPointerException` will be raised.

<div class="note">
  <h5>Don't like the NullPointerException?</h5>
  <p>
    We support <code>Optional</code> as the returned config value. For example, instead of
    <code>int maxNumThreads();</code>, write it as <code>Optional&lt;Integer&gt; maxNumThreads();</code>. To make a default
    value in this case, simply use the same annotation such as <code>@DefaultValue("8")</code>. The feature is based on
    the 3 rationals below:

    <ol>
      <li> Method should "return _empty_ arrays or collections, instead of nulls" (_Effective Java, Joshua Bloch, 2nd
           Edition_, Item 43)
      <li> For better error handling, our methods "don't return null" (_Clean Code, Robert C. Martin, 2009_, Ch. 7,
           Don't Return Null)
      <li> Oracle designed <a href="https://www.oracle.com/technical-resources/articles/java/java8-optional.html">Optional, which was exactly intended to replace `null` with a new standard</a>.
    </ol>

    What this feature brings to our team, and others in general, is that it allows us to promote null-check at
    compile-time. Our team believes compile-time check is good by reducing runtime-errors.
    
    "<i>Why not using the original's <a href="http://owner.aeonbits.org/docs/type-conversion/"><code>@ConverterClass</code></a></i> or a wrapper?"
    
    Because we tried and concluded they are suboptimal in our case:
    
    <ol>
      <li> This would require every single of our applications to load an extra converter class
      <li> ConverterClass, i.e. <code>Optional&lt;?&gt;</code>, due to type erasure, loses type information, i.e.
           <code><?></code>, which original project does need in order to do proper conversion
    
    
    At this moment, the fork allows the following config value types to be wrapped inside <code>Optional</code>:
    
    <ol>
      <li> Boolean
      <li> Integer
      <li> String
      <li> All Collection classes that the [original project](http://owner.aeonbits.org) supports
    </ol>
    
    We will keep working to catch up with all types supported by the original project.
  </p>
</div>

Conclusions
-----------

Now you know the minimum to get productive with the OWNER API. But this is just
the beginning. OWNER is a rich API that allows you to add additional behaviors
and have more interesting features, so that you should be able to use this 
library virtually in any other context where you where using the 
`java.util.Properties` class.
