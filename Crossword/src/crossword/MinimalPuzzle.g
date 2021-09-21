@skip whitespace {
    FILE ::= ">>" NAME DESCRIPTION ENTRY*;
    NAME ::= '"' String '"';
    DESCRIPTION ::= '"' String '"';
    ENTRY ::= "("  INDEX ","  CLUE "," DIRECTION "," ROW "," COL "," LENGTH "," GUESS ")";
    INDEX ::= Int;
    CLUE ::= '"' String '"';
    DIRECTION ::= "DOWN" | "ACROSS";
    LENGTH ::= Int;
    GUESS ::= '"' String '"';
    ROW ::= Int;
    COL ::= Int;    
}

//String ::= '"' [^"\r\n\t\\]* '"';
String ::= [^"\r\n\t\\]*;
Int ::= [0-9]+;
whitespace ::= [ \t\r\n]+;