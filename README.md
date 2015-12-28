Simple Text Crypt
=================

It's an Android app which encrypts plain texts.
This app does not claim any permissions, so you can trust that it cannot send
any of your private data to anyone.

Can You Trust it?
-----------------

Messages produces by this app is secure and can be
confidently send over a network, for example. It uses
[AES](https://en.wikipedia.org/wiki/Advanced_Encryption_Standard)
in CBC mode with
PKCS5 padding for encryption, and uses
[PBKDF2](https://en.wikipedia.org/wiki/PBKDF2) with HMAC, SHA1 and a random Salt
in order to derive a secure key from the entered password.
This is a very powerful encryption. Also it encrypts its
settings before storing them on the device.

However, the app itself may not be very secure, and probably vulnerable to
some attacks. Although it is secure enough to stand attacks from non-expert
crackers, e.g. normal users, it should not be used for serious data encryption.

Why Should You Encrypt?
----------------------

It's a big subject to discuss. I recommend reading these two articles:

1. [Why do you need PGP?](http://www.pgpi.org/doc/whypgp/en/)
In this article, Phil Zimmermann compare encryption of emails to putting
letters in an envelop: if you don't have anything to hide, why do you hide
your messages in envelops?

2. [Why we encrypt?](https://www.schneier.com/blog/archives/2015/06/why_we_encrypt.html)
This article tells us that we should encrypt everything not just to protect our
privacy, but also to protect those activists which their lives are depend on
encryption.

Installation
------------

It is recommended to install the app from
[F-Droid](https://f-droid.org/repository/browse/?fdid=com.aidinhut.simpletextcrypt).

Alternatively, you can download the apk directly from
[releases tab](https://github.com/aidin36/simpletextcrypt/releases).

Copyright
---------

Copyright (c) 2015 Aidin Gharibnavaz

SimpleTextCrypt is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

SimpleTextCrypt is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with SimpleTextCrypt.  If not, see <https://www.gnu.org/licenses/>.
