# LibRikka

Rikka0w0's code library for Minecraft Modding, contains helpers and templates 
for constructing blocks and items, texture loading, networking, 
coded 3D models (BakedQuads generator), advanced texture loader and math helper functions.

Presented by the Chinese Institution of Scientific Minecraft Mod (CISM)

This library supports Minecraft Forge only! However the Patchwork project may allow this lib to run on Fabric.

Currently Support:
Minecraft 1.16.4 (Also work on 1.16.3)

Deprecated:
Minecraft 1.11.2, 1.12.2, 1.14.4, 1.15.2

Author: Rikka0w0 (小六花)

Different versions of Minecraft have vastly different architecture, so different versions of LibRikka might not be compatible!

Sorry I don't have time to write detailed documentation/tutorials. QAQ! 
But the code itself should be self-explainable.

Currently used by:
* [SimElectricity](https://github.com/RoyalAliceAcademyOfSciences/SimElectricity)
* [ztouhoudecoration](https://github.com/rikka0w0/ztouhoudecoration)
* [magicalsculpture](https://github.com/rikka0w0/magicalsculpture)

## For developers
To use librikka in your Mod:
1. Add librikka as a git submodule or manually download this repo
2. Edit the `build.gradle` of your Mod, add to the `sourceSets` section:
```
sourceSets {
    librikka {
        java {
            srcDir "librikka/src/main/java"
        }
        resources {
            srcDir "librikka/src/main/resources"
        }
    }

    main {
        .........Keep whatever was here................
        compileClasspath += librikka.output
        runtimeClasspath += librikka.output
    }
}
```
3. Add `librikka` to all 3 run configurations (Client, server and data):
```
minecraft {
    runs {
        client {
            mods {
                .........Keep whatever was here................

                librikka {
                    source sourceSets.librikka
                }
            }

```