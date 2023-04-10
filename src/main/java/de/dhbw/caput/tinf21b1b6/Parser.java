package de.dhbw.caput.tinf21b1b6;

// Hier mal ein Ansatz wie es in der Theorie funktionieren sollte.. Bin mir nicht sicher warum es nicht tut, mein Gehirn ist gerade zu müde um effektiv zu debuggen :D

final class Parser {

    private static int groupCounter = 0;
    private static String[] groups = new String[1000];
    private static RegularExpression[] regex = new RegularExpression[1000];
    private static int moveJ = 0; // I'm sorry for this

	public static RegularExpression parse( String string ){
		// TODO: Customize the code here to create a regular expression from the given string.
        // split at unions('|') & concatinations ('punkt')
        // handle bracket depth
        // recursive parse method calls

        // resolve groups
        Parser.groups[0] = resolveGroups(string);

        for(String s : Parser.groups) {
            System.err.println(s);
        }

        // parse each group beginning with the deepest one. This will ensure that we always know the regex of a group that is referenced in a higher group
        RegularExpression lastRegex = null;
        for (int i = Parser.groupCounter; i >= 0; i--) {

            char[] cString = Parser.groups[i].toCharArray();
            lastRegex = null;
            for (int j = 0; j < cString.length; j++) {
                if (cString[j] == '|') {
                    // union
                    // last regex definitely exists and is the left side of the union
                    // determine right side of union
                    RegularExpression right = getRightRegex(cString, j);
                    lastRegex = new RegularExpression.Union(lastRegex, right);
                } else if (cString[j] == '·') {
                    // concatination
                    // last regex definitely exists and is the left side of the concatination
                    // determine right side of concatination
                    RegularExpression right = getRightRegex(cString, j);
                    lastRegex = new RegularExpression.Concatenation(lastRegex, right);


                // only relevant for first character
                } else if (cString[j+1] == '*') {
                    // kleene star
                    lastRegex = new RegularExpression.KleeneStar(new RegularExpression.Literal(cString[j]));
                    Parser.moveJ = 1;
                } else {
                    // literal
                    lastRegex = new RegularExpression.Literal(cString[j]);
                }

                j += Parser.moveJ;
                Parser.moveJ = 0;
            }
            Parser.regex[i] = lastRegex;

        }

		return lastRegex;
	}

    public static String resolveGroups(String string) {
        char[] cString = string.toCharArray();
        int bracketDepth = 0;
        int bracketStartIndex = 0;
        int bracketEndIndex = 0;
        String subString = "";
        for(int i = 0; i < cString.length; i++) {
            if(cString[i] == '('){
                // new opening bracket
                if (bracketDepth == 0) {
                    Parser.groupCounter++;
                    subString += "#" + Parser.groupCounter;
                    bracketStartIndex = i;
                }
                bracketDepth++;
            } else if(cString[i] == ')') {
                // new closing bracket
                bracketDepth--;
                if (bracketDepth == 0) {
                    bracketEndIndex = i;
                    // parse bracket content
                    Parser.groups[Parser.groupCounter] = resolveGroups(string.substring(bracketStartIndex+1, bracketEndIndex-1));
                }
            } else {
                subString += cString[i];
            }
        }
        return subString;
    }

    public static RegularExpression getRightRegex(char[] cString, int j) {
        RegularExpression right;
        if (j+1 < cString.length && cString[j+1] == '#') {
            // right side of union is a group (regex is already known)
            // determine group number
            int groupNumber = Integer.parseInt(String.valueOf(cString[j+2]));
            right = Parser.regex[groupNumber];
            Parser.moveJ = 2;
        } else if (j+2 < cString.length && cString[j+2] == '*') {
            // right side of union is a kleene star
            right = new RegularExpression.KleeneStar(new RegularExpression.Literal(cString[j+1]));
            Parser.moveJ = 2;
        } else {
            // right side of union is a literal
            right = new RegularExpression.Literal(cString[j+1]);
            Parser.moveJ = 1;
        }
        return right;
    }
}
