# kotlin-hides-members


A Kotlin compiler plugin that allows `@HidesMembers` to work for more declarations than just `forEach` and `addSuppressed`.

In short, this plugin allows using `@HidesMembers` so that you can declare an extension function that takes over a member function in overload resolution.

The main use-case being redefining functions using multiple receivers.

Use with care!

Note: This plugin was created
using [Brian Norman's Kotlin IR Plugin Template](https://github.com/bnorm/kotlin-ir-plugin-template) and from guidance
from his wonderful article
series [Writing Your Second Kotlin Compiler Plugin](https://blog.bnorm.dev/writing-your-second-compiler-plugin-part-1) (seriously like the articles were immensely helpful when I just knew absolutely nothing about IR)
