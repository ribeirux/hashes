# Hashes

## About

Hashes is a Cross-plataform tool, that generates and injects different keys with the same hash code, in order to test web applications against hash collision attacks (28th Chaos Communication Congress).

Supported web application technologies:
 - java (equivalent substrings)
 - php (equivalent substrings)
 - asp.net (multithreaded meet in the middle)
 - v8 (multithreaded meet in the middle)

[This video](http://www.youtube.com/watch?v=R2Cq3CLI6H8) describes how a hash collision attack works.

Hashes is released under the terms of the Apache License Version 2.0.

## Building distribution

### Requirements

* [Maven](http://maven.apache.org/) 2.1.0 or above
* Java 6 or above

To build hashes:

1. Download the source from [here](https://github.com/ribeirux/hashes/archive/master.zip).  
2. Unzip the contents and in the project root directory execute: `mvn clean install -Pdistribution`. This will build hashes and run all tests. To skip the tests when building execute `mvn clean install -Pdistribution -DskipTests`
3. Grab the hashes distribution located in **dist/target/hashes-${version}-bin.zip** and unzip the contents into a new folder.

## Usage

**Use only for testing purposes, not for evil.**
 
	usage: hashes [options...] <POST url>
	 -a,--asp <seed>                             Build ASP payload using MITM algorithm (default: OFF)
	 -b,--connection-timeout <timeout>           Connection timeout in seconds, zero to disable timeout (default: 60)
	 -c,--clients <clients>                      Number of clients to run (default: 1)
	 -d,--read-timeout <timeout>                 Read timeout in seconds, zero to disable timeout (default: 60)
	 -e,--header <header>                        Use extra header (overrides internal header with same name)
	 -g,--v8 <seed>                              Build V8 payload using MITM algorithm (default: OFF)
	 -h,--help                                   Print this message
	 -j,--java                                   Build JAVA payload using equivalent substrings algorithm (default: OFF)
	 -k,--keys <keys>                            Number of keys to inject per request (default: 85000)
	 -m,--progress-bar                           Display hash collision generation progress (default: OFF)
	 -n,--new                                    Generate new keys instead of using pre-built collisions (default: OFF)
	 -p,--php                                    Build PHP payload using equivalent substrings algorithm (default: ON)
	 -r,--requests <requests>                    Number of requests to submit per client (default: 1)
	 -s,--save <file>                            Save keys to file (default: OFF)
	 -t,--mitm-worker-threads <worker threads>   Number of MITM worker threads (default: number of available processors)
	 -w,--wait                                   Wait for response (default: OFF)

