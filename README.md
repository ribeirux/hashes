# Hashes

## About

Cross-plataform tool that generates and injects different keys with the same hash code in order to test web applications against hash collision attacks.
Supported web application technologies:
 - java (equivalent substrings)
 - php (equivalent substrings)
 - asp.net (multithreaded meet in the middle)
 - v8 (multithreaded meet in the middle)

[This video](http://www.youtube.com/watch?v=R2Cq3CLI6H8) describes how a hash collision attack works.

Hashes is released under the terms of the Apache License Version 2.0.

## Usage

**Use only for testing purposes, not for evil.**

	hashes [options...] <url>
	 -a,--asp <seed>                     Build ASP payload (default OFF)
	 -b,--connection-timeout <timeout>   Connection timeout in seconds (default 60)
	 -c,--clients <clients>              Number of clients to run (default 1)
	 -d,--read-timeout <timeout>         Read timeout in seconds (default 60)
	 -g,--v8 <seed>                      Build V8 payload (default OFF)
	 -h,--help                           Print this message
	 -j,--java                           Build JAVA payload (default OFF)
	 -k,--keys <keys>                    Number of keys to inject per request (default 85000)
	 -n,--new                            Generate new keys instead of using pre-built collisions (default OFF)
	 -p,--php                            Build PHP payload (default ON)
	 -r,--requests <requests>            Number of requests to submit per client (default 1)
	 -s,--save <file>                    Save keys to file (default OFF)
	 -w,--wait                           Wait for response (default OFF)
