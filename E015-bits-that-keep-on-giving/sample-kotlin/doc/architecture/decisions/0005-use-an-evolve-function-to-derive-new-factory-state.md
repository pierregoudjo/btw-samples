# 5. Use an evolve function to derive new Factory state

Date: 2021-03-30

## Status

Accepted

## Context

Until now to derive a new state, delegate functions were used. As the Factory stored the list of events, the delagate processed the list to derive the state properties. The issue with
that technique is that the state have to be recompile from alll the events that occured, even though performance isuue ar mitigated by the use of lazy loaded delegate

## Decision

A better way could be to use an `evolve(event, state)` function that derive a new state from the previous state and the new event.


## Consequences

It makes the code very much composable and the state doesn't need to hold the events anymore making it just a data class like another one.

