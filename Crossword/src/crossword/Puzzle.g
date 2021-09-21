@skip whitespace {
    FILE ::= ">>" NAME DESCRIPTION ENTRY*;
	NAME ::= '"' String '"';
	DESCRIPTION ::= '"' String '"';
	ENTRY ::= "("  WORDNAME ","  CLUE "," DIRECTION "," ROW "," COL ")";
	WORDNAME ::= [a-z]+;
	CLUE ::= '"' String '"';
	DIRECTION ::= "DOWN" | "ACROSS";
	ROW ::= Int;
	COL ::= Int;    
}

// String ::= '"' [^"\r\n\t\\]* '"';
String ::= [^"\r\n\t\\]*;
Int ::= [0-9]+;
whitespace ::= [ \t\r\n]+;



