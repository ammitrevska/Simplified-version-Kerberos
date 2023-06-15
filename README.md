# Simplified-version-Kerberos

## Key establishment using a simplified version of Kerberos

* `User` - instances of Alice and Bob
  * key, nonce and timestamp generation
* `EncDec` - encription and decription
* `KDC` -  securely distributing session keys to enable secure communication between network entities. Ensures the confidentiality and integrity of network communications by allowing entities to mutually authenticate and establish encrypted communication channels.
  * session key and life time generation
* `AES` - AES standard for encoding used in ECB mod

 > for message exchange the functions communicate() and ReceivedMessage() are used from the User class.

&nbsp;

---
Protocol
  ![Picture1](https://github.com/ammitrevska/Simplified-version-Kerberos/assets/94235179/01fd1b10-d086-48dc-be82-40e9208b8fff)
