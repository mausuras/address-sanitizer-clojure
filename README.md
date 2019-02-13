# address-sanitizer-clojure
Cli tool so sanitize addresses written in Clojure.

This is my first project using Clojure.

# Problem
Addresses have a clear structure that can be more or less detailed. It is very simple to model an address in a relational database but very often you get a CSV with infromation about companies or people including addresses that dont come correctly formated.

Unlike email addresses and phone numbers, addresses cannot easly be parsed or validated though regex. In addition in the real world people make typos, abreviate some words and only include part of the addres, turning something like

Geissbergstrasse 3, 8302 Kloten Zurich CH 

into 

Geisbergstr. 3 Kloten

If you need or want the complete address it would be nice to have it automatically corrected.

# Solution
This solution proposes to use an external service like open street maps, PTV maps or google maps, to sanitize and enrich the incorrect incomplete addresses.

It should be flexible enough to take a CSV or text file and list of the address fields that you want and return a file that contains the corrected and enriched addresses. If the address is not found in the service provider than it should return the original address in the field 'fallback'

# Next steps
In the future a REST API should be created an integrate this tool to provide a easier integration between applications. 

## Usage

Compile 

```
lein uberjar

```

Run

```
java -jar target/address-sanitizer-clojure-0.1.0-SNAPSHOT-standalone.jar

Usage: program-name [options] filepath

Options:
  -c, --chunk CHUNK_SIZE  10  Chunk size
  -h, --help

```

## License

Copyright © 2018 Miguel Soares

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
