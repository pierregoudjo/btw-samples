# 3. Move codec related code into a library

Date: 2021-03-26

## Status

Accepted

## Context

It would be nice to extract reusable code written for this project as library that could be reused in other projects. Codec feature seems like a nice candidate

## Decision

Move the codec related code into 2 libs: ktcodec and ktcodec-kotlinx-serialization-json. This two libs are taking inspiration from [FsCodec](https://github.com/jet/FsCodec) from Jet.

## Consequences

The imports are just changing at this moment nothing major
