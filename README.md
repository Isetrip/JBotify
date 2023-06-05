# JBotify

JBotify is a Java framework designed for convenient bot development. It provides a wide range of pre-built functions and mechanics for easy interaction with Telegram bots.

## Features

<ul>
  <li>Easy-to-use Java framework for bot development.</li>
  <li>Provides a variety of built-in functions and mechanics for Telegram bots.</li>
  <li>Supports integration with popular databases like SQLite and MySQL.</li>
  <li>Built with libraries such as Hibernate, Lombok, etc.</li>
</ul>

## Installation

To include JBotify in your project, you can use either Gradle or Maven.
To get started with JBotify, follow these steps:

<ul>
    <li>Download the latest version from the official GitHub repository(https://github.com/Isetrip/JBotify/releases/tag/untagged-a03183ed150be61d701b).</li>
    <li>Create a folder named libs in your project directory.</li>
    <li>Place the downloaded Mega Framework file into the libs folder.</li>
    <li>Add the following dependencies to your build system:</li>
</ul>

### Gradle

Add the following dependency to your `build.gradle` file:

```groovy
dependencies {
    implementation files('libs/JBotify-1.1.0-SHAPSHOT-sources.jar')
}
```

### Maven

Add the following dependency to your `pom.xml` file:

```xml
<dependencies>
    <dependency>
        <groupId>com.isetrip</groupId>
        <artifactId>JBotify-SHAPSHOT</artifactId>
        <version>1.1.0</version>
        <scope>system</scope>
        <systemPath>${basedir}/libs/JBotify-1.1.0-SHAPSHOT-sources.jar</systemPath>
    </dependency>
</dependencies>


```

### Usage

Examples and documentation can be found on the [GitHub repository](https://github.com/Isetrip/JBotify/tree/main/src/main/java/com/isetrip/jbotify/examples).

### Issues

If you encounter any issues or have feature requests, please [submit an issue](https://github.com/Isetrip/JBotify/issues) on GitHub.
