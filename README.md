# Simple Text Crypt

It's an Android app that encrypts plain texts.
This app does not claim any permissions, so you can trust that it cannot send
any of your private data to anyone.

## Can You Trust it?

Messages produced by this app are secure and, for example, can be
confidently sent over a network. It uses
[AES](https://en.wikipedia.org/wiki/Advanced_Encryption_Standard)
in GCM mode with no padding for encryption. It uses
[Argon2](https://en.wikipedia.org/wiki/Argon2) and a random Salt
to derive a secure key from the entered password.
This is a very powerful encryption. It also encrypts its
settings before storing them on the device.

However, the app might be vulnerable to some attacks. Although it is secure
enough to stand attacks from non-expert crackers, e.g. normal users,
should not be used for serious data encryption.

## Why Should You Encrypt?

It's a big subject to discuss. I recommend reading these two articles:

1. [Why do you need PGP?](http://www.pgpi.org/doc/whypgp/en/)
   In this article, Phil Zimmermann compares the encryption of emails to putting
   letters in an envelope: if you don't have anything to hide, why do you hide
   your messages in envelopes?

2. [Why we encrypt?](https://www.schneier.com/blog/archives/2015/06/why_we_encrypt.html)
   This article tells us that we should encrypt everything, not just to protect our
   privacy, but also to protect those activists whose lives depend on encryption.

## Installation

It is recommended to install the app from
[F-Droid](https://f-droid.org/repository/browse/?fdid=com.aidinhut.simpletextcrypt).

You can also directly download the APK from F-Droid if you don't want to install the F-Droid app.

## Decrypting with Other Apps

Because Simple Text Crypt uses standard algorithms, you can decrypt your data with any other apps
too. Follow these steps.

The first `16` characters of the encrypted string is the IV. It's encoded with Base64. Extract that
from the string and decode it from Base64 to bytes.

You need use Argon2 algorithm to derive a key from your Encryption Key. With the following arguments:

```
Mode: ARGON2_ID
Salt: The IV you extracted in the previous step
Cost In Iterations: 5
Cost In Memory: 64 * 1024
```

Remove the first `16` characters (IV) from the encrypted string. Pass the rest to your AES
decryption function. The AES arguments are as follows:

```
Algorithm: AES
Transformation: GCM
Padding: No padding
Tag size: 128
```

## Development

Open the directory with Android Studio.

From command line, to run the tests execute: `./gradlew app:connectedAndroidTest`

To install the debug build: `./gradlew installDebug`

## Copyright

Copyright (c) 2015-2025 Aidin Gharibnavaz

SimpleTextCrypt is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

SimpleTextCrypt is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with SimpleTextCrypt. If not, see <https://www.gnu.org/licenses/>.
