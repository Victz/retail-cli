# Retail CLI

### Reference Documentation

For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/2.4.5/maven-plugin/reference/html/)
* [Create an OCI image](https://docs.spring.io/spring-boot/docs/2.4.5/maven-plugin/reference/html/#build-image)
* [Spring Data JPA](https://docs.spring.io/spring-boot/docs/2.4.5/reference/htmlsingle/#boot-features-jpa-and-spring-data)

## Development

Run the following commands in terminals or execute the Main RetailCliApplication.java from IDE directly.

```
./mvnw
```

## Building for production

### Packaging as jar

To build the final jar and optimize for production, run:

```
./mvnw clean install -DskipTests
```

This will generate retail-cli-0.0.1-SNAPSHOT.jar in target directory. To ensure everything worked, run:

```
java -jar target/retail-cli-0.0.1-SNAPSHOT.jar
```

## Testing

To launch your application's tests, run:

```
./mvnw verify
```