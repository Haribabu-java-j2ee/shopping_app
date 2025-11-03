# Kafka Version Fix

## Issue
The project was configured to use `spring-kafka` version **3.6.0**, which **does not exist** in Maven Central Repository.

## Root Cause
The `kafka.version` property in the parent POM was incorrectly set to `3.6.0`:
```xml
<kafka.version>3.6.0</kafka.version>
```

## Available Versions
According to Maven Central Repository:
- **Spring Kafka 3.1.0** - Compatible with Spring Boot 3.2.0 ✓
- **Spring Kafka 3.2.x** - Latest stable minor version
- **Spring Kafka 3.3.10** - Latest stable release
- **Spring Kafka 4.0.0-RC1** - Release Candidate (not recommended for production)

**Version 3.6.0 does NOT exist!**

## Solution Applied

### 1. Removed Incorrect Version Property
**Before:**
```xml
<properties>
    <kafka.version>3.6.0</kafka.version>
</properties>
```

**After:**
```xml
<!-- kafka.version property removed -->
```

### 2. Removed from Dependency Management
Since Spring Boot's parent POM already manages the `spring-kafka` version, we removed it from our custom dependency management section.

**Before:**
```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.springframework.kafka</groupId>
            <artifactId>spring-kafka</artifactId>
            <version>${kafka.version}</version>
        </dependency>
    </dependencies>
</dependencyManagement>
```

**After:**
```xml
<!-- Removed - Spring Boot parent manages this -->
```

### 3. Child POMs Inherit from Spring Boot
The child projects (auth-service, order-service, report-service) now inherit the correct version from Spring Boot's parent POM:

```xml
<dependency>
    <groupId>org.springframework.kafka</groupId>
    <artifactId>spring-kafka</artifactId>
    <!-- Version managed by Spring Boot 3.2.0 parent -->
</dependency>
```

## Verification

Running `mvn dependency:tree` on order-service confirms the correct version is now being used:

```
[INFO] +- org.springframework.kafka:spring-kafka:jar:3.1.0:compile
```

✅ **Spring Kafka 3.1.0** is the correct version for Spring Boot 3.2.0 and **exists in Maven Central**!

## Best Practice

**Always let Spring Boot manage its own dependency versions** unless you have a specific reason to override them. Spring Boot's parent POM carefully selects compatible versions of all dependencies.

### When to Override Versions
- Security patches for critical vulnerabilities
- Bug fixes in newer patch versions
- Specific features required from a newer version

### When NOT to Override
- Default Spring Boot setup (like in this project)
- When the managed version works fine
- When upgrading to non-existent versions 😄

## Impact
- ✅ Build will now succeed
- ✅ All Maven commands will work correctly
- ✅ Docker builds will succeed
- ✅ No more "missing artifact" errors

## Files Modified
- `/Users/hkalyankuma/dev/reports/shopping_app/pom.xml`
  - Removed `kafka.version` property (line 45)
  - Removed Kafka from dependencyManagement (lines 90-95)

## Testing
```bash
# Verify the fix
cd /Users/hkalyankuma/dev/reports/shopping_app
mvn clean install -DskipTests

# Check Kafka version
mvn dependency:tree -pl order-service | grep spring-kafka
```

Expected output:
```
[INFO] +- org.springframework.kafka:spring-kafka:jar:3.1.0:compile
```



