# ISO-8583 Java Lib

#### ISO8583 Message Packer and Unpacker with ISOClient for communication with iso server 
###### (Supporting both Blocking IO and NIO)
###### (Supporting SSL/TLS)


A lightweight ISO8583 (is an international standard for financial transaction card originated interchange messaging - [wikipedia][iso8583-Wiki] ) library for Java and Android base on builder pattern and provide very simple use as you will see later.

  - Supporting Blocking IO and Non-blocking IO (NIO)
  - Supporting SSL/TLS
  - Base on Builder pattern
  - String not used for security reasons
  - Lightweight ISO-8583 lib for java and android
  - working with some enums, it's more readable
  - no dependency


### Usage

Library will be available at [Maven Centeral][mvn]
so you can add this dependency to your `pom.xml` like below:

```xml
<dependency>
    <groupId>com.imohsenb</groupId>
    <artifactId>ISO8583</artifactId>
    <version>1.0.5</version>
</dependency>
```
or direct download from [HERE][ddwn]

## ISOMessage
#### Create and pack an ISO message
To create an ISO message you must use ISOMessageBuilder which produce iso message for you:

```java

ISOMessage isoMessage = ISOMessageBuilder.Packer(VERSION.V1987)
                .networkManagement()
                .mti(MESSAGE_FUNCTION.Request, MESSAGE_ORIGIN.Acquirer)
                .processCode("920000")
                .setField(FIELDS.F11_STAN,  "1")
                .setField(FIELDS.F12_LocalTime,  "023120")
                .setField(FIELDS.F13_LocalDate,  "0332")
                .setField(FIELDS.F24_NII_FunctionCode,  "333")
                .setHeader("1002230000")
                .build();
                
```
with `ISOMessageBuilder.Packer(VERSION.V1987)` you can build iso message as you can see above. the 'Packer' method return 8 method for 8 iso message class (authorization, financial, networkManagment, ...) based on ISO8583 after that you can set message function and message origin by `mti` method.
`mti` method accept string and enums as parameter, and I think enums are much clear and readable.
As you know an iso message need a 'Processing Code' and you can set it's value by `processCode' method, and then we can start setting required fields by 'setField' method and accept String and enums as field number parameter.
After all, you must call build method to generate iso message object.
#### Unpack a buffer and parse it to ISO message
For unpacking a buffer received from server you need to use `ISOMessageBuilder.Unpacker()`:

```java
ISOMessage isoMessage = ISOMessageBuilder.Unpacker()
                                    .setMessage(SAMPLE_HEADER + SAMPLE_MSG)
                                    .build();
```
#### Working with ISOMessage object
ISOMessage object has multiple method provide fields, message body, header and ...
for security reason they will return byte array exept `.toString` and `.getStringField` method, because Strings stay alive in memory until a garbage collector will come to collect that. but you can clear byte or char arrays after use and calling garbage collector is not important anymore.
If you use Strings, taking memory dumps will be dangerous.
```java
    byte[] body = isoMessage.getBody();
```
```java
    byte[] trace = isoMessage.getField(11);
```
```java
    String trace = isoMessage.getStringField(FIELDS.F11_STAN);
```
## ISOClient
#### Sending message to iso server
Sending message to iso server and received response from server can be done with ISOClient in many ways:
```java
ISOClient client = ISOClientBuilder.createSocket(HOST, PORT)
                .build();

        client.connect();
        String response = Arrays.toString(client.sendMessageSync(new SampleIsoMessage()));
        System.out.println("response = " + response);
        client.disconnect();
```
#### Sending over SSL/TLS
Sending a message to ISO server over SSL/TLS is more complicated, especially with NIO methods. but I try to make it more simple and usefull:
```java
ISOClient client = ISOClientBuilder.createSocket(HOST, PORT)
                                .enableSSL()
                                .setSSLProtocol("TLSv1.2")
                                .setKeyManagers(km.getKeyManagers())
                                .setTrustManagers(null)
                                .build();
```

it's enough to adding `.enableSSL()` and another requirement parameters before `.build()` and may you need to prepare KeyStore and another things before. TrustManagers can be null.

#### Send a message with NIO benefits!
NIO (stands for non-blocking I/O) is a collection of Java programming language APIs that offer features for intensive I/O operations - [wikipedia][nio].
It's so simple to use in this library just see below : ðŸ˜Ž
```java
ISOClient client = ISOClientBuilder.createSocket(HOST, PORT)
                .configureBlocking(false)
                .build();
```

😁😁‚ it's ready for use, with just set `.configureBlocking` to false.
it is posibble to use with `.enableSSL()` too.


License
-------
Copyright 2018 Mohsen Beiranvand

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

   [iso8583-Wiki]: <https://en.wikipedia.org/wiki/ISO_8583/>
   [mvn]: <https://search.maven.org/>
   [mit]: <https://opensource.org/licenses/MIT/>
   [nio]: <https://en.wikipedia.org/wiki/New_I/O_(Java)/>
   [ddwn]: <https://oss.sonatype.org/service/local/repositories/releases/content/com/imohsenb/ISO8583/1.0.5/ISO8583-1.0.5.jar>

