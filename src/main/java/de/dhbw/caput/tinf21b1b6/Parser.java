package de.dhbw.caput.tinf21b1b6;

final class Parser {

	public static RegularExpression parse( String string ){
		// TODO: Customize the code here to create a regular expression from the given string.
        // split at unions('|') & concatinations ('punkt')
        // handle bracket depth
        // recursive parse method calls
		return new RegularExpression.EmptySet( );
	}

    private String inBracket(String str) {
        for (final char c : str.toCharArray()) {
            if (c == '(') {
                // new opening bracket

            }
        }
    }
}
