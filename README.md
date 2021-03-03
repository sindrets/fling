# fling

A test framework for java.

![img](https://i.imgur.com/JSVTqt6.png)

## Usage

Write some tests and annotate your test methods with `TestGroup`. Your test
classes may have a constructor as long as it has no parameters.

```java
public class FooTest {
    @TestGroup(description = "Testing string")
    public void test(TestInitiator suite) {
        String s = "foo";
        suite.it("should have a length of 3")
            .expect(s.length())
            .toBe(3);
        suite.it("should not be 'bar'")
            .expect(s).not()
            .toBe("bar");
        suite.it("should throw")
            .expect(s.chars().limit(-1))
            .toThrow(IllegalArgumentException.class);
    }
}
```

Compile your test classes and run `fling`.

```sh
$ java -jar fling.jar $classpaths
```

`$classpaths` is a colon (`:`) separated list of classpaths. `fling` will
recursively find all methods with the `TestGroup` annotation, in all classes,
starting from the given classpaths.

## Options

#### `java -jar fling.jar <classpath>[:<classpath>...] [OPTIONS...]`

- `--exclude=<GLOB>[:<GLOB>...]`
  - A colon separated list of glob patterns matching files that should be
    excluded from the test.
- `--include=<GLOB>[:<GLOB>...]`
  - A colon separated list of glob patterns matching files that should be
    included. If this option is present the provided list of glob patterns will
    work as a whitelist: any tests not matched by the given patterns will be
    excluded.
- `--loglevel=<INT>`
  - The log level controls how verbose the output is. The different levels are:
    - `0: NOTHING`
    - `1: ERROR` - show errors only
    - `2: INFO` - show simplified test results
    - `3: TEST_STATUS` - show status for each individual test
    - `4: TEST_STDOUT` - show everything printed to stdout during tests
    - `>=5: ALL` - default level
