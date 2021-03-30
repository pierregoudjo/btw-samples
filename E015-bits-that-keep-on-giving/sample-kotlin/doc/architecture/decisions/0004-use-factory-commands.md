# 4. Use Factory commands

Date: 2021-03-30

## Status

Accepted

## Context

Until now, to convey commands, functions were used to execute a command on the domain model. The main issue is that the caller have to know the exact signature of the command to call and introduce
coupling between the caller and the domain model.

## Decision

Instead of using functions, a better way would be to use data objects to convey the commands. A function `decide(command, state)` will be used to handle the decison process

## Consequences

All previous code using direct command call will be rewritten to use the decide command instead
