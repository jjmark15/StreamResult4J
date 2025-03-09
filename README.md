# Stream Results for Java

This library implements a `StreamResult` type along with gatherers and collectors to make it easier to write Java streams expressions that deal with fallible lambdas that can throw checked exceptions. The aim is to have an API that enables:
- maintaining knowledge of which checked exceptions were thrown
- unwrapping exceptions and success values after terminating the stream
- optionally terminating the fallible stream operation early in the event of failures