elivi-code-compressor
==============
Tool for compressing Java class files, similar to ProGuard and R8.

### Usage
Simply build the project using `./gradlew installDist`, then run the appropriate file from
`./build/install/elivi-code-compressor`.

#### Flags
|Name|Description|
|----|-----------|
|`REMOVE_LVT`|Remove the `LocalVariableTable` attribute from all methods|
|`REMOVE_LNT`|Remove the `LineNumberTable` attribute from all methods|
|`REMOVE_SOURCEFILE`|Remove the `SourceFile` attribute from the class|
|`RENAME_PRIVATE_FIELDS`|Rename all private fields to be shorter.|
|`RENAME_PRIVATE_METHODS`|Rename all private methods to be shorter.|
