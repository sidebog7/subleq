# subleq
My experiments in subleq

This is my first attempt at a subleq interpreter.

I have split the system up into multiple parts.

Interconnect
CPU
Memory
Screen (of some kind)

Interconnect contains a list of all the components and will for each process cycle iterate through the list and call the cycle method. The ordering is determined by the getPriority method of the Unit interface. The current ordering is Memory, CPU, TextOutput.

CPU this is just a basic implementation of a subleq processor for the time being.

Memory is a simple 16k int array. Also included is a mechanism for reading in integers from a file to prep the memory. The simpleLoad just uses a scanner to read in the integers. The currently used betterLoad will scan first for tokens and then scan a second time and replace tokens with the actual memory location. Simple calculations are allowed (e.g. START+3, JUMPPOINT+1, etc).

TextOutput is currently a simple 32*20 text (int based) screen which temporarily just outputs to the console every cycle. This is stored at the top end of memory. Hopefully I will eventually figure out a way of making this configurable?
