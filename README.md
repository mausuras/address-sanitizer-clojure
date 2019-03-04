# Address Sanitizer
Cli tool to sanitize and enrich addresses written in Clojure.


## Problem
Addresses have a clear structure that can be more or less detailed. It is simple to model an address in a relational database, but very often you get a CSV with infromation about companies or people including addresses that are not correctly formatted. 

Unlike email addresses and phone numbers, addresses cannot easly be parsed or validated though regex.
In addition people make typos, abreviate some words and only include part of the addres, turning something like

```
Geissbergstrasse 3, 8302 Kloten Zurich CH
```

into

```
Geisbergstr. 3 Kloten
```

For programmatic use of the data, especially in conjuction with other, external Services it is mendatory to have the data automatically corrected and normalized.


## Solution

This solution proposes to use an external service like [Open Street Maps](https://www.openstreetmap.org/), [PTV Maps](https://www.ptvgroup.com/en/solutions/products/ptv-xserver/developer-zone/digital-maps-api/) or [Google Maps](https://www.google.com/maps), to sanitize and enrich the incorrect or incomplete addresses.

It should be flexible enough to take a CSV or text file and list of the address fields that you want and return a file that contains the corrected and enriched addresses. If the address is not found in the service provider than it should return the original address in the field `fallback`.

## Next Steps

In order to make this tool usable in a more versatile and langauge agnostic way, a REST API should be implemented.

## Usage

### Prequisites:

- Current version of [Leiningen](https://leiningen.org/#install)
- [Java 8+](https://www.java.com/en/download/)

### Compiling

Before first using the tool, it is necessary to create an uberjar:

```
lein uberjar
```

The created uberjar will be located here

```
target/address-sanitizer-clojure-0.1.0-SNAPSHOT-standalone.jar
```

### Using the Tool

Once the file is compiled, you can use it with Java:

```
java -jar target/address-sanitizer-clojure-0.1.0-SNAPSHOT-standalone.jar

Usage: program-name [options] filepath

Options:
  -c, --chunk CHUNK_SIZE      100               Chunk size
  -o, --output FILE_PATH      /tmp/results.csv  Output file
  -f, --format OUTPUT_FORMAT  display_name      Output format
  -h, --help
```

An example case can be started with the provided dummy data:

```
java -jar target/address-sanitizer-clojure-0.1.0-SNAPSHOT-standalone.jar dummy_data/address_list.txt -c 6 -o /tmp/results.csv -f 'display_name lat lon fallback'
```

### Output format

The several options for the output format can be found at:
[Nominatim](https://wiki.openstreetmap.org/wiki/Nominatim)

### Video

[![asciicast](https://asciinema.org/a/lt80EQWjYLVqMK9tIsKYsqI1f)](https://asciinema.org/a/lt80EQWjYLVqMK9tIsKYsqI1f)


## License
Copyright © 2019 Miguel Soares

Distributed under the Eclipse Public License either version 1.0 or (at your option) any later version.
