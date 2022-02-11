# LibRikka

Rikka0w0's code library for Minecraft Modding, contains helpers and templates 
for constructing blocks and items, texture loading, networking, 
coded 3D models (BakedQuads generator), advanced texture loader and math helper functions.

Presented by the Chinese Institution of Scientific Minecraft Mod (CISM)

This library supports Minecraft Forge only!

Currently Support:
Minecraft 1.18.1

Deprecated:
Minecraft 1.11.2, 1.12.2, 1.14.4, 1.15.2, 1.16.5 (Works from 1.16.2 through 1.16.5), 1.17.1

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
dependencies {
    <Some Forge Stuff>
    implementation project(':librikka')
}
```
3. Add `librikka` to `settings.gradle`:
```
include 'librikka'

```
