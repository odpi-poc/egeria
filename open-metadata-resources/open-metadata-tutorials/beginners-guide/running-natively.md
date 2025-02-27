<!-- SPDX-License-Identifier: CC-BY-4.0 -->
<!-- Copyright Contributors to the ODPi Egeria project. -->

# Running Egeria natively

These technologies are what Egeria itself uses to operate. They are included when using the
[self-contained environments](running-self-contained.md), but they can also be installed and run natively (directly)
on your system.

Just be aware that running them natively on your system will require the additional effort of downloading, installing,
configuring and operating each one -- which the self-contained environments largely take care of for you. To make use
of the latest Egeria software, you will likely also need to be familiar with how to [build Egeria](building.md) in
order to use it natively.

Follow the [OMAG Server Platform tutorial](../open-metadata-tutorials/omag-server-tutorial/task-starting-the-omag-server-platform.md)
for instructions on how to set up and run a platform yourself.
You need to start three OMAG Server Platforms at the following URLs:

- `http://localhost:8080`
- `http://localhost:8081`
- `http://localhost:8082`

## Java

[Java](https://www.java.com) is a relatively mature object-oriented programming language that was
originally designed to be able to easily run programs across a number of different computer systems.

The Egeria project itself is primarily written in Java, and therefore a Java Runtime Environment (JRE)
is the most basic component needed in order to run Egeria.

You can either run such an environment directly on your system, or you can use one of the options
below to run a self-contained environment on top of your system.

You can check if you have a JRE available by running the command `java -version` from the command-line.

If not, and you prefer to run it directly rather than via one of the options outlined below,
Java can be installed by:

1. Downloading the **OpenJDK 8 (LTS) HotSpot** JVM from [AdoptOpenJDK](https://adoptopenjdk.net).
1. Running the installer that is downloaded.

## Apache Kafka

[Apache Kafka](https://kafka.apache.org) is used by Egeria as an event bus.

There is a [QuickStart Guide provided by Kafka itself](https://kafka.apache.org/quickstart) covering
installation and basic usage.

For the tutorials, the Kafka server needs to be running a `PLAINTEXT` listener on `localhost:59092`. From the directory
where Kafka is installed, edit the `config/server.properties` file so that the `listeners` and `advertised.listeners`
are setup as follows:

```text
listeners=PLAINTEXT://localhost:59092
advertised.listeners=PLAINTEXT://localhost:59092
```

To restart Kafka after making this update, change into Kafka's `bin` directory and issue the following commands:

```
$ ./zookeeper-server-start.sh ../config/zookeeper.properties &
$ ./kafka-server-start.sh ../config/server.properties &
```

## Apache ZooKeeper

[Apache ZooKeeper](https://zookeeper.apache.org) is used by Apache Kafka for maintaining certain configuration
information. It is therefore typically a pre-requisite to using Apache Kafka.

The [QuickStart Guide provided by Kafka itself](https://kafka.apache.org/quickstart) includes quick-and-dirty
instructions on running a minimal ZooKeeper server necessary for Kafka's use. For more detailed information,
you will want to read the [ZooKeeper Getting Started Guide](https://zookeeper.apache.org/doc/current/zookeeperStarted.html).


----
License: [CC BY 4.0](https://creativecommons.org/licenses/by/4.0/),
Copyright Contributors to the ODPi Egeria project.
