# 2. Use a pair instead of a custom record to hold the codec functions

Date: 2021-03-26

## Status

Accepted

## Context

So far, the EventCodec held the codec functions that encode Event into serializable form or decode blob into corresponding event. Even though it is a simple data struture, it is another one to maintain.

## Decision

A pair would be very suited to hold the encode and decode function. The signature of the functions are different enough so confusion would be hardly possible
## Consequences

It would be simple to destructure the pair into encode and decode function. It will also mean less custom code to maintain
