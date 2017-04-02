# Outputs hello world! to the text output.
ZERO:
        0
ONE:
        1
MINUSONE:
        -1
SETUP:
        SCREENPOS OUTPUT+1 -1
START:
        ZERO HELLO -1
        TEMP TEMP CHAR
CHAR:
        HELLO TEMP OUTPUT
OUTPUT:
        TEMP 0 -1
        MINUSONE START+1 -1
        MINUSONE CHAR -1
        MINUSONE OUTPUT+1 -1
        ZERO ZERO START
        -1 -1 -1
SCREENPOS:
        -16384
TEMP:
        0
HELLO:
        "hello world!"
        0
